import psycopg2
from psycopg2.extras import RealDictCursor, execute_values
import logging
from typing import Optional, Dict, Any, List
from config import Config

logger = logging.getLogger(__name__)

class DatabaseManager:
    def __init__(self):
        self.connection = None
        self.cursor = None
    
    def connect(self):
        """Establish database connection"""
        try:
            self.connection = psycopg2.connect(
                host=Config.POSTGRES_HOST,
                port=Config.POSTGRES_PORT,
                database=Config.POSTGRES_DATABASE,
                user=Config.POSTGRES_USER,
                password=Config.POSTGRES_PASSWORD,
                cursor_factory=RealDictCursor
            )
            self.cursor = self.connection.cursor()
            logger.info("Database connection established")
        except Exception as e:
            logger.error(f"Failed to connect to database: {e}")
            raise
    
    def disconnect(self):
        """Close database connection"""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        logger.info("Database connection closed")
    
    def execute_query(self, query: str, params: Optional[tuple] = None) -> List[Dict]:
        """Execute a SELECT query and return results"""
        try:
            self.cursor.execute(query, params)
            return self.cursor.fetchall()
        except Exception as e:
            logger.error(f"Query execution failed: {e}")
            raise
    
    def execute_upsert(self, query: str, params: tuple) -> bool:
        """Execute an UPSERT query (INSERT ... ON CONFLICT ... DO UPDATE)"""
        try:
            self.cursor.execute(query, params)
            self.connection.commit()
            return True
        except Exception as e:
            logger.error(f"Upsert operation failed: {e}")
            self.connection.rollback()
            return False
    
    def upsert_daily_prices(self, daily_prices: List[Dict[str, Any]]) -> bool:
        """Insert or update stock daily price data"""
        if not daily_prices:
            return True
        
        try:
            query = """
                INSERT INTO daily_prices (
                    symbol, date, open, high, low, close, volume
                )
                VALUES %s
                ON CONFLICT (symbol, date) DO UPDATE SET
                    open = EXCLUDED.open,
                    high = EXCLUDED.high,
                    low = EXCLUDED.low,
                    close = EXCLUDED.close,
                    volume = EXCLUDED.volume
            """
            
            # 데이터를 tuple 리스트로 변환
            values_list = [
                (
                    item['symbol'],
                    item['date'],
                    item['open'],
                    item['high'],
                    item['low'],
                    item['close'],
                    item['volume']
                )
                for item in daily_prices
            ]
            
            execute_values(
                self.cursor,
                query,
                values_list,
                template=None,
                page_size=1000
            )
            
            self.connection.commit()
            logger.info(f"Upserted {len(daily_prices)} daily price records")
            return True
            
        except Exception as e:
            logger.error(f"Upsert daily prices failed: {e}")
            self.connection.rollback()
            return False
        
    def upsert_stock(self, stock_data: List[Dict[str, Any]]) -> bool:
        """Insert or update stock data"""
        if not stock_data:
            return True
        
        try:
            query = """
                INSERT INTO stocks (symbol, name, exchange, asset_type, ipo_date, 
                                delisting_date, status)
                VALUES %s
                ON CONFLICT (symbol) DO UPDATE SET
                    name = EXCLUDED.name,
                    exchange = EXCLUDED.exchange,
                    asset_type = EXCLUDED.asset_type,
                    ipo_date = EXCLUDED.ipo_date,
                    delisting_date = EXCLUDED.delisting_date,
                    status = EXCLUDED.status,
                    last_updated = CURRENT_TIMESTAMP
            """

            values_list = [
                (
                    item['symbol'],
                    item['name'],
                    item['exchange'],
                    item['asset_type'],
                    item['ipo_date'],
                    item['delisting_date'],
                    item['status']
                )
                for item in stock_data
            ]
            
            execute_values(
                self.cursor,
                query,
                values_list,
                template=None,
                page_size=1000
            )
            
            self.connection.commit()
            logger.info(f"Upserted {len(stock_data)} stock data records")
            return True
            
        except Exception as e:
            logger.error(f"Upsert stock data failed: {e}")
            self.connection.rollback()
            return False
        
    def upsert_companies_overview(self, companies_data: List[Dict[str, Any]]) -> bool:
        """기업 기본 정보를 bulk upsert로 데이터베이스에 저장"""
        if not companies_data:
            return True
        
        try:    
            query = """
                INSERT INTO company_overview (
                    symbol, description, currency, country, sector, industry, address,
                    fiscal_year_end, latest_quarter, market_capitalization, ebitda,
                    pe_ratio, peg_ratio, book_value, dividend_per_share, dividend_yield,
                    eps, revenue_per_share_ttm, profit_margin, operating_margin_ttm,
                    return_on_assets_ttm, return_on_equity_ttm, revenue_ttm, gross_profit_ttm,
                    diluted_eps_ttm, quarterly_earnings_growth_yoy, quarterly_revenue_growth_yoy,
                    analyst_target_price, trailing_pe, forward_pe, price_to_sales_ratio_ttm,
                    price_to_book_ratio, ev_to_revenue, ev_to_ebitda, beta,
                    fifty_two_week_high, fifty_two_week_low, fifty_day_moving_average,
                    two_hundred_day_moving_average, shares_outstanding, shares_float,
                    shares_short, shares_short_prior_month, short_ratio,
                    short_percent_outstanding, short_percent_float, percent_insiders,
                    percent_institutions, forward_annual_dividend_rate,
                    forward_annual_dividend_yield, payout_ratio, dividend_date,
                    ex_dividend_date, last_split_factor, last_split_date
                )
                VALUES %s
                ON CONFLICT (symbol) DO UPDATE SET
                    description = EXCLUDED.description,
                    currency = EXCLUDED.currency,
                    country = EXCLUDED.country,
                    sector = EXCLUDED.sector,
                    industry = EXCLUDED.industry,
                    address = EXCLUDED.address,
                    fiscal_year_end = EXCLUDED.fiscal_year_end,
                    latest_quarter = EXCLUDED.latest_quarter,
                    market_capitalization = EXCLUDED.market_capitalization,
                    ebitda = EXCLUDED.ebitda,
                    pe_ratio = EXCLUDED.pe_ratio,
                    peg_ratio = EXCLUDED.peg_ratio,
                    book_value = EXCLUDED.book_value,
                    dividend_per_share = EXCLUDED.dividend_per_share,
                    dividend_yield = EXCLUDED.dividend_yield,
                    eps = EXCLUDED.eps,
                    revenue_per_share_ttm = EXCLUDED.revenue_per_share_ttm,
                    profit_margin = EXCLUDED.profit_margin,
                    operating_margin_ttm = EXCLUDED.operating_margin_ttm,
                    return_on_assets_ttm = EXCLUDED.return_on_assets_ttm,
                    return_on_equity_ttm = EXCLUDED.return_on_equity_ttm,
                    revenue_ttm = EXCLUDED.revenue_ttm,
                    gross_profit_ttm = EXCLUDED.gross_profit_ttm,
                    diluted_eps_ttm = EXCLUDED.diluted_eps_ttm,
                    quarterly_earnings_growth_yoy = EXCLUDED.quarterly_earnings_growth_yoy,
                    quarterly_revenue_growth_yoy = EXCLUDED.quarterly_revenue_growth_yoy,
                    analyst_target_price = EXCLUDED.analyst_target_price,
                    trailing_pe = EXCLUDED.trailing_pe,
                    forward_pe = EXCLUDED.forward_pe,
                    price_to_sales_ratio_ttm = EXCLUDED.price_to_sales_ratio_ttm,
                    price_to_book_ratio = EXCLUDED.price_to_book_ratio,
                    ev_to_revenue = EXCLUDED.ev_to_revenue,
                    ev_to_ebitda = EXCLUDED.ev_to_ebitda,
                    beta = EXCLUDED.beta,
                    fifty_two_week_high = EXCLUDED.fifty_two_week_high,
                    fifty_two_week_low = EXCLUDED.fifty_two_week_low,
                    fifty_day_moving_average = EXCLUDED.fifty_day_moving_average,
                    two_hundred_day_moving_average = EXCLUDED.two_hundred_day_moving_average,
                    shares_outstanding = EXCLUDED.shares_outstanding,
                    shares_float = EXCLUDED.shares_float,
                    shares_short = EXCLUDED.shares_short,
                    shares_short_prior_month = EXCLUDED.shares_short_prior_month,
                    short_ratio = EXCLUDED.short_ratio,
                    short_percent_outstanding = EXCLUDED.short_percent_outstanding,
                    short_percent_float = EXCLUDED.short_percent_float,
                    percent_insiders = EXCLUDED.percent_insiders,
                    percent_institutions = EXCLUDED.percent_institutions,
                    forward_annual_dividend_rate = EXCLUDED.forward_annual_dividend_rate,
                    forward_annual_dividend_yield = EXCLUDED.forward_annual_dividend_yield,
                    payout_ratio = EXCLUDED.payout_ratio,
                    dividend_date = EXCLUDED.dividend_date,
                    ex_dividend_date = EXCLUDED.ex_dividend_date,
                    last_split_factor = EXCLUDED.last_split_factor,
                    last_split_date = EXCLUDED.last_split_date,
                    last_updated = CURRENT_TIMESTAMP
            """
            
            # 데이터를 tuple 리스트로 변환 (모든 필드 순서대로)
            values_list = []
            for item in companies_data:
                values_tuple = (
                    item.get('symbol'), item.get('description'), item.get('currency'),
                    item.get('country'), item.get('sector'), item.get('industry'),
                    item.get('address'), item.get('fiscal_year_end'), item.get('latest_quarter'),
                    item.get('market_capitalization'), item.get('ebitda'), item.get('pe_ratio'),
                    item.get('peg_ratio'), item.get('book_value'), item.get('dividend_per_share'),
                    item.get('dividend_yield'), item.get('eps'), item.get('revenue_per_share_ttm'),
                    item.get('profit_margin'), item.get('operating_margin_ttm'),
                    item.get('return_on_assets_ttm'), item.get('return_on_equity_ttm'),
                    item.get('revenue_ttm'), item.get('gross_profit_ttm'), item.get('diluted_eps_ttm'),
                    item.get('quarterly_earnings_growth_yoy'), item.get('quarterly_revenue_growth_yoy'),
                    item.get('analyst_target_price'), item.get('trailing_pe'), item.get('forward_pe'),
                    item.get('price_to_sales_ratio_ttm'), item.get('price_to_book_ratio'),
                    item.get('ev_to_revenue'), item.get('ev_to_ebitda'), item.get('beta'),
                    item.get('fifty_two_week_high'), item.get('fifty_two_week_low'),
                    item.get('fifty_day_moving_average'), item.get('two_hundred_day_moving_average'),
                    item.get('shares_outstanding'), item.get('shares_float'), item.get('shares_short'),
                    item.get('shares_short_prior_month'), item.get('short_ratio'),
                    item.get('short_percent_outstanding'), item.get('short_percent_float'),
                    item.get('percent_insiders'), item.get('percent_institutions'),
                    item.get('forward_annual_dividend_rate'), item.get('forward_annual_dividend_yield'),
                    item.get('payout_ratio'), item.get('dividend_date'), item.get('ex_dividend_date'),
                    item.get('last_split_factor'), item.get('last_split_date')
                )
                values_list.append(values_tuple)
            
            execute_values(
                self.cursor,
                query,
                values_list,
                template=None,
                page_size=500
            )
            
            self.connection.commit()
            logger.info(f"Upserted {len(companies_data)} companies data records")
            return True
            
        except Exception as e:
            logger.error(f"Upsert companies data failed: {e}")
            self.connection.rollback()
            return False
    
    def upsert_income_statements(self, statements: List[Dict[str, Any]]) -> bool:
        """손익계산서 여러 건을 bulk upsert"""
        if not statements:
            return True
        
        try:
            query = '''
                INSERT INTO income_statements (
                    symbol, fiscal_date_ending, reported_currency, gross_profit, total_revenue,
                    cost_of_revenue, cost_of_goods_and_services_sold, operating_income,
                    selling_general_and_administrative, research_and_development, operating_expenses,
                    investment_income_net, net_interest_income, interest_income, interest_expense,
                    non_interest_income, other_non_operating_income, depreciation, depreciation_and_amortization,
                    income_before_tax, income_tax_expense, interest_and_debt_expense,
                    net_income_from_continuing_operations, comprehensive_income_net_of_tax, ebit, ebitda,
                    net_income, is_quarterly
                ) VALUES %s
                ON CONFLICT (symbol, fiscal_date_ending, is_quarterly) DO UPDATE SET
                    reported_currency = EXCLUDED.reported_currency,
                    gross_profit = EXCLUDED.gross_profit,
                    total_revenue = EXCLUDED.total_revenue,
                    cost_of_revenue = EXCLUDED.cost_of_revenue,
                    cost_of_goods_and_services_sold = EXCLUDED.cost_of_goods_and_services_sold,
                    operating_income = EXCLUDED.operating_income,
                    selling_general_and_administrative = EXCLUDED.selling_general_and_administrative,
                    research_and_development = EXCLUDED.research_and_development,
                    operating_expenses = EXCLUDED.operating_expenses,
                    investment_income_net = EXCLUDED.investment_income_net,
                    net_interest_income = EXCLUDED.net_interest_income,
                    interest_income = EXCLUDED.interest_income,
                    interest_expense = EXCLUDED.interest_expense,
                    non_interest_income = EXCLUDED.non_interest_income,
                    other_non_operating_income = EXCLUDED.other_non_operating_income,
                    depreciation = EXCLUDED.depreciation,
                    depreciation_and_amortization = EXCLUDED.depreciation_and_amortization,
                    income_before_tax = EXCLUDED.income_before_tax,
                    income_tax_expense = EXCLUDED.income_tax_expense,
                    interest_and_debt_expense = EXCLUDED.interest_and_debt_expense,
                    net_income_from_continuing_operations = EXCLUDED.net_income_from_continuing_operations,
                    comprehensive_income_net_of_tax = EXCLUDED.comprehensive_income_net_of_tax,
                    ebit = EXCLUDED.ebit,
                    ebitda = EXCLUDED.ebitda,
                    net_income = EXCLUDED.net_income,
                    last_updated = CURRENT_TIMESTAMP
            '''

            values_list = [
                (
                    s['symbol'], s['fiscal_date_ending'], s['reported_currency'], s['gross_profit'],
                    s['total_revenue'], s['cost_of_revenue'], s['cost_of_goods_and_services_sold'],
                    s['operating_income'], s['selling_general_and_administrative'], s['research_and_development'],
                    s['operating_expenses'], s['investment_income_net'], s['net_interest_income'],
                    s['interest_income'], s['interest_expense'], s['non_interest_income'],
                    s['other_non_operating_income'], s['depreciation'], s['depreciation_and_amortization'],
                    s['income_before_tax'], s['income_tax_expense'], s['interest_and_debt_expense'],
                    s['net_income_from_continuing_operations'], s['comprehensive_income_net_of_tax'],
                    s['ebit'], s['ebitda'], s['net_income'], s['is_quarterly']
                )
                for s in statements
            ]
            
            execute_values(self.cursor, query, values_list, page_size=500)
            self.connection.commit()
            logger.info(f"Upserted {len(statements)} income statement records")
            return True
        except Exception as e:
            logger.error(f"Upsert income statements failed: {e}")
            self.connection.rollback()
            return False
    
    def upsert_balance_sheets(self, sheets: List[Dict[str, Any]]) -> bool:
        """대차대조표 여러 건을 bulk upsert"""
        if not sheets:
            return True
        try:
            query = '''
                INSERT INTO balance_sheets (
                    symbol, fiscal_date_ending, reported_currency, total_assets, total_current_assets,
                    cash_and_cash_equivalents_at_carrying_value, cash_and_short_term_investments, inventory,
                    current_net_receivables, total_non_current_assets, property_plant_equipment,
                    accumulated_depreciation_amortization_ppe, intangible_assets, intangible_assets_excluding_goodwill,
                    goodwill, investments, long_term_investments, short_term_investments, other_current_assets,
                    other_non_current_assets, total_liabilities, total_current_liabilities, current_accounts_payable,
                    deferred_revenue, current_debt, short_term_debt, total_non_current_liabilities, capital_lease_obligations,
                    long_term_debt, current_long_term_debt, long_term_debt_noncurrent, short_long_term_debt_total,
                    other_current_liabilities, other_non_current_liabilities, total_shareholder_equity, treasury_stock,
                    retained_earnings, common_stock, common_stock_shares_outstanding, is_quarterly
                ) VALUES %s
                ON CONFLICT (symbol, fiscal_date_ending, is_quarterly) DO UPDATE SET
                    reported_currency = EXCLUDED.reported_currency,
                    total_assets = EXCLUDED.total_assets,
                    total_current_assets = EXCLUDED.total_current_assets,
                    cash_and_cash_equivalents_at_carrying_value = EXCLUDED.cash_and_cash_equivalents_at_carrying_value,
                    cash_and_short_term_investments = EXCLUDED.cash_and_short_term_investments,
                    inventory = EXCLUDED.inventory,
                    current_net_receivables = EXCLUDED.current_net_receivables,
                    total_non_current_assets = EXCLUDED.total_non_current_assets,
                    property_plant_equipment = EXCLUDED.property_plant_equipment,
                    accumulated_depreciation_amortization_ppe = EXCLUDED.accumulated_depreciation_amortization_ppe,
                    intangible_assets = EXCLUDED.intangible_assets,
                    intangible_assets_excluding_goodwill = EXCLUDED.intangible_assets_excluding_goodwill,
                    goodwill = EXCLUDED.goodwill,
                    investments = EXCLUDED.investments,
                    long_term_investments = EXCLUDED.long_term_investments,
                    short_term_investments = EXCLUDED.short_term_investments,
                    other_current_assets = EXCLUDED.other_current_assets,
                    other_non_current_assets = EXCLUDED.other_non_current_assets,
                    total_liabilities = EXCLUDED.total_liabilities,
                    total_current_liabilities = EXCLUDED.total_current_liabilities,
                    current_accounts_payable = EXCLUDED.current_accounts_payable,
                    deferred_revenue = EXCLUDED.deferred_revenue,
                    current_debt = EXCLUDED.current_debt,
                    short_term_debt = EXCLUDED.short_term_debt,
                    total_non_current_liabilities = EXCLUDED.total_non_current_liabilities,
                    capital_lease_obligations = EXCLUDED.capital_lease_obligations,
                    long_term_debt = EXCLUDED.long_term_debt,
                    current_long_term_debt = EXCLUDED.current_long_term_debt,
                    long_term_debt_noncurrent = EXCLUDED.long_term_debt_noncurrent,
                    short_long_term_debt_total = EXCLUDED.short_long_term_debt_total,
                    other_current_liabilities = EXCLUDED.other_current_liabilities,
                    other_non_current_liabilities = EXCLUDED.other_non_current_liabilities,
                    total_shareholder_equity = EXCLUDED.total_shareholder_equity,
                    treasury_stock = EXCLUDED.treasury_stock,
                    retained_earnings = EXCLUDED.retained_earnings,
                    common_stock = EXCLUDED.common_stock,
                    common_stock_shares_outstanding = EXCLUDED.common_stock_shares_outstanding,
                    last_updated = CURRENT_TIMESTAMP
            '''
            
            values_list = [
                (
                    s['symbol'], s['fiscal_date_ending'], s['reported_currency'], s['total_assets'], s['total_current_assets'],
                    s['cash_and_cash_equivalents_at_carrying_value'], s['cash_and_short_term_investments'], s['inventory'],
                    s['current_net_receivables'], s['total_non_current_assets'], s['property_plant_equipment'],
                    s['accumulated_depreciation_amortization_ppe'], s['intangible_assets'], s['intangible_assets_excluding_goodwill'],
                    s['goodwill'], s['investments'], s['long_term_investments'], s['short_term_investments'], s['other_current_assets'],
                    s['other_non_current_assets'], s['total_liabilities'], s['total_current_liabilities'], s['current_accounts_payable'],
                    s['deferred_revenue'], s['current_debt'], s['short_term_debt'], s['total_non_current_liabilities'], s['capital_lease_obligations'],
                    s['long_term_debt'], s['current_long_term_debt'], s['long_term_debt_noncurrent'], s['short_long_term_debt_total'],
                    s['other_current_liabilities'], s['other_non_current_liabilities'], s['total_shareholder_equity'], s['treasury_stock'],
                    s['retained_earnings'], s['common_stock'], s['common_stock_shares_outstanding'], s['is_quarterly']
                )
                for s in sheets
            ]

            execute_values(self.cursor, query, values_list, page_size=500)
            self.connection.commit()
            logger.info(f"Upserted {len(sheets)} balance sheet records")
            return True
        except Exception as e:
            logger.error(f"Upsert balance sheets failed: {e}")
            self.connection.rollback()
            return False

    def upsert_cash_flows(self, flows: List[Dict[str, Any]]) -> bool:
        """현금흐름표 여러 건을 bulk upsert"""
        if not flows:
            return True
        try:
            query = '''
                INSERT INTO cash_flows (
                    symbol, fiscal_date_ending, reported_currency, operating_cashflow, payments_for_operating_activities,
                    proceeds_from_operating_activities, change_in_operating_liabilities, change_in_operating_assets,
                    depreciation_depletion_and_amortization, capital_expenditures, change_in_receivables, change_in_inventory,
                    profit_loss, cashflow_from_investment, cashflow_from_financing, proceeds_from_repayments_of_short_term_debt,
                    payments_for_repurchase_of_common_stock, payments_for_repurchase_of_equity, payments_for_repurchase_of_preferred_stock,
                    dividend_payout, dividend_payout_common_stock, dividend_payout_preferred_stock, proceeds_from_issuance_of_common_stock,
                    proceeds_from_issuance_of_long_term_debt_and_capital_securities, proceeds_from_issuance_of_preferred_stock,
                    proceeds_from_repurchase_of_equity, proceeds_from_sale_of_treasury_stock, change_in_cash_and_cash_equivalents,
                    change_in_exchange_rate, net_income, is_quarterly
                ) VALUES %s
                ON CONFLICT (symbol, fiscal_date_ending, is_quarterly) DO UPDATE SET
                    reported_currency = EXCLUDED.reported_currency,
                    operating_cashflow = EXCLUDED.operating_cashflow,
                    payments_for_operating_activities = EXCLUDED.payments_for_operating_activities,
                    proceeds_from_operating_activities = EXCLUDED.proceeds_from_operating_activities,
                    change_in_operating_liabilities = EXCLUDED.change_in_operating_liabilities,
                    change_in_operating_assets = EXCLUDED.change_in_operating_assets,
                    depreciation_depletion_and_amortization = EXCLUDED.depreciation_depletion_and_amortization,
                    capital_expenditures = EXCLUDED.capital_expenditures,
                    change_in_receivables = EXCLUDED.change_in_receivables,
                    change_in_inventory = EXCLUDED.change_in_inventory,
                    profit_loss = EXCLUDED.profit_loss,
                    cashflow_from_investment = EXCLUDED.cashflow_from_investment,
                    cashflow_from_financing = EXCLUDED.cashflow_from_financing,
                    proceeds_from_repayments_of_short_term_debt = EXCLUDED.proceeds_from_repayments_of_short_term_debt,
                    payments_for_repurchase_of_common_stock = EXCLUDED.payments_for_repurchase_of_common_stock,
                    payments_for_repurchase_of_equity = EXCLUDED.payments_for_repurchase_of_equity,
                    payments_for_repurchase_of_preferred_stock = EXCLUDED.payments_for_repurchase_of_preferred_stock,
                    dividend_payout = EXCLUDED.dividend_payout,
                    dividend_payout_common_stock = EXCLUDED.dividend_payout_common_stock,
                    dividend_payout_preferred_stock = EXCLUDED.dividend_payout_preferred_stock,
                    proceeds_from_issuance_of_common_stock = EXCLUDED.proceeds_from_issuance_of_common_stock,
                    proceeds_from_issuance_of_long_term_debt_and_capital_securities = EXCLUDED.proceeds_from_issuance_of_long_term_debt_and_capital_securities,
                    proceeds_from_issuance_of_preferred_stock = EXCLUDED.proceeds_from_issuance_of_preferred_stock,
                    proceeds_from_repurchase_of_equity = EXCLUDED.proceeds_from_repurchase_of_equity,
                    proceeds_from_sale_of_treasury_stock = EXCLUDED.proceeds_from_sale_of_treasury_stock,
                    change_in_cash_and_cash_equivalents = EXCLUDED.change_in_cash_and_cash_equivalents,
                    change_in_exchange_rate = EXCLUDED.change_in_exchange_rate,
                    net_income = EXCLUDED.net_income,
                    last_updated = CURRENT_TIMESTAMP
            '''

            values_list = [
                (
                    f['symbol'], f['fiscal_date_ending'], f['reported_currency'], f['operating_cashflow'], f['payments_for_operating_activities'],
                    f['proceeds_from_operating_activities'], f['change_in_operating_liabilities'], f['change_in_operating_assets'],
                    f['depreciation_depletion_and_amortization'], f['capital_expenditures'], f['change_in_receivables'], f['change_in_inventory'],
                    f['profit_loss'], f['cashflow_from_investment'], f['cashflow_from_financing'], f['proceeds_from_repayments_of_short_term_debt'],
                    f['payments_for_repurchase_of_common_stock'], f['payments_for_repurchase_of_equity'], f['payments_for_repurchase_of_preferred_stock'],
                    f['dividend_payout'], f['dividend_payout_common_stock'], f['dividend_payout_preferred_stock'], f['proceeds_from_issuance_of_common_stock'],
                    f['proceeds_from_issuance_of_long_term_debt_and_capital_securities'], f['proceeds_from_issuance_of_preferred_stock'],
                    f['proceeds_from_repurchase_of_equity'], f['proceeds_from_sale_of_treasury_stock'], f['change_in_cash_and_cash_equivalents'],
                    f['change_in_exchange_rate'], f['net_income'], f['is_quarterly']
                )
                for f in flows
            ]

            execute_values(self.cursor, query, values_list, page_size=500)
            self.connection.commit()
            logger.info(f"Upserted {len(flows)} cash flow records")
            return True
        except Exception as e:
            logger.error(f"Upsert cash flows failed: {e}")
            self.connection.rollback()
            return False
    
    def upsert_news_articles(self, articles: list) -> dict:
        """뉴스 기사 여러 건을 bulk upsert, url→id 매핑 반환"""
        if not articles:
            return {}
        try:
            query = '''
                INSERT INTO news_articles (
                    title, url, time_published, authors, summary, source, category_within_source, source_domain,
                    overall_sentiment_score, overall_sentiment_label
                ) VALUES %s
                ON CONFLICT (url) DO UPDATE SET
                    title = EXCLUDED.title,
                    time_published = EXCLUDED.time_published,
                    authors = EXCLUDED.authors,
                    summary = EXCLUDED.summary,
                    source = EXCLUDED.source,
                    category_within_source = EXCLUDED.category_within_source,
                    source_domain = EXCLUDED.source_domain,
                    overall_sentiment_score = EXCLUDED.overall_sentiment_score,
                    overall_sentiment_label = EXCLUDED.overall_sentiment_label,
                    last_updated = CURRENT_TIMESTAMP
            '''
            values_list = [
                (
                    a['title'], a['url'], a['time_published'], a['authors'], a['summary'], a['source'],
                    a['category_within_source'], a['source_domain'], a['overall_sentiment_score'], a['overall_sentiment_label']
                )
                for a in articles
            ]

            execute_values(self.cursor, query, values_list, page_size=500)
            url_list = [a['url'] for a in articles]
            self.cursor.execute(
                "SELECT id, url FROM news_articles WHERE url = ANY(%s)", (url_list,)
            )
            rows = self.cursor.fetchall()
            self.connection.commit()
            url_to_id = {row['url']: row['id'] for row in rows}
            logger.info(f"Upserted {len(articles)} news articles")
            return url_to_id
        except Exception as e:
            logger.error(f"Upsert news articles failed: {e}")
            self.connection.rollback()
            return {}

    def upsert_news_stocks(self, news_stocks: list) -> bool:
        """뉴스-종목 연결 여러 건을 bulk upsert"""
        if not news_stocks:
            return True
        try:
            query = '''
                INSERT INTO news_stocks (
                    news_id, symbol, relevance_score, sentiment_score, sentiment_label
                ) VALUES %s
                ON CONFLICT (news_id, symbol) DO UPDATE SET
                    relevance_score = EXCLUDED.relevance_score,
                    sentiment_score = EXCLUDED.sentiment_score,
                    sentiment_label = EXCLUDED.sentiment_label
            '''
            values_list = [
                (
                    ns['news_id'], ns['symbol'], ns['relevance_score'], ns['sentiment_score'], ns['sentiment_label']
                )
                for ns in news_stocks
            ]
            execute_values(self.cursor, query, values_list, page_size=500)
            self.connection.commit()
            logger.info(f"Upserted {len(news_stocks)} news-stock links")
            return True
        except Exception as e:
            logger.error(f"Upsert news_stocks failed: {e}")
            self.connection.rollback()
            return False
    
    def __enter__(self):
        self.connect()
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.disconnect()