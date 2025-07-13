from pydantic import BaseModel, Field
from typing import List, Optional, Any, Dict
from datetime import datetime

class StockSummaryRequest(BaseModel):
    symbol: str = Field(..., description="Stock symbol to analyze")
    model_name: str = Field(default="phi4", description="LLM model to use for analysis")
    force_refresh: bool = Field(default=False, description="Force refresh of cached data")

class StockRecommendationResponse(BaseModel):
    symbol: str
    recommendation_score: float = Field(..., ge=1.0, le=10.0, description="Recommendation score from 1 (strong sell) to 10 (strong buy)")
    recommendation_label: str = Field(..., description="Recommendation label: STRONG_SELL, SELL, HOLD, BUY, STRONG_BUY")
    summary: str = Field(..., description="AI-generated investment analysis summary")
    created_at: datetime
    last_updated: datetime

class ModelPerformanceTest(BaseModel):
    symbol: str = Field(..., description="Stock symbol to test")
    models_to_test: List[str] = Field(default=["phi4", "gemma2", "deepseek-r1", "qwen2.5"], description="List of models to test")

class PerformanceTestResult(BaseModel):
    model_name: str
    response_time_seconds: float
    summary_word_count: int
    summary_char_count: int
    summary_preview: str = Field(..., max_length=200)
    success: bool
    error_message: Optional[str] = None

class BatchGenerateRequest(BaseModel):
    symbols: List[str] = Field(..., min_items=1, max_items=50, description="List of stock symbols to analyze")
    model_name: str = Field(default="phi4", description="LLM model to use")

class HealthCheckResponse(BaseModel):
    status: str
    timestamp: str
    database: str
    available_models: List[str]
    error: Optional[str] = None

class NewsArticle(BaseModel):
    title: str
    url: str
    time_published: datetime
    authors: Optional[List[str]] = None
    summary: Optional[str] = None
    source: str
    overall_sentiment_score: Optional[float] = None
    overall_sentiment_label: Optional[str] = None

class CompanyOverview(BaseModel):
    symbol: str
    description: Optional[str] = None
    sector: Optional[str] = None
    industry: Optional[str] = None
    market_capitalization: Optional[int] = None
    pe_ratio: Optional[float] = None
    price_to_book_ratio: Optional[float] = None
    dividend_yield: Optional[float] = None
    beta: Optional[float] = None
    fifty_two_week_high: Optional[float] = None
    fifty_two_week_low: Optional[float] = None

class FinancialStatement(BaseModel):
    fiscal_date_ending: datetime
    total_revenue: Optional[int] = None
    net_income: Optional[int] = None
    gross_profit: Optional[int] = None
    operating_income: Optional[int] = None
    ebitda: Optional[int] = None

class StockAnalysisData(BaseModel):
    symbol: str
    company_overview: CompanyOverview
    income_statements: List[FinancialStatement]
    recent_news: List[NewsArticle]
    analysis_date: datetime

class RecommendationScoreBreakdown(BaseModel):
    valuation_score: float = Field(..., ge=1.0, le=10.0)
    profitability_score: float = Field(..., ge=1.0, le=10.0)
    growth_score: float = Field(..., ge=1.0, le=10.0)
    financial_health_score: float = Field(..., ge=1.0, le=10.0)
    sentiment_score: float = Field(..., ge=1.0, le=10.0)
    technical_score: float = Field(..., ge=1.0, le=10.0)
    total_score: float = Field(..., ge=1.0, le=10.0)
    weights: Dict[str, float]

class DetailedRecommendationResponse(StockRecommendationResponse):
    score_breakdown: RecommendationScoreBreakdown
    key_metrics: Dict[str, Any]
    risk_factors: List[str]
    strengths: List[str]