from langchain_community.llms import Ollama
from langchain.prompts import PromptTemplate
from langchain.schema import Document
from langchain_community.vectorstores import FAISS
from langchain_community.embeddings import OllamaEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from typing import Dict, Any, List
import json
import logging
import asyncio
from datetime import datetime

logger = logging.getLogger(__name__)

class LLMService:
    def __init__(self, ollama_base_url: str = "http://localhost:11434"):
        self.ollama_base_url = ollama_base_url
        self.models = {
            # "phi4": None,
            # "gemma2": None,
            # "deepseek-r1": None,
            # "qwen2.5": None
            "phi3:mini": None
            # "nomic-embed-text": None
        }
        self.embeddings = None
        
        # Analysis prompt template
        self.prompt_template = PromptTemplate(
            input_variables=["company_data", "financial_data", "news_sentiment", "market_metrics"],
            template="""
You are a professional stock analyst. Analyze the following company data and provide a comprehensive investment summary.

COMPANY INFORMATION:
{company_data}

FINANCIAL DATA:
{financial_data}

NEWS SENTIMENT:
{news_sentiment}

MARKET METRICS:
{market_metrics}

Please provide a professional investment analysis summary with the following structure:

## Executive Summary
[2-3 sentences highlighting the key investment thesis and overall outlook]

## Financial Performance
[Analysis of revenue growth, profitability trends, and key financial metrics]

## Strengths
[Key competitive advantages and positive factors]

## Risks & Concerns
[Main risks and potential challenges]

## Market Position
[Industry context and competitive positioning]

## Recent Developments
[Analysis of recent news and market sentiment]

## Conclusion
[Final assessment and key takeaways for investors]

Keep the analysis objective, professional, and suitable for web display. Use clear formatting and avoid overly technical jargon.
"""
        )
    
    async def initialize_models(self):
        """Initialize all LLM models"""
        try:
            # Initialize embeddings
            self.embeddings = OllamaEmbeddings(
                base_url=self.ollama_base_url,
                model="phi3:mini"
            )
            
            # Initialize LLM models
            for model_name in self.models.keys():
                try:
                    self.models[model_name] = Ollama(
                        base_url=self.ollama_base_url,
                        model=model_name,
                        temperature=0.1,  # Low temperature for consistent analysis
                        num_ctx=2048  # Max tokens for summary
                    )
                    logger.info(f"Initialized model: {model_name}")
                except Exception as e:
                    logger.warning(f"Failed to initialize model {model_name}: {str(e)}")
                    self.models[model_name] = None
            
        except Exception as e:
            logger.error(f"Error initializing LLM service: {str(e)}")
            raise
    
    def get_available_models(self) -> List[str]:
        """Get list of available models"""
        return [name for name, model in self.models.items() if model is not None]
    
    def _prepare_company_data(self, data: Dict[str, Any]) -> str:
        """Prepare company overview data for analysis"""
        overview = data.get("company_overview", {})
        
        company_info = f"""
Symbol: {data.get('symbol', 'N/A')}
Company Description: {overview.get('description', 'N/A')}
Sector: {overview.get('sector', 'N/A')}
Industry: {overview.get('industry', 'N/A')}
Country: {overview.get('country', 'N/A')}
Market Cap: ${overview.get('market_capitalization', 0):,} if overview.get('market_capitalization') else 'N/A'
"""
        return company_info.strip()
    
    def _prepare_financial_data(self, data: Dict[str, Any]) -> str:
        """Prepare financial statements data for analysis"""
        overview = data.get("company_overview", {})
        income_statements = data.get("income_statements", [])
        balance_sheets = data.get("balance_sheets", [])
        
        # Key financial metrics
        financial_info = f"""
VALUATION METRICS:
- P/E Ratio: {overview.get('pe_ratio', 'N/A')}
- P/B Ratio: {overview.get('price_to_book_ratio', 'N/A')}
- P/S Ratio: {overview.get('price_to_sales_ratio_ttm', 'N/A')}
- EV/EBITDA: {overview.get('ev_to_ebitda', 'N/A')}

PROFITABILITY:
- Profit Margin: {overview.get('profit_margin', 'N/A')}
- Operating Margin: {overview.get('operating_margin_ttm', 'N/A')}
- ROE: {overview.get('return_on_equity_ttm', 'N/A')}
- ROA: {overview.get('return_on_assets_ttm', 'N/A')}

GROWTH:
- Quarterly Revenue Growth (YoY): {overview.get('quarterly_revenue_growth_yoy', 'N/A')}
- Quarterly Earnings Growth (YoY): {overview.get('quarterly_earnings_growth_yoy', 'N/A')}

FINANCIAL HEALTH:
- Beta: {overview.get('beta', 'N/A')}
- Debt-to-Equity: {self._calculate_debt_to_equity(balance_sheets)}
- Current Ratio: {self._calculate_current_ratio(balance_sheets)}
"""
        
        # Recent revenue and earnings trend
        if income_statements:
            revenue_trend = []
            earnings_trend = []
            
            for stmt in income_statements[:4]:  # Last 4 quarters
                revenue_trend.append(stmt.get('total_revenue', 0))
                earnings_trend.append(stmt.get('net_income', 0))
            
            financial_info += f"""
RECENT PERFORMANCE TREND:
- Revenue (Last 4 Quarters): {[f'${r/1000000:.1f}M' if r else 'N/A' for r in revenue_trend]}
- Net Income (Last 4 Quarters): {[f'${e/1000000:.1f}M' if e else 'N/A' for e in earnings_trend]}
"""
        
        return financial_info.strip()
    
    def _prepare_news_sentiment(self, data: Dict[str, Any]) -> str:
        """Prepare news sentiment analysis"""
        news_data = data.get("recent_news", [])
        
        if not news_data:
            return "No recent news data available."
        
        # Calculate average sentiment
        sentiments = [item.get('overall_sentiment_score', 0) for item in news_data if item.get('overall_sentiment_score')]
        avg_sentiment = sum(sentiments) / len(sentiments) if sentiments else 0
        
        # Get sentiment labels
        sentiment_labels = [item.get('overall_sentiment_label', '') for item in news_data if item.get('overall_sentiment_label')]
        
        news_info = f"""
RECENT NEWS SENTIMENT (Last 30 days):
- Number of Articles: {len(news_data)}
- Average Sentiment Score: {avg_sentiment:.2f} (Range: -1 to 1)
- Sentiment Distribution: {dict(zip(*[sentiment_labels, [sentiment_labels.count(label) for label in set(sentiment_labels)]]))}

RECENT HEADLINES:
"""
        
        # Add top 5 recent headlines
        for i, article in enumerate(news_data[:5]):
            title = article.get('title', 'N/A')
            sentiment = article.get('overall_sentiment_label', 'N/A')
            news_info += f"- {title} (Sentiment: {sentiment})\n"
        
        return news_info.strip()
    
    def _prepare_market_metrics(self, data: Dict[str, Any]) -> str:
        """Prepare market metrics and technical indicators"""
        overview = data.get("company_overview", {})
        
        market_info = f"""
STOCK PERFORMANCE:
- 52-Week High: ${overview.get('fifty_two_week_high', 'N/A')}
- 52-Week Low: ${overview.get('fifty_two_week_low', 'N/A')}
- 50-Day MA: ${overview.get('fifty_day_moving_average', 'N/A')}
- 200-Day MA: ${overview.get('two_hundred_day_moving_average', 'N/A')}

ANALYST METRICS:
- Analyst Target Price: ${overview.get('analyst_target_price', 'N/A')}
- Forward P/E: {overview.get('forward_pe', 'N/A')}

DIVIDEND INFO:
- Dividend Yield: {overview.get('dividend_yield', 'N/A')}
- Dividend Per Share: ${overview.get('dividend_per_share', 'N/A')}
- Payout Ratio: {overview.get('payout_ratio', 'N/A')}

SHARE STRUCTURE:
- Shares Outstanding: {overview.get('shares_outstanding', 'N/A'):,} if overview.get('shares_outstanding') else 'N/A'
- Float: {overview.get('shares_float', 'N/A'):,} if overview.get('shares_float') else 'N/A'
- Short Interest: {overview.get('short_percent_outstanding', 'N/A')}%
- Institutional Ownership: {overview.get('percent_institutions', 'N/A')}%
"""
        
        return market_info.strip()
    
    def _calculate_debt_to_equity(self, balance_sheets: List[Dict]) -> str:
        """Calculate debt-to-equity ratio"""
        if not balance_sheets:
            return "N/A"
        
        latest = balance_sheets[0]
        total_debt = (latest.get('short_term_debt', 0) or 0) + (latest.get('long_term_debt', 0) or 0)
        total_equity = latest.get('total_shareholder_equity', 0) or 0
        
        if total_equity > 0:
            return f"{total_debt / total_equity:.2f}"
        return "N/A"
    
    def _calculate_current_ratio(self, balance_sheets: List[Dict]) -> str:
        """Calculate current ratio"""
        if not balance_sheets:
            return "N/A"
        
        latest = balance_sheets[0]
        current_assets = latest.get('total_current_assets', 0) or 0
        current_liabilities = latest.get('total_current_liabilities', 0) or 0
        
        if current_liabilities > 0:
            return f"{current_assets / current_liabilities:.2f}"
        return "N/A"
    
    async def generate_summary(self, stock_data: Dict[str, Any], model_name: str = "phi4") -> str:
        """Generate comprehensive stock analysis summary using RAG"""
        try:
            if model_name not in self.models or self.models[model_name] is None:
                raise ValueError(f"Model {model_name} is not available")
            
            # Prepare data sections
            company_data = self._prepare_company_data(stock_data)
            financial_data = self._prepare_financial_data(stock_data)
            news_sentiment = self._prepare_news_sentiment(stock_data)
            market_metrics = self._prepare_market_metrics(stock_data)
            
            # Create documents for RAG
            documents = [
                Document(page_content=company_data, metadata={"type": "company"}),
                Document(page_content=financial_data, metadata={"type": "financial"}),
                Document(page_content=news_sentiment, metadata={"type": "news"}),
                Document(page_content=market_metrics, metadata={"type": "market"})
            ]
            
            # Create vector store
            text_splitter = RecursiveCharacterTextSplitter(
                chunk_size=1000,
                chunk_overlap=100
            )
            
            # Split documents
            split_docs = text_splitter.split_documents(documents)
            
            # Create vector store
            vectorstore = FAISS.from_documents(split_docs, self.embeddings)
            
            # Create query for retrieval
            query = f"Comprehensive investment analysis for {stock_data.get('symbol', 'stock')}"
            
            # Retrieve relevant context
            relevant_docs = vectorstore.similarity_search(query, k=4)
            context = "\n\n".join([doc.page_content for doc in relevant_docs])
            
            # Generate prompt
            prompt = self.prompt_template.format(
                company_data=company_data,
                financial_data=financial_data,
                news_sentiment=news_sentiment,
                market_metrics=market_metrics
            )
            
            # Generate summary using LLM
            llm = self.models[model_name]
            summary = await asyncio.to_thread(llm.invoke, prompt)
            
            # Clean up the summary
            summary = self._clean_summary(summary)
            
            logger.info(f"Generated summary for {stock_data.get('symbol')} using {model_name}")
            return summary
            
        except Exception as e:
            logger.error(f"Error generating summary with {model_name}: {str(e)}")
            raise
    
    def _clean_summary(self, summary: str) -> str:
        """Clean and format the generated summary"""
        # Remove any unwanted prefixes or suffixes
        summary = summary.strip()
        
        # Ensure proper markdown formatting
        lines = summary.split('\n')
        cleaned_lines = []
        
        for line in lines:
            line = line.strip()
            if line:
                # Ensure proper header formatting
                if line.startswith('##') and not line.startswith('## '):
                    line = line.replace('##', '## ', 1)
                elif line.startswith('#') and not line.startswith('# '):
                    line = line.replace('#', '# ', 1)
                
                cleaned_lines.append(line)
        
        return '\n\n'.join(cleaned_lines)