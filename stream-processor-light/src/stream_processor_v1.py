import json
import time
import uuid
import io
from datetime import datetime
from typing import Dict, List

import avro.schema
from avro.io import DatumReader, BinaryDecoder
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from kafka import KafkaConsumer
import pandas as pd
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

class StreamProcessor:
    def __init__(self):
        # Load Avro schema
        with open('schemas/trades.avsc', 'r') as f:
            self.schema = avro.schema.parse(f.read())
        
        # Initialize Kafka consumer
        self.consumer = KafkaConsumer(
            'market',
            bootstrap_servers=f"{os.getenv('KAFKA_SERVER')}:{os.getenv('KAFKA_PORT')}",
            group_id='stream-processor-group',
            auto_offset_reset='latest',
            enable_auto_commit=True
        )
        
        # Initialize Cassandra connection
        auth_provider = PlainTextAuthProvider(
            username=os.getenv('CASSANDRA_USERNAME', 'cassandra'),
            password=os.getenv('CASSANDRA_PASSWORD', 'cassandra')
        )
        self.cluster = Cluster(
            [os.getenv('CASSANDRA_HOST', 'cassandra')],
            auth_provider=auth_provider
        )
        self.session = self.cluster.connect('market')
        
        # Prepare Cassandra statements
        self.prepare_statements()
        
        # Initialize running averages tracking
        self.running_averages = {}
        self.last_aggregate_time = time.time()

    def prepare_statements(self):
        # Prepare statements for trades
        self.insert_trade = self.session.prepare("""
            INSERT INTO trades (uuid, symbol, trade_conditions, price, volume, 
                              trade_timestamp, ingest_timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """)
        
        # Prepare statement for running averages
        self.insert_average = self.session.prepare("""
            INSERT INTO running_averages_15_sec (uuid, symbol, price_volume_multiply, 
                                               ingest_timestamp)
            VALUES (?, ?, ?, ?)
        """)

    def process_trade(self, trade_data: Dict):
        # Extract trade data
        trade_conditions = trade_data.get('c', [])
        price = trade_data.get('p')
        symbol = trade_data.get('s')
        trade_timestamp = datetime.fromtimestamp(trade_data.get('t') / 1000)
        volume = trade_data.get('v')
        
        # Generate UUID and current timestamp
        trade_uuid = uuid.uuid4()
        ingest_timestamp = datetime.now()
        
        # Insert into trades table
        self.session.execute(self.insert_trade, (
            trade_uuid,
            symbol,
            str(trade_conditions),
            price,
            volume,
            trade_timestamp,
            ingest_timestamp
        ))
        
        # Update running averages
        if symbol not in self.running_averages:
            self.running_averages[symbol] = []
        
        self.running_averages[symbol].append({
            'price': price,
            'volume': volume,
            'timestamp': trade_timestamp
        })
        
        # Clean old data (older than 15 seconds)
        current_time = time.time()
        self.running_averages[symbol] = [
            trade for trade in self.running_averages[symbol]
            if (current_time - trade['timestamp'].timestamp()) <= 15
        ]

    def calculate_running_averages(self):
        current_time = time.time()
        if current_time - self.last_aggregate_time >= 5:  # Every 5 seconds
            for symbol, trades in self.running_averages.items():
                if trades:
                    df = pd.DataFrame(trades)
                    avg_price_volume = (df['price'] * df['volume']).mean()
                    
                    # Insert running average
                    self.session.execute(self.insert_average, (
                        uuid.uuid4(),
                        symbol,
                        avg_price_volume,
                        datetime.now()
                    ))
            
            self.last_aggregate_time = current_time

    def run(self):
        print("Starting stream processor...")
        try:
            while True:
                # Process Kafka messages
                for message in self.consumer:
                    try:
                        # Decode Avro message
                        reader = DatumReader(self.schema)
                        decoder = BinaryDecoder(io.BytesIO(message.value))
                        decoded_message = reader.read(decoder)
                        
                        # Process each trade in the message
                        for trade in decoded_message['data']:
                            self.process_trade(trade)
                        
                        # Calculate and store running averages
                        self.calculate_running_averages()
                        
                    except Exception as e:
                        print(f"Error processing message: {e}")
                        continue
                
        except KeyboardInterrupt:
            print("Shutting down...")
        finally:
            self.consumer.close()
            self.cluster.shutdown()

if __name__ == "__main__":
    processor = StreamProcessor()
    processor.run() 