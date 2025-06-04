import json
import time
import uuid
import io
import threading
from datetime import datetime, date
from typing import Dict, List
from queue import Queue, Empty

import avro.schema
from avro.io import DatumReader, BinaryDecoder
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from kafka import KafkaConsumer
import pandas as pd
import redis
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
        
        # Initialize Redis connection
        self.redis_client = redis.Redis(
            host=os.getenv('REDIS_HOST', 'redis'),
            port=int(os.getenv('REDIS_PORT', 6379)),
            decode_responses=True
        )
        
        # Prepare Cassandra statements
        self.prepare_statements()
        
        # Initialize running averages tracking
        self.running_averages = {}
        self.last_aggregate_time = time.time()
        
        # Thread-safe batch processing for Redis updates
        self.batch_queue = Queue(maxsize=10000)  # Thread-safe queue
        self.batch_size = int(os.getenv('BATCH_SIZE', 100))  # 배치 크기
        self.batch_interval = int(os.getenv('BATCH_INTERVAL', 10))  # 10초마다 배치 처리
        
        # Daily aggregation persistence
        self.last_daily_persist_time = time.time()
        self.daily_persist_interval = int(os.getenv('DAILY_PERSIST_INTERVAL', 300))  # 5분마다 Cassandra에 저장
        
        # Start background thread for batch processing
        self.batch_thread = threading.Thread(target=self.batch_processor, daemon=True)
        self.batch_thread.start()
        
        # Start background thread for daily aggregation persistence
        self.daily_persist_thread = threading.Thread(target=self.daily_persist_processor, daemon=True)
        self.daily_persist_thread.start()
        
        print("StreamProcessor initialized with Redis daily aggregation")

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
        
        # Prepare statement for daily aggregates
        self.upsert_daily_aggregate = self.session.prepare("""
            INSERT INTO daily_aggregates (symbol, trade_date, total_volume, total_amount, 
                                        trade_count, first_trade_time, last_trade_time, 
                                        created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
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
        
        # Add to thread-safe queue for Redis processing
        try:
            trade_data = {
                'symbol': symbol,
                'price': price,
                'volume': volume,
                'trade_timestamp': trade_timestamp,
                'amount': price * volume
            }
            self.batch_queue.put_nowait(trade_data)
        except Exception as e:
            print(f"Warning: Batch queue full, dropping trade data: {e}")
        
        # Update running averages (기존 로직 유지)
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

    def batch_processor(self):
        """배치로 Redis 업데이트를 처리하는 백그라운드 스레드 (Thread-safe)"""
        batch_trades = []
        last_batch_time = time.time()
        
        while True:
            try:
                current_time = time.time()
                
                # Queue에서 데이터 수집 (non-blocking)
                try:
                    while len(batch_trades) < self.batch_size:
                        # 0.1초 타임아웃으로 큐에서 데이터 가져오기
                        trade_data = self.batch_queue.get(timeout=0.1)
                        batch_trades.append(trade_data)
                        self.batch_queue.task_done()
                except Empty:
                    # 큐가 비어있거나 타임아웃
                    pass
                
                # 배치 크기 또는 시간 간격에 따라 처리
                should_process = (
                    len(batch_trades) >= self.batch_size or 
                    (len(batch_trades) > 0 and current_time - last_batch_time >= self.batch_interval)
                )
                
                if should_process:
                    # 현재 배치 처리
                    self.update_redis_aggregates(batch_trades)
                    batch_trades = []  # 배치 초기화
                    last_batch_time = current_time
                
                # 배치가 없으면 잠시 대기
                if not batch_trades:
                    time.sleep(0.1)
                
            except Exception as e:
                print(f"Error in batch processor: {e}")
                time.sleep(5)

    def update_redis_aggregates(self, trades_batch: List[Dict]):
        """Redis에 일별 집계 데이터 업데이트"""
        try:
            pipe = self.redis_client.pipeline()
            
            for trade in trades_batch:
                symbol = trade['symbol']
                trade_date = trade['trade_timestamp'].date().isoformat()
                amount = trade['amount']
                volume = trade['volume']
                
                # Redis 키 패턴: daily_agg:{symbol}:{date}
                key = f"daily_agg:{symbol}:{trade_date}"
                
                # Hash로 저장 (total_volume, total_amount, trade_count, first_trade, last_trade)
                pipe.hincrbyfloat(key, 'total_volume', volume)
                pipe.hincrbyfloat(key, 'total_amount', amount)
                pipe.hincrby(key, 'trade_count', 1)
                
                # 첫 거래 시간과 마지막 거래 시간 업데이트
                trade_timestamp_str = trade['trade_timestamp'].isoformat()
                
                # 첫 거래 시간 설정 (존재하지 않으면 설정)
                pipe.hsetnx(key, 'first_trade_time', trade_timestamp_str)
                
                # 마지막 거래 시간은 항상 업데이트
                pipe.hset(key, 'last_trade_time', trade_timestamp_str)
                
                # TTL 설정 (30일)
                pipe.expire(key, 30 * 24 * 3600)
            
            pipe.execute()
            print(f"Updated Redis aggregates for {len(trades_batch)} trades")
            
        except Exception as e:
            print(f"Error updating Redis aggregates: {e}")

    def daily_persist_processor(self):
        """일별 집계 데이터를 Cassandra에 저장하는 백그라운드 스레드"""
        while True:
            try:
                current_time = time.time()
                
                if current_time - self.last_daily_persist_time >= self.daily_persist_interval:
                    self.persist_daily_aggregates_to_cassandra()
                    self.last_daily_persist_time = current_time
                
                time.sleep(30)  # 30초마다 체크
                
            except Exception as e:
                print(f"Error in daily persist processor: {e}")
                time.sleep(60)

    def persist_daily_aggregates_to_cassandra(self):
        """Redis의 일별 집계 데이터를 Cassandra에 저장"""
        try:
            # Redis에서 모든 일별 집계 키 조회
            keys = self.redis_client.keys('daily_agg:*')
            
            for key in keys:
                try:
                    # 키 파싱: daily_agg:{symbol}:{date}
                    _, symbol, date_str = key.split(':', 2)
                    trade_date = datetime.strptime(date_str, '%Y-%m-%d').date()
                    
                    # Redis에서 집계 데이터 조회
                    agg_data = self.redis_client.hgetall(key)
                    
                    if not agg_data:
                        continue
                    
                    total_volume = float(agg_data.get('total_volume', 0))
                    total_amount = float(agg_data.get('total_amount', 0))
                    trade_count = int(agg_data.get('trade_count', 0))
                    first_trade_time = datetime.fromisoformat(agg_data.get('first_trade_time'))
                    last_trade_time = datetime.fromisoformat(agg_data.get('last_trade_time'))
                    
                    # Cassandra에 upsert
                    self.session.execute(self.upsert_daily_aggregate, (
                        symbol,
                        trade_date,
                        total_volume,
                        total_amount,
                        trade_count,
                        first_trade_time,
                        last_trade_time,
                        datetime.now(),  # created_at
                        datetime.now()   # updated_at
                    ))
                    
                except Exception as e:
                    print(f"Error processing key {key}: {e}")
                    continue
            
            print(f"Persisted {len(keys)} daily aggregates to Cassandra")
            
        except Exception as e:
            print(f"Error persisting daily aggregates: {e}")

    def calculate_running_averages(self):
        """기존 running averages 계산 로직 유지"""
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

    def get_daily_aggregate(self, symbol: str, trade_date: date = None) -> Dict:
        """특정 심볼의 일별 집계 데이터 조회 (Redis 우선, 없으면 Cassandra)"""
        if trade_date is None:
            trade_date = date.today()
        
        # Redis에서 먼저 조회
        key = f"daily_agg:{symbol}:{trade_date.isoformat()}"
        redis_data = self.redis_client.hgetall(key)
        
        if redis_data:
            return {
                'symbol': symbol,
                'trade_date': trade_date,
                'total_volume': float(redis_data.get('total_volume', 0)),
                'total_amount': float(redis_data.get('total_amount', 0)),
                'trade_count': int(redis_data.get('trade_count', 0)),
                'first_trade_time': redis_data.get('first_trade_time'),
                'last_trade_time': redis_data.get('last_trade_time'),
                'source': 'redis'
            }
        
        # Redis에 없으면 Cassandra에서 조회
        query = "SELECT * FROM daily_aggregates WHERE symbol = ? AND trade_date = ?"
        result = self.session.execute(query, (symbol, trade_date))
        row = result.one()
        
        if row:
            return {
                'symbol': row.symbol,
                'trade_date': row.trade_date,
                'total_volume': row.total_volume,
                'total_amount': row.total_amount,
                'trade_count': row.trade_count,
                'first_trade_time': row.first_trade_time.isoformat() if row.first_trade_time else None,
                'last_trade_time': row.last_trade_time.isoformat() if row.last_trade_time else None,
                'source': 'cassandra'
            }
        
        return None

    def run(self):
        print("Starting enhanced stream processor with Redis daily aggregation...")
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
                        
                        # Calculate and store running averages (기존 로직)
                        # self.calculate_running_averages()
                        
                    except Exception as e:
                        print(f"Error processing message: {e}")
                        continue
                
        except KeyboardInterrupt:
            print("Shutting down...")
        finally:
            self.consumer.close()
            self.cluster.shutdown()
            self.redis_client.close()

if __name__ == "__main__":
    processor = StreamProcessor()
    processor.run()