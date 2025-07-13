from typing import Dict, Any, Tuple
import logging
import numpy as np

logger = logging.getLogger(__name__)

class RecommendationEngine:
    """
    Stock recommendation engine that calculates recommendation scores and labels
    based on fundamental analysis, technical indicators, and market sentiment.
    """
    
    def __init__(self):
        # Scoring weights
        self.weights = {
            'valuation': 0.25,      # P/E, P/B, P/S ratios
            'profitability': 0.25,  # Margins, ROE, ROA
            'growth': 0.20,         # Revenue/earnings growth
            'financial_health': 0.15, # Debt ratios, current ratio
            'market_sentiment': 0.10,  # News sentiment
            'technical': 0.05       # Price momentum, MA position
        }
        
        # Recommendation thresholds
        self.thresholds = {
            'STRONG_BUY': 8.0,
            'BUY': 6.5,
            'HOLD': 4.5,
            'SELL': 3.0,
            'STRONG_SELL': 0.0
        }
    
    async def calculate_recommendation(self, stock_data: Dict[str, Any]) -> Tuple[float, str]:
        """Calculate recommendation score and label"""
        try:
            overview = stock_data.get('company_overview', {})
            income_statements = stock_data.get('income_statements', [])
            balance_sheets = stock_data.get('balance_sheets', [])
            news_data = stock_data.get('recent_news', [])
            
            # Calculate individual scores
            valuation_score = self._calculate_valuation_score(overview)
            profitability_score = self._calculate_profitability_score(overview)
            growth_score = self._calculate_growth_score(overview, income_statements)
            financial_health_score = self._calculate_financial_health_score(overview, balance_sheets)
            sentiment_score = self._calculate_sentiment_score(news_data)
            technical_score = self._calculate_technical_score(overview)
            
            # Calculate weighted total score
            total_score = (
                valuation_score * self.weights['valuation'] +
                profitability_score * self.weights['profitability'] +
                growth_score * self.weights['growth'] +
                financial_health_score * self.weights['financial_health'] +
                sentiment_score * self.weights['market_sentiment'] +
                technical_score * self.weights['technical']
            )
            
            # Normalize to 1-10 scale
            final_score = max(1.0, min(10.0, total_score))
            
            # Determine recommendation label
            recommendation_label = self._get_recommendation_label(final_score)
            
            logger.info(f"Calculated recommendation: {final_score:.2f} ({recommendation_label}) for {stock_data.get('symbol')}")
            
            return round(final_score, 2), recommendation_label
            
        except Exception as e:
            logger.error(f"Error calculating recommendation: {str(e)}")
            # Return neutral recommendation on error
            return 5.0, "HOLD"
    
    def _calculate_valuation_score(self, overview: Dict[str, Any]) -> float:
        """Calculate valuation score based on P/E, P/B, P/S ratios"""
        score = 5.0  # Neutral starting point
        
        try:
            # P/E Ratio scoring
            pe_ratio = overview.get('pe_ratio')
            if pe_ratio:
                if pe_ratio < 10:
                    score += 2.0  # Very undervalued
                elif pe_ratio < 15:
                    score += 1.5  # Undervalued
                elif pe_ratio < 25:
                    score += 0.5  # Fair value
                elif pe_ratio < 35:
                    score -= 0.5  # Overvalued
                else:
                    score -= 2.0  # Very overvalued
            
            # P/B Ratio scoring
            pb_ratio = overview.get('price_to_book_ratio')
            if pb_ratio:
                if pb_ratio < 1.0:
                    score += 1.5  # Very undervalued
                elif pb_ratio < 2.0:
                    score += 1.0  # Undervalued
                elif pb_ratio < 3.0:
                    score += 0.0  # Fair value
                else:
                    score -= 1.0  # Overvalued
            
            # P/S Ratio scoring
            ps_ratio = overview.get('price_to_sales_ratio_ttm')
            if ps_ratio:
                if ps_ratio < 1.0:
                    score += 1.0
                elif ps_ratio < 3.0:
                    score += 0.5
                elif ps_ratio > 10.0:
                    score -= 1.0
            
        except Exception as e:
            logger.warning(f"Error in valuation scoring: {str(e)}")
        
        return max(1.0, min(10.0, score))
    
    def _calculate_profitability_score(self, overview: Dict[str, Any]) -> float:
        """Calculate profitability score based on margins and returns"""
        score = 5.0
        
        try:
            # Profit Margin
            profit_margin = overview.get('profit_margin')
            if profit_margin:
                if profit_margin > 0.20:
                    score += 2.0  # Excellent
                elif profit_margin > 0.10:
                    score += 1.0  # Good
                elif profit_margin > 0.05:
                    score += 0.5  # Average
                elif profit_margin < 0:
                    score -= 2.0  # Negative
            
            # Operating Margin
            operating_margin = overview.get('operating_margin_ttm')
            if operating_margin:
                if operating_margin > 0.15:
                    score += 1.0
                elif operating_margin > 0.10:
                    score += 0.5
                elif operating_margin < 0:
                    score -= 1.0
            
            # ROE
            roe = overview.get('return_on_equity_ttm')
            if roe:
                if roe > 0.20:
                    score += 1.5
                elif roe > 0.15:
                    score += 1.0
                elif roe > 0.10:
                    score += 0.5
                elif roe < 0:
                    score -= 1.0
            
            # ROA
            roa = overview.get('return_on_assets_ttm')
            if roa:
                if roa > 0.10:
                    score += 1.0
                elif roa > 0.05:
                    score += 0.5
                elif roa < 0:
                    score -= 0.5
            
        except Exception as e:
            logger.warning(f"Error in profitability scoring: {str(e)}")
        
        return max(1.0, min(10.0, score))
    
    def _calculate_growth_score(self, overview: Dict[str, Any], income_statements: list) -> float:
        """Calculate growth score based on revenue and earnings growth"""
        score = 5.0
        
        try:
            # Quarterly Revenue Growth
            revenue_growth = overview.get('quarterly_revenue_growth_yoy')
            if revenue_growth:
                if revenue_growth > 0.30:
                    score += 2.5  # Excellent growth
                elif revenue_growth > 0.15:
                    score += 1.5  # Good growth
                elif revenue_growth > 0.05:
                    score += 0.5  # Modest growth
                elif revenue_growth < -0.05:
                    score -= 1.5  # Declining
            
            # Quarterly Earnings Growth
            earnings_growth = overview.get('quarterly_earnings_growth_yoy')
            if earnings_growth:
                if earnings_growth > 0.25:
                    score += 2.0
                elif earnings_growth > 0.10:
                    score += 1.0
                elif earnings_growth > 0.0:
                    score += 0.5
                elif earnings_growth < -0.10:
                    score -= 1.5
            
            # Revenue trend consistency
            if len(income_statements) >= 4:
                revenues = [stmt.get('total_revenue', 0) for stmt in income_statements[:4]]
                revenues = [r for r in revenues if r and r > 0]
                
                if len(revenues) >= 3:
                    # Check for consistent growth
                    growth_rates = []
                    for i in range(1, len(revenues)):
                        if revenues[i-1] > 0:
                            growth_rate = (revenues[i-1] - revenues[i]) / revenues[i]
                            growth_rates.append(growth_rate)
                    
                    if growth_rates:
                        avg_growth = np.mean(growth_rates)
                        consistency = 1 - np.std(growth_rates) if len(growth_rates) > 1 else 1
                        
                        if avg_growth > 0 and consistency > 0.7:
                            score += 1.0  # Consistent growth
                        elif avg_growth < -0.05:
                            score -= 1.0  # Declining trend
            
        except Exception as e:
            logger.warning(f"Error in growth scoring: {str(e)}")
        
        return max(1.0, min(10.0, score))
    
    def _calculate_financial_health_score(self, overview: Dict[str, Any], balance_sheets: list) -> float:
        """Calculate financial health score"""
        score = 5.0
        
        try:
            # Current Ratio
            if balance_sheets:
                latest_bs = balance_sheets[0]
                current_assets = latest_bs.get('total_current_assets', 0)
                current_liabilities = latest_bs.get('total_current_liabilities', 0)
                
                if current_liabilities and current_liabilities > 0:
                    current_ratio = current_assets / current_liabilities
                    if current_ratio > 2.0:
                        score += 1.5  # Very healthy
                    elif current_ratio > 1.5:
                        score += 1.0  # Healthy
                    elif current_ratio > 1.0:
                        score += 0.5  # Adequate
                    else:
                        score -= 2.0  # Poor liquidity
                
                # Debt-to-Equity
                total_debt = (latest_bs.get('short_term_debt', 0) or 0) + (latest_bs.get('long_term_debt', 0) or 0)
                total_equity = latest_bs.get('total_shareholder_equity', 0) or 0
                
                if total_equity > 0:
                    debt_to_equity = total_debt / total_equity
                    if debt_to_equity < 0.3:
                        score += 1.5  # Very low debt
                    elif debt_to_equity < 0.6:
                        score += 1.0  # Moderate debt
                    elif debt_to_equity < 1.0:
                        score += 0.0  # Acceptable debt
                    else:
                        score -= 1.5  # High debt
            
            # Interest Coverage (EBITDA/Interest)
            ebitda = overview.get('ebitda')
            # This would need interest expense from income statement
            # Simplified scoring based on available data
            
        except Exception as e:
            logger.warning(f"Error in financial health scoring: {str(e)}")
        
        return max(1.0, min(10.0, score))
    
    def _calculate_sentiment_score(self, news_data: list) -> float:
        """Calculate sentiment score from recent news"""
        score = 5.0
        
        try:
            if not news_data:
                return score
            
            # Calculate average sentiment
            sentiments = [item.get('overall_sentiment_score', 0) for item in news_data if item.get('overall_sentiment_score') is not None]
            
            if sentiments:
                avg_sentiment = np.mean(sentiments)
                
                # Convert sentiment (-1 to 1) to score adjustment based on definition
                if avg_sentiment >= 0.35:
                    score += 2.0  # Bullish
                elif avg_sentiment >= 0.15:
                    score += 1.0  # Somewhat_Bullish
                elif avg_sentiment > -0.15:
                    score += 0.0  # Neutral
                elif avg_sentiment > -0.35:
                    score -= 1.0  # Somewhat-Bearish
                else:
                    score -= 2.0  # Bearish
                
                # Consider sentiment consistency
                sentiment_std = np.std(sentiments) if len(sentiments) > 1 else 0
                if sentiment_std < 0.2:  # Consistent sentiment
                    score += 0.5
            
        except Exception as e:
            logger.warning(f"Error in sentiment scoring: {str(e)}")
        
        return max(1.0, min(10.0, score))
    
    def _calculate_technical_score(self, overview: Dict[str, Any]) -> float:
        """Calculate technical score based on price movements and indicators"""
        score = 5.0
        
        try:
            # 52-week position
            high_52w = overview.get('fifty_two_week_high')
            low_52w = overview.get('fifty_two_week_low')
            ma_50 = overview.get('fifty_day_moving_average')
            ma_200 = overview.get('two_hundred_day_moving_average')
            
            # Current price proxy (using 50-day MA as approximation)
            current_price = ma_50
            
            if high_52w and low_52w and current_price:
                # Position in 52-week range
                position = (current_price - low_52w) / (high_52w - low_52w)
                
                if position > 0.8:
                    score += 1.0  # Near highs
                elif position > 0.6:
                    score += 0.5
                elif position < 0.2:
                    score -= 1.0  # Near lows
            
            # Moving average comparison
            if ma_50 and ma_200:
                if ma_50 > ma_200:
                    score += 1.0  # Bullish trend
                else:
                    score -= 1.0  # Bearish trend
            
            # Beta consideration
            beta = overview.get('beta')
            if beta:
                if 0.5 <= beta <= 1.5:
                    score += 0.5  # Reasonable volatility
                elif beta > 2.0:
                    score -= 0.5  # High volatility
            
        except Exception as e:
            logger.warning(f"Error in technical scoring: {str(e)}")
        
        return max(1.0, min(10.0, score))
    
    def _get_recommendation_label(self, score: float) -> str:
        """Convert numerical score to recommendation label"""
        if score >= self.thresholds['STRONG_BUY']:
            return 'STRONG_BUY'
        elif score >= self.thresholds['BUY']:
            return 'BUY'
        elif score >= self.thresholds['HOLD']:
            return 'HOLD'
        elif score >= self.thresholds['SELL']:
            return 'SELL'
        else:
            return 'STRONG_SELL'