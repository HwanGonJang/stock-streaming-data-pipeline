#!/usr/bin/env python3

import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), 'src'))

from config import Config
from api_client import AlphaVantageClient, FinnhubClient
from database import DatabaseManager
from sync_service import StockDataSyncService

def test_api_clients():
    """Test API clients"""
    print("Testing API clients...")
    
    # Test Alpha Vantage
    print("\n1. Testing Alpha Vantage API:")
    alpha_client = AlphaVantageClient()
    
    try:
        quote = alpha_client.get_daily_quote("AAPL")
        if quote:
            print(f"  ✓ Daily quote for AAPL: {quote['close_price']}")
        else:
            print("  ✗ Failed to get daily quote")
    except Exception as e:
        print(f"  ✗ Alpha Vantage error: {e}")
    
    try:
        overview = alpha_client.get_company_overview("AAPL")
        if overview:
            print(f"  ✓ Company overview for AAPL: {overview['name']}")
        else:
            print("  ✗ Failed to get company overview")
    except Exception as e:
        print(f"  ✗ Alpha Vantage company overview error: {e}")
    
    # Test Finnhub
    print("\n2. Testing Finnhub API:")
    finnhub_client = FinnhubClient()
    
    try:
        profile = finnhub_client.get_company_profile("AAPL")
        if profile:
            print(f"  ✓ Company profile for AAPL: {profile['name']}")
        else:
            print("  ✗ Failed to get company profile")
    except Exception as e:
        print(f"  ✗ Finnhub error: {e}")

def test_database_connection():
    """Test database connection"""
    print("\n3. Testing database connection:")
    
    try:
        with DatabaseManager() as db:
            # Test simple query
            result = db.execute_query("SELECT 1 as test")
            if result and result[0]['test'] == 1:
                print("  ✓ Database connection successful")
            else:
                print("  ✗ Database query failed")
    except Exception as e:
        print(f"  ✗ Database connection error: {e}")

def test_sync_service():
    """Test sync service with limited data"""
    print("\n4. Testing sync service:")
    
    try:
        # Override symbols for testing
        Config.STOCK_SYMBOLS = ["AAPL"]  # Test with just one symbol
        
        sync_service = StockDataSyncService()
        
        # Test daily sync
        print("  Testing daily sync...")
        results = sync_service.sync_daily_quotes()
        if results['success_count'] > 0:
            print(f"  ✓ Daily sync successful: {results['success_count']} symbols")
        else:
            print(f"  ✗ Daily sync failed: {results['error_count']} errors")
            if results['errors']:
                print(f"    Errors: {results['errors']}")
        
    except Exception as e:
        print(f"  ✗ Sync service error: {e}")

def main():
    """Run all tests"""
    print("Stock Data Sync - Test Suite")
    print("=" * 40)
    
    try:
        # Validate configuration
        Config.validate_config()
        print("✓ Configuration validated")
        
        # Run tests
        test_api_clients()
        test_database_connection()
        test_sync_service()
        
        print("\n" + "=" * 40)
        print("Test suite completed")
        
    except Exception as e:
        print(f"Test suite failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()