import requests
import time
import logging
from typing import Dict, Any, Optional, List
from datetime import datetime, date
from config import Config

logger = logging.getLogger(__name__)

class RateLimiter:
    def __init__(self, max_requests: int, time_window: int = 60):
        self.max_requests = max_requests
        self.time_window = time_window
        self.requests = []
    
    def wait_if_needed(self):
        """Wait if rate limit would be exceeded"""
        now = time.time()
        # Remove old requests outside time window
        self.requests = [req_time for req_time in self.requests if now - req_time < self.time_window]
        
        if len(self.requests) >= self.max_requests:
            sleep_time = self.time_window - (now - self.requests[0]) + 1
            if sleep_time > 0:
                logger.info(f"Rate limit reached. Waiting {sleep_time:.2f} seconds...")
                time.sleep(sleep_time)
        
        self.requests.append(now)

class AlphaVantageClient:
    def __init__(self):
        self.api_key = Config.ALPHA_VANTAGE_API_KEY
        self.base_url = "https://www.alphavantage.co/query"
        self.rate_limiter = RateLimiter(Config.ALPHA_VANTAGE_RATE_LIMIT)
    
    def _make_request(self, params: Dict[str, Any]) -> Optional[Dict]:
        """Make API request with rate limiting and error handling"""
        # self.rate_limiter.wait_if_needed()
        
        params['apikey'] = self.api_key
        
        try:
            response = requests.get(self.base_url, params=params, timeout=30)
            response.raise_for_status()
            if params['function'] == 'LISTING_STATUS':
                return response

            data = response.json()
            
            # Check for API errors
            if "Error Message" in data:
                logger.error(f"API Error: {data['Error Message']}")
                return None
            
            if "Note" in data:
                logger.warning(f"API Note: {data['Note']}")
                return None
            
            return data
        
        except requests.exceptions.RequestException as e:
            logger.error(f"Request failed: {e}")
            return None
        except ValueError as e:
            logger.error(f"JSON parsing failed: {e}")
            return None
    
    def get_daily_prices(self, symbol: str, output_size: str = "compact") -> Optional[List[Dict[str, Any]]]:
        """개별 종목의 일별 OHLCV 데이터를 가져와서 필요한 값들만 딕셔너리 리스트로 반환"""
        params = {
            'function': 'TIME_SERIES_DAILY',
            'symbol': symbol.upper(),
            'outputsize': output_size  # 'compact' (최근 100일) 또는 'full' (최대 20년)
        }
        
        data = self._make_request(params)
        if not data:
            return None
        
        # API 에러 체크
        if "Error Message" in data:
            logger.error(f"API Error: {data['Error Message']}")
            return None
        
        if "Note" in data:
            logger.warning(f"API Note: {data['Note']}")
            return None
        
        # 메타데이터 확인
        meta_data = data.get("Meta Data", {})
        if not meta_data:
            logger.error("No Meta Data found in response")
            return None
        
        # 시계열 데이터 추출
        time_series = data.get("Time Series (Daily)", {})
        if not time_series:
            logger.error("No Time Series (Daily) data found in response")
            return None
        
        # 데이터 변환
        daily_prices = []
        for date_str, values in time_series.items():
            price_date = self._safe_date(date_str)
            if not price_date:
                continue
            
            price_data = {
                'symbol': symbol.upper(),
                'date': price_date,
                'open': self._safe_float(values.get('1. open')),
                'high': self._safe_float(values.get('2. high')),
                'low': self._safe_float(values.get('3. low')),
                'close': self._safe_float(values.get('4. close')),
                'volume': self._safe_int(values.get('5. volume'))
            }
            
            # 필수 데이터가 있는 경우만 추가
            if all(v is not None for v in [price_data['open'], price_data['high'], 
                                         price_data['low'], price_data['close']]):
                daily_prices.append(price_data)
        
        # 날짜순으로 정렬 (오래된 것부터)
        daily_prices.sort(key=lambda x: x['date'])
        
        logger.info(f"Successfully fetched {len(daily_prices)} daily price records for {symbol}")
        return daily_prices
    
    # 주식 종목 리스트 가져오기
    def get_stock_listings(self) -> Optional[List[Dict[str, Any]]]:
        """주식 종목 리스트를 가져와서 필요한 값들만 딕셔너리로 반환"""
        params = {
            'function': 'LISTING_STATUS'
        }
        
        response = self._make_request(params)
        if not response:
            return None
        
        try:
            # CSV 응답을 파싱
            lines = response.text.strip().split('\n')
            if not lines:
                logger.error("Empty response from API")
                return None
            
            # 헤더 행 파싱
            headers = [h.strip() for h in lines[0].split(',')]
            
            # 데이터 행들 파싱하여 필요한 값들만 추출
            stock_listings = []
            symbol_count = 0
            for line in lines[1:]:
                if line.strip():
                    values = [v.strip() for v in line.split(',')]
                    if len(values) == len(headers):
                        raw_data = dict(zip(headers, values))
                        
                        # 필요한 값들만 딕셔너리로 구성
                        stock_info = {
                            'symbol': raw_data.get('symbol', '').strip(),
                            'name': raw_data.get('name', '').strip(),
                            'exchange': raw_data.get('exchange', '').strip(),
                            'asset_type': raw_data.get('assetType', '').strip(),
                            'ipo_date': self._safe_date(raw_data.get('ipoDate', '')),
                            'delisting_date': self._safe_date(raw_data.get('delistingDate', '')),
                            'status': raw_data.get('status', '').strip()
                        }
                        
                        # 필수 필드가 있는 경우만 추가
                        if stock_info['symbol'] in Config.STOCK_SYMBOLS:
                            stock_listings.append(stock_info)
                            symbol_count += 1
                
                if symbol_count == len(Config.STOCK_SYMBOLS):
                    break
            
            logger.info(f"Successfully fetched {len(stock_listings)} stock listings")
            return stock_listings
            
        except Exception as e:
            logger.error(f"Error parsing API response: {e}")
            return None
        
    # 기업 별 정보 가져오기
    def get_company_overview(self, symbol: str) -> Optional[Dict[str, Any]]:
        """기업의 기본 정보 및 재무 지표를 가져와서 필요한 값들만 딕셔너리로 반환"""
        params = {
            'function': 'OVERVIEW',
            'symbol': symbol.upper()
        }
        
        data = self._make_request(params)
        if not data:
            return None
        
        # API 에러 체크
        if "Error Message" in data:
            logger.error(f"API Error: {data['Error Message']}")
            return None
        
        if "Note" in data:
            logger.warning(f"API Note: {data['Note']}")
            return None
        
        # 기본 필드 확인
        if not data.get("Symbol"):
            logger.error("No Symbol found in response")
            return None
        
        # 데이터 변환
        company_overview = {
            'symbol': data.get('Symbol', '').upper(),
            'description': data.get('Description', '').strip() or None,
            'currency': data.get('Currency', '').strip() or None,
            'country': data.get('Country', '').strip() or None,
            'sector': data.get('Sector', '').strip() or None,
            'industry': data.get('Industry', '').strip() or None,
            'address': data.get('Address', '').strip() or None,
            'fiscal_year_end': data.get('FiscalYearEnd', '').strip() or None,
            'latest_quarter': self._safe_date(data.get('LatestQuarter', '')),
            
            # 재무 지표들
            'market_capitalization': self._safe_int(data.get('MarketCapitalization', '')),
            'ebitda': self._safe_int(data.get('EBITDA', '')),
            'pe_ratio': self._safe_float(data.get('PERatio', '')),
            'peg_ratio': self._safe_float(data.get('PEGRatio', '')),
            'book_value': self._safe_float(data.get('BookValue', '')),
            'dividend_per_share': self._safe_float(data.get('DividendPerShare', '')),
            'dividend_yield': self._safe_float(data.get('DividendYield', '')),
            'eps': self._safe_float(data.get('EPS', '')),
            'revenue_per_share_ttm': self._safe_float(data.get('RevenuePerShareTTM', '')),
            'profit_margin': self._safe_float(data.get('ProfitMargin', '')),
            'operating_margin_ttm': self._safe_float(data.get('OperatingMarginTTM', '')),
            'return_on_assets_ttm': self._safe_float(data.get('ReturnOnAssetsTTM', '')),
            'return_on_equity_ttm': self._safe_float(data.get('ReturnOnEquityTTM', '')),
            'revenue_ttm': self._safe_int(data.get('RevenueTTM', '')),
            'gross_profit_ttm': self._safe_int(data.get('GrossProfitTTM', '')),
            'diluted_eps_ttm': self._safe_float(data.get('DilutedEPSTTM', '')),
            'quarterly_earnings_growth_yoy': self._safe_float(data.get('QuarterlyEarningsGrowthYOY', '')),
            'quarterly_revenue_growth_yoy': self._safe_float(data.get('QuarterlyRevenueGrowthYOY', '')),
            
            # 분석가 및 가격 지표
            'analyst_target_price': self._safe_float(data.get('AnalystTargetPrice', '')),
            'trailing_pe': self._safe_float(data.get('TrailingPE', '')),
            'forward_pe': self._safe_float(data.get('ForwardPE', '')),
            'price_to_sales_ratio_ttm': self._safe_float(data.get('PriceToSalesRatioTTM', '')),
            'price_to_book_ratio': self._safe_float(data.get('PriceToBookRatio', '')),
            'ev_to_revenue': self._safe_float(data.get('EVToRevenue', '')),
            'ev_to_ebitda': self._safe_float(data.get('EVToEBITDA', '')),
            'beta': self._safe_float(data.get('Beta', '')),
            
            # 주가 지표
            'fifty_two_week_high': self._safe_float(data.get('52WeekHigh', '')),
            'fifty_two_week_low': self._safe_float(data.get('52WeekLow', '')),
            'fifty_day_moving_average': self._safe_float(data.get('50DayMovingAverage', '')),
            'two_hundred_day_moving_average': self._safe_float(data.get('200DayMovingAverage', '')),
            
            # 주식 수량 지표
            'shares_outstanding': self._safe_int(data.get('SharesOutstanding', '')),
            'shares_float': self._safe_int(data.get('SharesFloat', '')),
            'shares_short': self._safe_int(data.get('SharesShort', '')),
            'shares_short_prior_month': self._safe_int(data.get('SharesShortPriorMonth', '')),
            'short_ratio': self._safe_float(data.get('ShortRatio', '')),
            'short_percent_outstanding': self._safe_float(data.get('ShortPercentOutstanding', '')),
            'short_percent_float': self._safe_float(data.get('ShortPercentFloat', '')),
            'percent_insiders': self._safe_float(data.get('PercentInsiders', '')),
            'percent_institutions': self._safe_float(data.get('PercentInstitutions', '')),
            
            # 배당 정보
            'forward_annual_dividend_rate': self._safe_float(data.get('ForwardAnnualDividendRate', '')),
            'forward_annual_dividend_yield': self._safe_float(data.get('ForwardAnnualDividendYield', '')),
            'payout_ratio': self._safe_float(data.get('PayoutRatio', '')),
            'dividend_date': self._safe_date(data.get('DividendDate', '')),
            'ex_dividend_date': self._safe_date(data.get('ExDividendDate', '')),
            'last_split_factor': data.get('LastSplitFactor', '').strip() or None,
            'last_split_date': self._safe_date(data.get('LastSplitDate', ''))
        }
        
        logger.info(f"Successfully fetched company overview for {symbol}")
        return company_overview

    def get_income_statement(self, symbol: str) -> Optional[Dict[str, Any]]:
        """기업의 손익계산서(연간/분기) 원본 응답 반환"""
        params = {
            'function': 'INCOME_STATEMENT',
            'symbol': symbol.upper()
        }
        data = self._make_request(params)
        if not data or "symbol" not in data:
            logger.error(f"Failed to fetch income statement for {symbol}")
            return None
        
        def parse_report(report):
            return {
                "symbol": symbol,
                "fiscal_date_ending": self._safe_date(report.get("fiscalDateEnding")),
                "reported_currency": report.get("reportedCurrency"),
                "gross_profit": self._safe_int(report.get("grossProfit")),
                "total_revenue": self._safe_int(report.get("totalRevenue")),
                "cost_of_revenue": self._safe_int(report.get("costOfRevenue")),
                "cost_of_goods_and_services_sold": self._safe_int(report.get("costofGoodsAndServicesSold")),
                "operating_income": self._safe_int(report.get("operatingIncome")),
                "selling_general_and_administrative": self._safe_int(report.get("sellingGeneralAndAdministrative")),
                "research_and_development": self._safe_int(report.get("researchAndDevelopment")),
                "operating_expenses": self._safe_int(report.get("operatingExpenses")),
                "investment_income_net": self._safe_int(report.get("investmentIncomeNet")),
                "net_interest_income": self._safe_int(report.get("netInterestIncome")),
                "interest_income": self._safe_int(report.get("interestIncome")),
                "interest_expense": self._safe_int(report.get("interestExpense")),
                "non_interest_income": self._safe_int(report.get("nonInterestIncome")),
                "other_non_operating_income": self._safe_int(report.get("otherNonOperatingIncome")),
                "depreciation": self._safe_int(report.get("depreciation")),
                "depreciation_and_amortization": self._safe_int(report.get("depreciationAndAmortization")),
                "income_before_tax": self._safe_int(report.get("incomeBeforeTax")),
                "income_tax_expense": self._safe_int(report.get("incomeTaxExpense")),
                "interest_and_debt_expense": self._safe_int(report.get("interestAndDebtExpense")),
                "net_income_from_continuing_operations": self._safe_int(report.get("netIncomeFromContinuingOperations")),
                "comprehensive_income_net_of_tax": self._safe_int(report.get("comprehensiveIncomeNetOfTax")),
                "ebit": self._safe_int(report.get("ebit")),
                "ebitda": self._safe_int(report.get("ebitda")),
                "net_income": self._safe_int(report.get("netIncome")),
            }
        
        annual_reports = [parse_report(r) for r in data.get("annualReports", [])]
        quarterly_reports = [parse_report(r) for r in data.get("quarterlyReports", [])]
        
        return {
            "symbol": symbol.upper(),
            "annualReports": annual_reports,
            "quarterlyReports": quarterly_reports
        }

    def get_balance_sheet(self, symbol: str) -> Optional[Dict[str, Any]]:
        """기업의 대차대조표(연간/분기) 데이터를 파싱하여 반환"""
        params = {
            'function': 'BALANCE_SHEET',
            'symbol': symbol.upper()
        }
        data = self._make_request(params)
        if not data or "symbol" not in data:
            logger.error(f"Failed to fetch balance sheet for {symbol}")
            return None
        def parse_report(report):
            return {
                "symbol": symbol,
                "fiscal_date_ending": self._safe_date(report.get("fiscalDateEnding")),
                "reported_currency": report.get("reportedCurrency"),
                "total_assets": self._safe_int(report.get("totalAssets")),
                "total_current_assets": self._safe_int(report.get("totalCurrentAssets")),
                "cash_and_cash_equivalents_at_carrying_value": self._safe_int(report.get("cashAndCashEquivalentsAtCarryingValue")),
                "cash_and_short_term_investments": self._safe_int(report.get("cashAndShortTermInvestments")),
                "inventory": self._safe_int(report.get("inventory")),
                "current_net_receivables": self._safe_int(report.get("currentNetReceivables")),
                "total_non_current_assets": self._safe_int(report.get("totalNonCurrentAssets")),
                "property_plant_equipment": self._safe_int(report.get("propertyPlantEquipment")),
                "accumulated_depreciation_amortization_ppe": self._safe_int(report.get("accumulatedDepreciationAmortizationPPE")),
                "intangible_assets": self._safe_int(report.get("intangibleAssets")),
                "intangible_assets_excluding_goodwill": self._safe_int(report.get("intangibleAssetsExcludingGoodwill")),
                "goodwill": self._safe_int(report.get("goodwill")),
                "investments": self._safe_int(report.get("investments")),
                "long_term_investments": self._safe_int(report.get("longTermInvestments")),
                "short_term_investments": self._safe_int(report.get("shortTermInvestments")),
                "other_current_assets": self._safe_int(report.get("otherCurrentAssets")),
                "other_non_current_assets": self._safe_int(report.get("otherNonCurrentAssets")),
                "total_liabilities": self._safe_int(report.get("totalLiabilities")),
                "total_current_liabilities": self._safe_int(report.get("totalCurrentLiabilities")),
                "current_accounts_payable": self._safe_int(report.get("currentAccountsPayable")),
                "deferred_revenue": self._safe_int(report.get("deferredRevenue")),
                "current_debt": self._safe_int(report.get("currentDebt")),
                "short_term_debt": self._safe_int(report.get("shortTermDebt")),
                "total_non_current_liabilities": self._safe_int(report.get("totalNonCurrentLiabilities")),
                "capital_lease_obligations": self._safe_int(report.get("capitalLeaseObligations")),
                "long_term_debt": self._safe_int(report.get("longTermDebt")),
                "current_long_term_debt": self._safe_int(report.get("currentLongTermDebt")),
                "long_term_debt_noncurrent": self._safe_int(report.get("longTermDebtNoncurrent")),
                "short_long_term_debt_total": self._safe_int(report.get("shortLongTermDebtTotal")),
                "other_current_liabilities": self._safe_int(report.get("otherCurrentLiabilities")),
                "other_non_current_liabilities": self._safe_int(report.get("otherNonCurrentLiabilities")),
                "total_shareholder_equity": self._safe_int(report.get("totalShareholderEquity")),
                "treasury_stock": self._safe_int(report.get("treasuryStock")),
                "retained_earnings": self._safe_int(report.get("retainedEarnings")),
                "common_stock": self._safe_int(report.get("commonStock")),
                "common_stock_shares_outstanding": self._safe_int(report.get("commonStockSharesOutstanding")),
            }
        annual_reports = [parse_report(r) for r in data.get("annualReports", [])]
        quarterly_reports = [parse_report(r) for r in data.get("quarterlyReports", [])]
        return {
            "symbol": symbol.upper(),
            "annualReports": annual_reports,
            "quarterlyReports": quarterly_reports
        }

    def get_cash_flow(self, symbol: str) -> Optional[Dict[str, Any]]:
        """기업의 현금흐름표(연간/분기) 데이터를 파싱하여 반환"""
        params = {
            'function': 'CASH_FLOW',
            'symbol': symbol.upper()
        }
        data = self._make_request(params)
        if not data or "symbol" not in data:
            logger.error(f"Failed to fetch cash flow for {symbol}")
            return None
        def parse_report(report):
            return {
                "symbol": symbol,
                "fiscal_date_ending": self._safe_date(report.get("fiscalDateEnding")),
                "reported_currency": report.get("reportedCurrency"),
                "operating_cashflow": self._safe_int(report.get("operatingCashflow")),
                "payments_for_operating_activities": self._safe_int(report.get("paymentsForOperatingActivities")),
                "proceeds_from_operating_activities": self._safe_int(report.get("proceedsFromOperatingActivities")),
                "change_in_operating_liabilities": self._safe_int(report.get("changeInOperatingLiabilities")),
                "change_in_operating_assets": self._safe_int(report.get("changeInOperatingAssets")),
                "depreciation_depletion_and_amortization": self._safe_int(report.get("depreciationDepletionAndAmortization")),
                "capital_expenditures": self._safe_int(report.get("capitalExpenditures")),
                "change_in_receivables": self._safe_int(report.get("changeInReceivables")),
                "change_in_inventory": self._safe_int(report.get("changeInInventory")),
                "profit_loss": self._safe_int(report.get("profitLoss")),
                "cashflow_from_investment": self._safe_int(report.get("cashflowFromInvestment")),
                "cashflow_from_financing": self._safe_int(report.get("cashflowFromFinancing")),
                "proceeds_from_repayments_of_short_term_debt": self._safe_int(report.get("proceedsFromRepaymentsOfShortTermDebt")),
                "payments_for_repurchase_of_common_stock": self._safe_int(report.get("paymentsForRepurchaseOfCommonStock")),
                "payments_for_repurchase_of_equity": self._safe_int(report.get("paymentsForRepurchaseOfEquity")),
                "payments_for_repurchase_of_preferred_stock": self._safe_int(report.get("paymentsForRepurchaseOfPreferredStock")),
                "dividend_payout": self._safe_int(report.get("dividendPayout")),
                "dividend_payout_common_stock": self._safe_int(report.get("dividendPayoutCommonStock")),
                "dividend_payout_preferred_stock": self._safe_int(report.get("dividendPayoutPreferredStock")),
                "proceeds_from_issuance_of_common_stock": self._safe_int(report.get("proceedsFromIssuanceOfCommonStock")),
                "proceeds_from_issuance_of_long_term_debt_and_capital_securities": self._safe_int(report.get("proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet")),
                "proceeds_from_issuance_of_preferred_stock": self._safe_int(report.get("proceedsFromIssuanceOfPreferredStock")),
                "proceeds_from_repurchase_of_equity": self._safe_int(report.get("proceedsFromRepurchaseOfEquity")),
                "proceeds_from_sale_of_treasury_stock": self._safe_int(report.get("proceedsFromSaleOfTreasuryStock")),
                "change_in_cash_and_cash_equivalents": self._safe_int(report.get("changeInCashAndCashEquivalents")),
                "change_in_exchange_rate": self._safe_int(report.get("changeInExchangeRate")),
                "net_income": self._safe_int(report.get("netIncome")),
            }
        annual_reports = [parse_report(r) for r in data.get("annualReports", [])]
        quarterly_reports = [parse_report(r) for r in data.get("quarterlyReports", [])]
        return {
            "symbol": symbol.upper(),
            "annualReports": annual_reports,
            "quarterlyReports": quarterly_reports
        }

    def get_news_sentiment(self, symbol: str, time_from: str = None, limits: int = 200) -> list:
        """주어진 symbols에 대해 뉴스 및 감성분석 데이터를 파싱하여 반환"""
        params = {
            'function': 'NEWS_SENTIMENT',
            'tickers': symbol,
            'apikey': self.api_key,
            'limit': limits
        }
        if time_from:
            params['time_from'] = time_from
        data = self._make_request(params)
        print(data)
        if not data or 'feed' not in data:
            logger.error(f"Failed to fetch news sentiment for {symbol}")
            return []
        
        def parse_time(ts):
            # API: '20250710T132000' → datetime
            try:
                return datetime.strptime(ts, '%Y%m%dT%H%M%S')
            except Exception:
                return None
        
        news_list = []
        for item in data['feed']:
            news = {
                'title': item.get('title'),
                'url': item.get('url'),
                'time_published': parse_time(item.get('time_published')),
                'authors': item.get('authors', []),
                'summary': item.get('summary'),
                'source': item.get('source'),
                'category_within_source': item.get('category_within_source'),
                'source_domain': item.get('source_domain'),
                'overall_sentiment_score': self._safe_float(item.get('overall_sentiment_score')),
                'overall_sentiment_label': item.get('overall_sentiment_label'),
                'ticker_sentiment': [
                    {
                        'ticker': ts.get('ticker'),
                        'relevance_score': self._safe_float(ts.get('relevance_score')),
                        'sentiment_score': self._safe_float(ts.get('ticker_sentiment_score')),
                        'sentiment_label': ts.get('ticker_sentiment_label')
                    }
                    for ts in item.get('ticker_sentiment', [])
                ]
            }
            news_list.append(news)
        return news_list

    def _safe_float(self, value: Any) -> Optional[float]:
        """Safely convert value to float"""
        if value is None or value == '' or value == 'None':
            return None
        try:
            return float(value)
        except (ValueError, TypeError):
            return None
    
    def _safe_int(self, value: Any) -> Optional[int]:
        """Safely convert value to integer"""
        if value is None or value == '' or value == 'None':
            return None
        try:
            return int(float(value))
        except (ValueError, TypeError):
            return None
    
    def _safe_date(self, value: Any) -> Optional[date]:
        """Safely convert value to date"""
        if value is None or value == '' or value == 'None':
            return None
        try:
            return datetime.strptime(value, '%Y-%m-%d').date()
        except (ValueError, TypeError):
            return None