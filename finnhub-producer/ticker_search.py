import argparse
import os
from dotenv import load_dotenv
from src.utils.functions import load_client, lookup_ticker


def main():
    """Main function to look up stock tickers based on user input and Finnhub API."""
    load_dotenv()

    # Initialize Finnhub client using the API token from environment variables
    finnhub_client = load_client(os.getenv('FINNHUB_API_TOKEN'))

    # Set up argument parser for ticker search
    parser = argparse.ArgumentParser(
        description="Get a list of tickers based on Finnhub search",
        prog="ticker_search.py",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
    )
    parser.add_argument(
        '--ticker', 
        type=str,
        help="Enter the phrase to look up a ticker"
    )

    # Parse the input arguments
    args = parser.parse_args()
    params = vars(args)

    try:
        # Look up the ticker based on user input
        result = lookup_ticker(finnhub_client, params['ticker'])
        print(result)
    except Exception as e:
        # Print any exceptions that occur
        print(f"Error: {str(e)}")


if __name__ == '__main__':
    # Run the main function when the script is executed
    main()
