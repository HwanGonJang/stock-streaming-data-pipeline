from fastapi import FastAPI, HTTPException, Depends, BackgroundTasks
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import asyncio
import asyncpg
import os
from datetime import datetime, timedelta
import logging
from contextlib import asynccontextmanager
from dotenv import load_dotenv

from services.database import DatabaseService
from services.llm_service import LLMService
from services.recommendation_engine import RecommendationEngine
from models.schemas import (
    StockRecommendationResponse,
    StockSummaryRequest,
    ModelPerformanceTest,
    PerformanceTestResult
)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Database connection pool
db_pool = None

load_dotenv()

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Manage application lifecycle"""
    global db_pool
    
    # Startup
    logger.info("Starting up application...")
    
    # Initialize database connection pool
    db_url = os.getenv("DATABASE_URL")
    db_pool = await asyncpg.create_pool(db_url, min_size=5, max_size=20)
    
    # Initialize services
    app.state.db_service = DatabaseService(db_pool)
    app.state.llm_service = LLMService()
    app.state.recommendation_engine = RecommendationEngine()
    
    # Initialize LLM models
    await app.state.llm_service.initialize_models()
    
    yield
    
    # Shutdown
    logger.info("Shutting down application...")
    if db_pool:
        await db_pool.close()

app = FastAPI(
    title="Stock Analysis API",
    description="AI-powered stock analysis and recommendation system",
    version="1.0.0",
    lifespan=lifespan
)

# Dependency to get services
async def get_db_service() -> DatabaseService:
    return app.state.db_service

async def get_llm_service() -> LLMService:
    return app.state.llm_service

async def get_recommendation_engine() -> RecommendationEngine:
    return app.state.recommendation_engine

@app.get("/", response_class=HTMLResponse)
async def root():
    """Root endpoint with basic HTML interface"""
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <title>Stock Analysis API</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 40px; }
            .container { max-width: 800px; margin: 0 auto; }
            .endpoint { background: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }
            .method { color: #007bff; font-weight: bold; }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Stock Analysis API</h1>
            <p>AI-powered stock analysis and recommendation system</p>
            
            <h2>Available Endpoints:</h2>
            
            <div class="endpoint">
                <span class="method">POST</span> /generate-summary/{symbol}
                <p>Generate AI-powered stock analysis summary</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> /recommendation/{symbol}
                <p>Get stock recommendation and summary</p>
            </div>
            
            <div class="endpoint">
                <span class="method">POST</span> /test-models
                <p>Test and compare LLM model performance</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> /health
                <p>Health check endpoint</p>
            </div>
            
            <div class="endpoint">
                <span class="method">GET</span> /docs
                <p>Interactive API documentation</p>
            </div>
        </div>
    </body>
    </html>
    """

@app.post("/generate-summary/{symbol}")
async def generate_summary(
    symbol: str,
    background_tasks: BackgroundTasks,
    model_name: str = "phi3:mini",
    db_service: DatabaseService = Depends(get_db_service),
    llm_service: LLMService = Depends(get_llm_service),
    recommendation_engine: RecommendationEngine = Depends(get_recommendation_engine)
):
    """Generate AI-powered stock analysis summary"""
    try:
        symbol = symbol.upper()
        
        # Get stock data from database
        stock_data = await db_service.get_stock_analysis_data(symbol)
        if not stock_data:
            raise HTTPException(status_code=404, detail=f"Stock data not found for {symbol}")
        
        # Calculate recommendation score and label
        recommendation_score, recommendation_label = await recommendation_engine.calculate_recommendation(stock_data)
        
        # Generate summary using LLM
        summary = await llm_service.generate_summary(stock_data, model_name)
        
        # Save recommendation to database
        await db_service.save_recommendation(
            symbol=symbol,
            score=recommendation_score,
            label=recommendation_label,
            summary=summary
        )
        
        return {
            "symbol": symbol,
            "recommendation_score": recommendation_score,
            "recommendation_label": recommendation_label,
            "summary": summary,
            "model_used": model_name,
            "generated_at": datetime.utcnow().isoformat()
        }
        
    except Exception as e:
        logger.error(f"Error generating summary for {symbol}: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/recommendation/{symbol}", response_model=StockRecommendationResponse)
