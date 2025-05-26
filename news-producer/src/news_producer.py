import os
import ast
import json
import time
import uuid
import websocket
from datetime import datetime
from dotenv import load_dotenv
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider


class NewsProducer:
    """Handles streaming news data from Finnhub WebSocket and storing in Cassandra."""

    def __init__(self):
        """Initializes the news producer, loads environment variables, and starts the WebSocket connection."""
        self._load_environment_variables()
        self._initialize_services()
        self._start_websocket()

    def _load_environment_variables(self):
        """Loads environment variables from .env file and validates required variables.

        Raises:
            ValueError: If any required environment variable is missing.
            ValueError: If FINNHUB_STOCKS_TICKERS is not a valid list format.
        """
        load_dotenv()  # Load environment variables from .env file

        required_vars = [
            'FINNHUB_API_TOKEN_NEWS', 'CASSANDRA_HOST', 'CASSANDRA_USERNAME',
            'CASSANDRA_PASSWORD', 'FINNHUB_STOCKS_TICKERS'
        ]

        for var in required_vars:
            if not os.getenv(var):
                raise ValueError(f'Missing required environment variable: {var}')

        self.api_token = os.getenv('FINNHUB_API_TOKEN_NEWS')
        self.cassandra_host = os.getenv('CASSANDRA_HOST')
        self.cassandra_username = os.getenv('CASSANDRA_USERNAME')
        self.cassandra_password = os.getenv('CASSANDRA_PASSWORD')

        # Convert tickers string to list safely
        try:
            self.tickers = ast.literal_eval(os.getenv('FINNHUB_STOCKS_TICKERS'))
            if not isinstance(self.tickers, list):
                raise ValueError('FINNHUB_STOCKS_TICKERS must be a list')
            print(f"Monitoring news for tickers: {self.tickers}")
        except (SyntaxError, ValueError):
            raise ValueError('Invalid format for FINNHUB_STOCKS_TICKERS. Must be a list.')

    def _initialize_services(self):
        """Initializes Cassandra connection and prepares statements."""
        # Initialize Cassandra connection
        auth_provider = PlainTextAuthProvider(
            username=self.cassandra_username,
            password=self.cassandra_password
        )
        self.cluster = Cluster(
            [self.cassandra_host],
            auth_provider=auth_provider
        )
        self.session = self.cluster.connect('market')
        
        # Prepare statement for news
        self.insert_news = self.session.prepare("""
            INSERT INTO news (uuid, symbol, category, datetime, headline, 
                            news_id, image, source, summary, url, ingest_timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)

    def _start_websocket(self):
        """Starts the WebSocket connection to Finnhub."""
        websocket.enableTrace(True)
        print(self.api_token)
        self.ws = websocket.WebSocketApp(
            f'wss://ws.finnhub.io?token={self.api_token}',
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close
        )
        self.ws.on_open = self.on_open
        self.ws.run_forever()

    def on_message(self, ws, message):
        """Processes incoming WebSocket messages and stores them in Cassandra.

        Args:
            ws (WebSocketApp): The WebSocket instance.
            message (str): The received message in JSON format.
        """
        try:
            message_data = json.loads(message)
            
            # Skip if not a news message
            if message_data.get('type') != 'news':
                return
            
            # Process each news item
            for news_item in message_data.get('data', []):
                # Convert datetime from milliseconds to timestamp
                news_datetime = datetime.fromtimestamp(news_item.get('datetime', 0) / 1000)
                
                # Insert into Cassandra
                self.session.execute(self.insert_news, (
                    uuid.uuid4(),  # Generate new UUID
                    news_item.get('related', ''),  # symbol
                    news_item.get('category', ''),
                    news_datetime,
                    news_item.get('headline', ''),
                    news_item.get('id', 0),
                    news_item.get('image', ''),
                    news_item.get('source', ''),
                    news_item.get('summary', ''),
                    news_item.get('url', ''),
                    datetime.now()  # ingest_timestamp
                ))
                
                print(f"Stored news for {news_item.get('related')}: {news_item.get('headline')}")
            
        except Exception as e:
            print(f'Error processing message: {e}')

    def on_error(self, ws, error):
        """Handles WebSocket errors.

        Args:
            ws (WebSocketApp): The WebSocket instance.
            error (Exception): The error that occurred.
        """
        print(f'WebSocket error: {error}')

    def on_close(self, ws):
        """Handles WebSocket closure.

        Args:
            ws (WebSocketApp): The WebSocket instance.
        """
        print('### WebSocket closed ###')
        time.sleep(5)  # Wait before reconnecting
        self._start_websocket()

    def on_open(self, ws):
        """Subscribes to news for stock tickers when WebSocket connection opens.

        Args:
            ws (WebSocketApp): The WebSocket instance.
        """
        for ticker in self.tickers:
            subscribe_msg = {
                "type": "subscribe-news",
                "symbol": ticker
            }
            ws.send(json.dumps(subscribe_msg))
            print(f'Subscribed to news for {ticker}')


if __name__ == '__main__':
    NewsProducer()