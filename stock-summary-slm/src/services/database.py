import asyncpg
from typing import Dict, Any, Optional, List
from datetime import datetime
import logging

logger = logging.getLogger(__name__)

class DatabaseService:
    def __init__(self, pool: asyncpg.Pool):
        self.pool = pool
    
    async def get_stock_analysis_data(self, symbol: str) -> Optional[Dict[str, Any]]:
        """Get comprehensive stock data for analysis"""
        async with self.pool.acquire() as connection:
            try:
                # Get company overview
                company_query = """
                SELECT * FROM company_overview WHERE symbol = $1
                """
                company_data = await connection.fetchrow(company_query, symbol)
                
                if not company_data:
                    return None
                
                # Get latest financial statements
                income_query = """
                SELECT * FROM income_statements 
                WHERE symbol = $1 
                AND is_quarterly = TRUE
                ORDER BY fiscal_date_ending DESC 
                LIMIT 4
                """
                income_data = await connection.fetch(income_query, symbol)
                
                balance_query = """
                SELECT * FROM balance_sheets 
                WHERE symbol = $1 
                AND is_quarterly = TRUE
                ORDER BY fiscal_date_ending DESC 
                LIMIT 4
                """
                balance_data = await connection.fetch(balance_query, symbol)
                
                cashflow_query = """
                SELECT * FROM cash_flows 
                WHERE symbol = $1 
                AND is_quarterly = TRUE
                ORDER BY fiscal_date_ending DESC 
                LIMIT 4
                """
                cashflow_data = await connection.fetch(cashflow_query, symbol)
                
                # Get recent news (last 30 days)
                news_query = """
                SELECT na.title, na.summary, na.overall_sentiment_score, na.overall_sentiment_label, na.time_published
                FROM news_stocks ns
                JOIN news_articles na ON ns.news_id = na.id
                WHERE ns.symbol = $1
                ORDER BY na.time_published DESC
                LIMIT 10;
                """
                news_data = await connection.fetch(news_query, symbol)
                
                return {
                    "symbol": symbol,
                    "company_overview": dict(company_data) if company_data else {},
                    "income_statements": [dict(row) for row in income_data],
                    "balance_sheets": [dict(row) for row in balance_data],
                    "cash_flows": [dict(row) for row in cashflow_data],
                    "recent_news": [dict(row) for row in news_data],
                    "analysis_date": datetime.utcnow().isoformat()
                }
                
            except Exception as e:
                logger.error(f"Error fetching stock data for {symbol}: {str(e)}")
                raise
    
    async def save_recommendation(
        self, 
        symbol: str, 
        score: float, 
        label: str, 
        summary: str
    ) -> None:
        """Save or update stock recommendation"""
        async with self.pool.acquire() as connection:
            try:
                query = """
                INSERT INTO stock_recommendations (symbol, recommendation_score, recommendation_label, summary)
                VALUES ($1, $2, $3, $4)
                ON CONFLICT (symbol) 
                DO UPDATE SET
                    recommendation_score = EXCLUDED.recommendation_score,
                    recommendation_label = EXCLUDED.recommendation_label,
                    summary = EXCLUDED.summary,
                    last_updated = CURRENT_TIMESTAMP
                """
                await connection.execute(query, symbol, score, label, summary)
                logger.info(f"Saved recommendation for {symbol}")
                
            except Exception as e:
                logger.error(f"Error saving recommendation for {symbol}: {str(e)}")
                raise
    
    async def get_recommendation(self, symbol: str) -> Optional[Dict[str, Any]]:
        """Get existing stock recommendation"""
        async with self.pool.acquire() as connection:
            try:
                query = """
                SELECT symbol, recommendation_score, recommendation_label, summary, 
                       created_at, last_updated
                FROM stock_recommendations 
                WHERE symbol = $1
                """
                result = await connection.fetchrow(query, symbol)
                
                if result:
                    return dict(result)
                return None
                
            except Exception as e:
                logger.error(f"Error fetching recommendation for {symbol}: {str(e)}")
                raise
    
    async def get_all_symbols(self) -> List[str]:
        """Get all available stock symbols"""
        async with self.pool.acquire() as connection:
            try:
                query = "SELECT DISTINCT symbol FROM stocks ORDER BY symbol"
                results = await connection.fetch(query)
                return [row['symbol'] for row in results]
                
            except Exception as e:
                logger.error(f"Error fetching symbols: {str(e)}")
                raise
    
    async def get_outdated_recommendations(self, hours: int = 24) -> List[str]:
        """Get symbols with outdated recommendations"""
        async with self.pool.acquire() as connection:
            try:
                query = """
                SELECT symbol FROM stock_recommendations 
                WHERE last_updated < NOW() - INTERVAL '%s hours'
                OR last_updated IS NULL
                """ % hours
                
                results = await connection.fetch(query)
                return [row['symbol'] for row in results]
                
            except Exception as e:
                logger.error(f"Error fetching outdated recommendations: {str(e)}")
                raise