async def get_recommendation(
    symbol: str,
    db_service: DatabaseService = Depends(get_db_service)
):
    """Get existing stock recommendation and summary"""
    try:
        symbol = symbol.upper()
        recommendation = await db_service.get_recommendation(symbol)
        
        if not recommendation:
            raise HTTPException(
                status_code=404, 
                detail=f"No recommendation found for {symbol}. Generate one first using /generate-summary/{symbol}"
            )
        
        return recommendation
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error retrieving recommendation for {symbol}: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/test-models", response_model=List[PerformanceTestResult])
async def test_model_performance(
    test_request: ModelPerformanceTest,
    llm_service: LLMService = Depends(get_llm_service),
    db_service: DatabaseService = Depends(get_db_service)
):
    """Test and compare performance of different LLM models"""
    try:
        symbol = test_request.symbol.upper()
        
        # Get stock data
        stock_data = await db_service.get_stock_analysis_data(symbol)
        if not stock_data:
            raise HTTPException(status_code=404, detail=f"Stock data not found for {symbol}")
        
        results = []
        
        for model_name in test_request.models_to_test:
            try:
                start_time = datetime.utcnow()
                
                # Generate summary
                summary = await llm_service.generate_summary(stock_data, model_name)
                
                end_time = datetime.utcnow()
                response_time = (end_time - start_time).total_seconds()
                
                # Basic quality metrics
                word_count = len(summary.split())
                char_count = len(summary)
                
                results.append(PerformanceTestResult(
                    model_name=model_name,
                    response_time_seconds=response_time,
                    summary_word_count=word_count,
                    summary_char_count=char_count,
                    summary_preview=summary[:200] + "..." if len(summary) > 200 else summary,
                    success=True,
                    error_message=None
                ))
                
            except Exception as e:
                results.append(PerformanceTestResult(
                    model_name=model_name,
                    response_time_seconds=0,
                    summary_word_count=0,
                    summary_char_count=0,
                    summary_preview="",
                    success=False,
                    error_message=str(e)
                ))
        
        return results
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error testing models: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/batch-generate")
async def batch_generate_summaries(
    background_tasks: BackgroundTasks,
    model_name: str = "phi3:mini",
    db_service: DatabaseService = Depends(get_db_service),
    llm_service: LLMService = Depends(get_llm_service),
    recommendation_engine: RecommendationEngine = Depends(get_recommendation_engine)
):
    """Generate summaries for multiple stocks in background"""
    
    STOCK_SYMBOLS = os.getenv('STOCKS_TICKERS', 'AAPL,MSFT,GOOGL,AMZN,TSLA,META,NVDA,AVGO,CRM,ORCL,NFLX,ADBE,AMD,INTC,PYPL,CSCO,QCOM,TXN,AMAT,PLTR').split(',')

    async def process_batch():
        for symbol in STOCK_SYMBOLS:
            try:
                await generate_summary(symbol, background_tasks, model_name, db_service, llm_service, recommendation_engine)
                logger.info(f"Generated summary for {symbol}")
            except Exception as e:
                logger.error(f"Failed to generate summary for {symbol}: {str(e)}")
    
    background_tasks.add_task(process_batch)
    
    return {
        "message": f"Batch processing started for {len(STOCK_SYMBOLS)} stocks",
        "symbols": [s.upper() for s in STOCK_SYMBOLS],
        "model": model_name
    }

@app.get("/health")
async def health_check(
    llm_service: LLMService = Depends(get_llm_service)
):
    """Health check endpoint"""
    try:
        # Check database connection
        async with db_pool.acquire() as connection:
            await connection.fetchval("SELECT 1")
        
        # Check LLM service
        available_models = llm_service.get_available_models()
        
        return {
            "status": "healthy",
            "timestamp": datetime.utcnow().isoformat(),
            "database": "connected",
            "available_models": available_models
        }
        
    except Exception as e:
        return {
            "status": "unhealthy",
            "timestamp": datetime.utcnow().isoformat(),
            "error": str(e)
        }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)