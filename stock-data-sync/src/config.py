import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    # Database configuration
    DB_HOST = os.getenv('DB_HOST', 'localhost')
    DB_PORT = os.getenv('DB_PORT', '5432')
    DB_NAME = os.getenv('DB_NAME', 'gon_stock_dashboard')
    DB_USER = os.getenv('DB_USER', 'admin')
    DB_PASSWORD = os.getenv('DB_PASSWORD', 'password123!')
    
    # API configuration
    ALPHA_VANTAGE_API_KEY = os.getenv('ALPHA_VANTAGE_API_KEY')
    
    # Stock symbols to monitor
    STOCK_SYMBOLS = os.getenv('STOCKS_TICKERS', 'AAPL,MSFT,GOOGL,AMZN,TSLA,META,NVDA,NFLX,CRM,ORCL,NFLX,ADBE,AMD,INTC,PYPL,CSCO,QCOM,TXN,AMAT,PLTR').split(',')
    
    # Sync configuration
    SYNC_TYPE = os.getenv('SYNC_TYPE', 'daily')  # daily, weekly, quarterly
    
    # Rate limiting
    ALPHA_VANTAGE_RATE_LIMIT = 5  # requests per minute
    
    @classmethod
    def get_database_url(cls):
        return f"postgresql://{cls.DB_USER}:{cls.DB_PASSWORD}@{cls.DB_HOST}:{cls.DB_PORT}/{cls.DB_NAME}"
    
    @classmethod
    def validate_config(cls):
        """Validate required configuration"""
        required_vars = []
        
        if not cls.ALPHA_VANTAGE_API_KEY:
            required_vars.append('ALPHA_VANTAGE_API_KEY')
        
        if required_vars:
            raise ValueError(f"Missing required environment variables: {', '.join(required_vars)}")
        
        return True