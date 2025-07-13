import logging
from typing import List, Dict, Any
from datetime import datetime, timedelta
from database import DatabaseManager
from api_client import AlphaVantageClient
from config import Config
import pytz

logger = logging.getLogger(__name__)

class StockDataSyncService:
    def __init__(self):
        self.db_manager = DatabaseManager()
        self.alpha_vantage = AlphaVantageClient()
        self.symbols = Config.STOCK_SYMBOLS
    
    # Daily: 20회 API 요청
    def sync_daily_prices(self) -> Dict[str, Any]:
        """Sync daily prices for all symbols"""
        results = {
            'success_count': 0,
            'error_count': 0,
            'errors': []
        }
        
        logger.info(f"Starting daily prices sync for {len(self.symbols)} symbols")
        
        # Save stock daily prices to database
        all_daily_prices = {}
        for symbol in self.symbols:
            daily_prices = self.alpha_vantage.get_daily_prices(symbol)
            all_daily_prices[symbol] = daily_prices
        
        stats = self._save_daily_prices_to_database(all_daily_prices)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"TIME_SERIES_DAILY")

        if stats['saved'] > 0:
            results['success_count'] += 1
        
        return results

    # Daily News: 1회 API 요청
    def sync_daily_news(self) -> Dict[str, Any]:
        """Sync daily prices for all symbols"""
        results = {
            'success_count': 0,
            'error_count': 0,
            'errors': []
        }
        
        logger.info(f"Starting daily news sync for {len(self.symbols)} symbols")
        
        # Save news articles and news-stocks to database
        eastern = pytz.timezone('US/Eastern')
        now_utc = datetime.utcnow().replace(tzinfo=pytz.utc)
        yesterday_est = now_utc.astimezone(eastern) - timedelta(days=1)
        target_est = yesterday_est.replace(hour=9, minute=0, second=0, microsecond=0)
        target_utc = target_est.astimezone(pytz.utc)
        time_from = target_utc.strftime('%Y%m%dT%H%M')

        news_list = self.alpha_vantage.get_news_sentiment(self.symbols, time_from=time_from, limits=200)

        stats = self._save_news_to_database(news_list)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"NEWS_SENTIMENT")

        if stats['saved'] > 0:
            results['success_count'] += 1
        
        logger.info(f"Daily quotes sync completed. Success: {results['success_count']}, Errors: {results['error_count']}")
        return results

    # Weekly: 1 + 20 회 API 요청
    def sync_weekly(self) -> Dict[str, Any]:
        """Sync weekly for all symbols"""
        results = {
            'success_count': 0,
            'error_count': 0,
            'errors': []
        }
        
        logger.info(f"Starting weekly data sync for {len(self.symbols)} symbols")
        
        # 1. Save stock listings to database
        stock_listings = self.alpha_vantage.get_stock_listings()
        stats = self._save_stocks_to_database(stock_listings)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"LISTING_STATUS")

        if stats['saved'] > 0:
            results['success_count'] += 1

        # 2. Save companies overview to database
        all_companies_overview = []
        for symbol in self.symbols:
            companies_overview = self.alpha_vantage.get_company_overview(symbol)
            all_companies_overview.append(companies_overview)
        
        stats = self._save_companies_overview_to_database(all_companies_overview)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"OVERVIEW")

        if stats['saved'] > 0:
            results['success_count'] += 1

        logger.info(f"Weekly quotes sync completed. Success: {results['success_count']}, Errors: {results['error_count']}")
        return results
    
    # Quarterly: 20 + 20 + 20 회 API 요청 -> Manual로 저장하기
    def sync_quarterly(self) -> Dict[str, Any]:
        """Sync quarterly for all symbols"""
        results = {
            'success_count': 0,
            'error_count': 0,
            'errors': []
        }
        logger.info(f"Starting quarterly sync for {len(self.symbols)} symbols")

        # 1. Income Statements
        all_income_statements = {}
        for symbol in self.symbols:
            data = self.alpha_vantage.get_income_statement(symbol)
            all_income_statements[symbol] = data
        stats = self._save_income_statements_to_database(all_income_statements)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"INCOME_STATEMENT")
        if stats['saved'] > 0:
            results['success_count'] += 1

        # 2. Balance Sheets
        all_balance_sheets = {}
        for symbol in self.symbols:
            data = self.alpha_vantage.get_balance_sheet(symbol)
            all_balance_sheets[symbol] = data
        stats = self._save_balance_sheets_to_database(all_balance_sheets)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"BALANCE_SHEET")
        if stats['saved'] > 0:
            results['success_count'] += 1

        # 3. Cash Flows
        all_cash_flows = {}
        for symbol in self.symbols:
            data = self.alpha_vantage.get_cash_flow(symbol)
            all_cash_flows[symbol] = data
        stats = self._save_cash_flows_to_database(all_cash_flows)
        if stats['failed'] > 0:
            results['error_count'] += 1
            results['errors'].append(f"CASH_FLOW")
        if stats['saved'] > 0:
            results['success_count'] += 1

        logger.info(f"Quarterly sync completed. Success: {results['success_count']}, Errors: {results['error_count']}")
        return results

    def run_sync(self, sync_type: str = None) -> Dict[str, Any]:
        """Run the appropriate sync based on type"""
        if sync_type is None:
            sync_type = Config.SYNC_TYPE
        
        results = {
            'sync_type': sync_type,
            'timestamp': datetime.now().isoformat(),
            'results': {}
        }
        
        logger.info(f"Starting {sync_type} sync")
        
        try:
            if sync_type == 'daily-prices':
                results['results'] = self.sync_daily_prices()
            elif sync_type == 'daily-news':
                results['results'] = self.sync_daily_news()
            elif sync_type == 'weekly':
                results['results'] = self.sync_weekly()
            elif sync_type == 'quarterly':
                results['results'] = self.sync_quarterly()
            else:
                raise ValueError(f"Unknown sync type: {sync_type}")
                
        except Exception as e:
            logger.error(f"Sync failed: {e}")
            results['error'] = str(e)
        
        logger.info(f"Sync completed: {sync_type}")
        return results
    

    ##############
    # Daily Sync #
    ##############
    def _save_daily_prices_to_database(self, all_daily_prices: Dict[str, List[Dict[str, Any]]]) -> Dict[str, int]:
        """여러 symbol의 일별 주가 데이터를 데이터베이스에 저장"""
        stats = {
            'total': 0,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }
        
        for symbol, daily_prices in all_daily_prices.items():
            stats['total'] += len(daily_prices)
            if not daily_prices:
                logger.warning(f"No daily price data to save ")
                continue
            try:
                with self.db_manager as db:
                    logger.info(f"Starting to save {len(daily_prices)} daily price records ...")

                    try:
                        success = db.upsert_daily_prices(daily_prices)
                        if success:
                            stats['saved'] += 1
                        else:
                            stats['failed'] += 1
                            logger.warning(f"Failed to save price data ")
                    except Exception as e:
                        stats['failed'] += 1
                        logger.error(f"Error processing price data : {e}")

                    logger.info(f"Daily prices save completed ")
            except Exception as e:
                logger.error(f"Database operation failed : {e}")
                stats['failed'] += 1

        return stats
    
    def _save_news_to_database(self, news_list: list) -> dict:
        """뉴스 기사와 뉴스-종목 연결을 데이터베이스에 저장"""
        stats = {
            'total': 0,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }

        if not news_list:
            stats['failed'] += 1
            return stats
        
        with self.db_manager as db:
            url_to_id = db.upsert_news_articles(news_list)
            print(url_to_id)
            news_stocks = []
            stats['total'] += len(news_list)

            for news in news_list:
                news_id = url_to_id.get(news['url'])

                if not news_id:
                    continue
                
                for ts in news.get('ticker_sentiment', []):
                    if ts['ticker'] in self.symbols:
                        news_stocks.append({
                            'news_id': news_id,
                            'symbol': ts['ticker'],
                            'relevance_score': ts['relevance_score'],
                            'sentiment_score': ts['sentiment_score'],
                            'sentiment_label': ts['sentiment_label']
                        })

            if news_stocks:
                success = db.upsert_news_stocks(news_stocks)
                if success:
                    stats['saved'] += 1
                else:
                    stats['failed'] += 1
            else:
                stats['failed'] += 1

        return stats

    ###############
    # Weekly Sync #
    ###############
    def _save_stocks_to_database(self, stock_listings: List[Dict[str, Any]]) -> Dict[str, int]:
        """주식 종목 리스트를 데이터베이스에 저장합니다"""
        stats = {
            'total': 1,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }
        
        try:
            with self.db_manager as db:
                logger.info(f"Starting to save {stats['total']} stock listings to database...")
                
                try:
                    success = db.upsert_stock(stock_listings)
                    
                    if success:
                        stats['saved'] += 1
                    else:
                        stats['failed'] += 1
                        logger.warning(f"Failed to save stock data")    
                except Exception as e:
                    stats['failed'] += 1
                    logger.error(f"Error processing stock")
                
                logger.info(f"Stock listing save completed: {stats}")
        except Exception as e:
            logger.error(f"Database operation failed: {e}")
            raise
            
        return stats

    def _save_companies_overview_to_database(self, all_companies_overview: List[Dict[str, Any]]) -> Dict[str, int]:
        """여러 symbol의 기업 별 정보 데이터를 데이터베이스에 저장"""
        stats = {
            'total': 1,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }
        
        if not all_companies_overview:
            logger.warning(f"No company overview to save")
        try:
            with self.db_manager as db:
                logger.info(f"Starting to save {len(all_companies_overview)} companies overview records...")

                try:
                    success = db.upsert_companies_overview(all_companies_overview)
                    if success:
                        stats['saved'] += 1
                    else:
                        stats['failed'] += 1
                        logger.warning(f"Failed to save company overview")
                except Exception as e:
                    stats['failed'] += 1
                    logger.error(f"Error processing company overview: {e}")

                logger.info(f"Company overview save completed: {stats}")
        except Exception as e:
            logger.error(f"Database operation failed: {e}")
            stats['failed'] += 1

        return stats
    

    ##################
    # Quarterly Sync #
    ##################
    def _save_income_statements_to_database(self, all_income_statements: dict) -> Dict[str, int]:
        """여러 symbol의 손익계산서(연간/분기) 데이터를 데이터베이스에 저장"""
        stats = {
            'total': 0,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }
        bulk_data = []
        for symbol, data in all_income_statements.items():
            if not data or "symbol" not in data:
                logger.warning(f"No income statement data to save ")
                stats['failed'] += 1
                continue
            # 연간 보고서
            for report in data.get("annualReports", []):
                report["is_quarterly"] = False
                bulk_data.append(report)
            # 분기별 보고서
            for report in data.get("quarterlyReports", []):
                report["is_quarterly"] = True
                bulk_data.append(report)
        stats['total'] = len(bulk_data)
        try:
            with self.db_manager as db:
                success = db.upsert_income_statements(bulk_data)
                if success:
                    stats['saved'] = len(bulk_data)
                else:
                    stats['failed'] += len(bulk_data)
        except Exception as e:
            logger.error(f"Database operation failed: {e}")
            stats['failed'] += len(bulk_data)
        return stats

    def _save_balance_sheets_to_database(self, all_balance_sheets: dict) -> Dict[str, int]:
        """여러 symbol의 대차대조표(연간/분기) 데이터를 데이터베이스에 저장"""
        stats = {
            'total': 0,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }
        bulk_data = []
        for symbol, data in all_balance_sheets.items():
            if not data or "symbol" not in data:
                logger.warning(f"No balance sheet data to save ")
                stats['failed'] += 1
                continue
            # 연간 보고서
            for report in data.get("annualReports", []):
                report["is_quarterly"] = False
                bulk_data.append(report)
            # 분기별 보고서
            for report in data.get("quarterlyReports", []):
                report["is_quarterly"] = True
                bulk_data.append(report)
        stats['total'] = len(bulk_data)
        try:
            with self.db_manager as db:
                success = db.upsert_balance_sheets(bulk_data)
                if success:
                    stats['saved'] = len(bulk_data)
                else:
                    stats['failed'] += len(bulk_data)
        except Exception as e:
            logger.error(f"Database operation failed: {e}")
            stats['failed'] += len(bulk_data)
        return stats

    def _save_cash_flows_to_database(self, all_cash_flows: dict) -> Dict[str, int]:
        """여러 symbol의 현금흐름표(연간/분기) 데이터를 데이터베이스에 저장"""
        stats = {
            'total': 0,
            'saved': 0,
            'failed': 0,
            'skipped': 0
        }
        bulk_data = []
        for symbol, data in all_cash_flows.items():
            if not data or "symbol" not in data:
                logger.warning(f"No cash flow data to save ")
                stats['failed'] += 1
                continue
            # 연간 보고서
            for report in data.get("annualReports", []):
                report["is_quarterly"] = False
                bulk_data.append(report)
            # 분기별 보고서
            for report in data.get("quarterlyReports", []):
                report["is_quarterly"] = True
                bulk_data.append(report)
        stats['total'] = len(bulk_data)
        try:
            with self.db_manager as db:
                success = db.upsert_cash_flows(bulk_data)
                if success:
                    stats['saved'] = len(bulk_data)
                else:
                    stats['failed'] += len(bulk_data)
        except Exception as e:
            logger.error(f"Database operation failed: {e}")
            stats['failed'] += len(bulk_data)
        return stats