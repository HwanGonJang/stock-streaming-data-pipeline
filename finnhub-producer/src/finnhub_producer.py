import os
import ast
import json
import time
import threading
import websocket
from dotenv import load_dotenv
from utils.functions import load_client, load_producer, ticker_validator, avro_encode, load_avro_schema


class FinnhubProducer:
    """Handles streaming stock data from Finnhub WebSocket and publishing to Kafka."""

    def __init__(self):
        """Initializes the Finnhub producer, loads environment variables, and starts the WebSocket connection."""
        self._load_environment_variables()
        self._initialize_services()
        self._initialize_throttling()
        self._start_websocket()

    def _load_environment_variables(self):
        """Loads environment variables from .env file and validates required variables.

        Raises:
            ValueError: If any required environment variable is missing.
            ValueError: If FINNHUB_STOCKS_TICKERS is not a valid list format.
        """
        load_dotenv()  # Load environment variables from .env file

        required_vars = [
            'FINNHUB_API_TOKEN', 'KAFKA_SERVER', 'KAFKA_PORT',
            'FINNHUB_STOCKS_TICKERS', 'KAFKA_TOPIC_NAME', 'FINNHUB_VALIDATE_TICKERS'
        ]

        for var in required_vars:
            if not os.getenv(var):
                raise ValueError(f'Missing required environment variable: {var}')

        self.api_token = os.getenv('FINNHUB_API_TOKEN')
        self.kafka_server = os.getenv('KAFKA_SERVER')
        self.kafka_port = os.getenv('KAFKA_PORT')
        self.kafka_topic = os.getenv('KAFKA_TOPIC_NAME')
        self.validate_tickers = os.getenv('FINNHUB_VALIDATE_TICKERS') == '1'

        # Convert tickers string to list safely
        try:
            self.tickers = ast.literal_eval(os.getenv('FINNHUB_STOCKS_TICKERS'))
            if not isinstance(self.tickers, list):
                raise ValueError('FINNHUB_STOCKS_TICKERS must be a list')
        except (SyntaxError, ValueError):
            raise ValueError('Invalid format for FINNHUB_STOCKS_TICKERS. Must be a list.')

    def _initialize_services(self):
        """Initializes Finnhub client, Kafka producer, and Avro schema."""
        self.finnhub_client = load_client(self.api_token)
        self.producer = load_producer(f'{self.kafka_server}:{self.kafka_port}')
        self.avro_schema = load_avro_schema('src/schemas/trades.avsc')

    def _initialize_throttling(self):
        """Initializes throttling mechanism to limit data processing to once per second."""
        self.running = True
        self.last_processed_time = 0
        self.message_buffer = []
        self.buffer_lock = threading.Lock()
        self.processing_thread = threading.Thread(target=self._process_buffer, daemon=True)
        self.processing_thread.start()

    def _process_buffer(self):
        """Background thread that processes messages from the buffer once per second."""
        while self.running:
            current_time = time.time()
            # Wait until at least 1 second has passed since the last processing
            if current_time - self.last_processed_time >= 1.0 and self.message_buffer:
                with self.buffer_lock:
                    # Get the latest message only (most recent data)
                    latest_message = self.message_buffer[-1]
                    self.message_buffer.clear()
                
                try:
                    message_data = json.loads(latest_message)
                    avro_message = avro_encode(
                        {
                            'data': message_data.get('data', []),
                            'type': message_data.get('type', '')
                        },
                        self.avro_schema
                    )
                    self.producer.send(self.kafka_topic, avro_message)
                    print(f"Processed message at {time.strftime('%H:%M:%S')}")
                except Exception as e:
                    print(f'Error processing message: {e}')
                
                self.last_processed_time = current_time
            
            # Sleep briefly to prevent CPU hogging
            time.sleep(0.1)

    def _start_websocket(self):
        """Starts the WebSocket connection to Finnhub."""
        websocket.enableTrace(True)
        self.ws = websocket.WebSocketApp(
            f'wss://ws.finnhub.io?token={self.api_token}',
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close
        )
        self.ws.on_open = self.on_open
        self.ws.run_forever()

    def on_message(self, ws, message):
        """Buffers incoming WebSocket messages for throttled processing.

        Args:
            ws (WebSocketApp): The WebSocket instance.
            message (str): The received message in JSON format.
        """
        with self.buffer_lock:
            self.message_buffer.append(message)

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
        self.running = False

    def on_open(self, ws):
        """Subscribes to stock tickers when WebSocket connection opens.

        Args:
            ws (WebSocketApp): The WebSocket instance.
        """
        for ticker in self.tickers:
            if self.validate_tickers:
                if ticker_validator(self.finnhub_client, ticker):
                    self.ws.send(json.dumps({'type': 'subscribe', 'symbol': ticker}))
                    print(f'Subscription for {ticker} succeeded')
                else:
                    print(f'Subscription for {ticker} failed - ticker not found')
            else:
                self.ws.send(json.dumps({'type': 'subscribe', 'symbol': ticker}))


if __name__ == '__main__':
    FinnhubProducer()