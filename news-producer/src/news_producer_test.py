import os
import json
import time
import websocket
from dotenv import load_dotenv
from datetime import datetime


class NewsWebSocketTest:
    """Simple test class to receive and print news data from Finnhub WebSocket."""

    def __init__(self):
        """Initialize the test class and start WebSocket connection."""
        self._load_environment_variables()
        self._start_websocket()

    def _load_environment_variables(self):
        """Load and validate required environment variables."""
        load_dotenv()

        if not os.getenv('FINNHUB_API_TOKEN_NEWS'):
            raise ValueError('Missing required environment variable: FINNHUB_API_TOKEN_NEWS')

        self.api_token = os.getenv('FINNHUB_API_TOKEN_NEWS')
        self.tickers = ["AAPL", "MSFT", "GOOGL", "AMZN", "META", "NVDA", "TSLA", "AVGO", "CRM", "ORCL"]
        print(f"Monitoring news for tickers: {self.tickers}")

    def _start_websocket(self):
        """Start WebSocket connection to Finnhub."""
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
        """Process and print incoming WebSocket messages.

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
                
                # Print news details in a readable format
                print("\n" + "="*80)
                print(f"Symbol: {news_item.get('related', 'N/A')}")
                print(f"Headline: {news_item.get('headline', 'N/A')}")
                print(f"Category: {news_item.get('category', 'N/A')}")
                print(f"Source: {news_item.get('source', 'N/A')}")
                print(f"Datetime: {news_datetime}")
                print(f"Summary: {news_item.get('summary', 'N/A')}")
                print(f"URL: {news_item.get('url', 'N/A')}")
                print("="*80 + "\n")
            
        except Exception as e:
            print(f'Error processing message: {e}')

    def on_error(self, ws, error):
        """Handle WebSocket errors.

        Args:
            ws (WebSocketApp): The WebSocket instance.
            error (Exception): The error that occurred.
        """
        print(f'WebSocket error: {error}')

    def on_close(self, ws):
        """Handle WebSocket closure.

        Args:
            ws (WebSocketApp): The WebSocket instance.
        """
        print('### WebSocket closed ###')
        time.sleep(5)  # Wait before reconnecting
        self._start_websocket()

    def on_open(self, ws):
        """Subscribe to news for stock tickers when WebSocket connection opens.

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
    print("Starting WebSocket test...")
    print("Press Ctrl+C to stop")
    NewsWebSocketTest() 