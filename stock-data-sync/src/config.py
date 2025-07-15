import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    # Database configuration
    POSTGRES_HOST = os.getenv('POSTGRES_HOST', 'localhost')
    POSTGRES_PORT = os.getenv('POSTGRES_PORT', '5432')
    POSTGRES_DATABASE = os.getenv('POSTGRES_DATABASE', 'gon_stock_dashboard')
    POSTGRES_USER = os.getenv('POSTGRES_USER', 'admin')
    POSTGRES_PASSWORD = os.getenv('POSTGRES_PASSWORD', 'password123!')
    
    # API configuration
    ALPHA_VANTAGE_API_KEY = os.getenv('ALPHA_VANTAGE_API_KEY')
    
    # Stock symbols to monitor
    STOCK_SYMBOLS = os.getenv('STOCKS_TICKERS', 'AAPL,MSFT,GOOGL,AMZN,TSLA,META,NVDA,AVGO,CRM,ORCL,NFLX,ADBE,AMD,INTC,PYPL,CSCO,QCOM,TXN,AMAT,PLTR').split(',')
    
    # Sync configuration
    SYNC_TYPE = os.getenv('SYNC_TYPE', 'daily')  # daily, weekly, quarterly
    
    # Rate limiting
    ALPHA_VANTAGE_RATE_LIMIT = 5  # requests per minute
    
    @classmethod
    def get_database_url(cls):
        return f"postgresql://{cls.POSTGRES_USER}:{cls.POSTGRES_PASSWORD}@{cls.POSTGRES_HOST}:{cls.POSTGRES_PORT}/{cls.POSTGRES_DATABASE}"
    
    @classmethod
    def validate_config(cls):
        """Validate required configuration"""
        required_vars = []
        
        if not cls.ALPHA_VANTAGE_API_KEY:
            required_vars.append('ALPHA_VANTAGE_API_KEY')
        
        if required_vars:
            raise ValueError(f"Missing required environment variables: {', '.join(required_vars)}")
        
        return True