-- 주식 종목 정보 테이블
CREATE TABLE stocks (
    symbol VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    exchange VARCHAR(20) NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    ipo_date DATE,
    delisting_date DATE,
    status VARCHAR(20) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 일별 주가 데이터 테이블 (캔들차트용)
CREATE TABLE daily_prices (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) REFERENCES stocks(symbol),
    date DATE NOT NULL,
    open DECIMAL(18, 4) NOT NULL,
    high DECIMAL(18, 4) NOT NULL,
    low DECIMAL(18, 4) NOT NULL,
    close DECIMAL(18, 4) NOT NULL,
    volume BIGINT NOT NULL,
    UNIQUE(symbol, date)
);

-- 기업 기본 정보 테이블
CREATE TABLE company_overview (
    symbol VARCHAR(10) PRIMARY KEY REFERENCES stocks(symbol),
    description TEXT,
    currency VARCHAR(10),
    country VARCHAR(50),
    sector VARCHAR(100),
    industry VARCHAR(100),
    address TEXT,
    fiscal_year_end VARCHAR(10),
    latest_quarter DATE,
    market_capitalization BIGINT,
    ebitda BIGINT,
    pe_ratio DECIMAL(12, 4),
    peg_ratio DECIMAL(12, 4),
    book_value DECIMAL(12, 4),
    dividend_per_share DECIMAL(12, 4),
    dividend_yield DECIMAL(12, 4),
    eps DECIMAL(12, 4),
    revenue_per_share_ttm DECIMAL(12, 4),
    profit_margin DECIMAL(12, 4),
    operating_margin_ttm DECIMAL(12, 4),
    return_on_assets_ttm DECIMAL(12, 4),
    return_on_equity_ttm DECIMAL(12, 4),
    revenue_ttm BIGINT,
    gross_profit_ttm BIGINT,
    diluted_eps_ttm DECIMAL(12, 4),
    quarterly_earnings_growth_yoy DECIMAL(12, 4),
    quarterly_revenue_growth_yoy DECIMAL(12, 4),
    analyst_target_price DECIMAL(12, 4),
    trailing_pe DECIMAL(12, 4),
    forward_pe DECIMAL(12, 4),
    price_to_sales_ratio_ttm DECIMAL(12, 4),
    price_to_book_ratio DECIMAL(12, 4),
    ev_to_revenue DECIMAL(12, 4),
    ev_to_ebitda DECIMAL(12, 4),
    beta DECIMAL(12, 4),
    fifty_two_week_high DECIMAL(12, 4),
    fifty_two_week_low DECIMAL(12, 4),
    fifty_day_moving_average DECIMAL(12, 4),
    two_hundred_day_moving_average DECIMAL(12, 4),
    shares_outstanding BIGINT,
    shares_float BIGINT,
    shares_short BIGINT,
    shares_short_prior_month BIGINT,
    short_ratio DECIMAL(12, 4),
    short_percent_outstanding DECIMAL(12, 4),
    short_percent_float DECIMAL(12, 4),
    percent_insiders DECIMAL(12, 4),
    percent_institutions DECIMAL(12, 4),
    forward_annual_dividend_rate DECIMAL(12, 4),
    forward_annual_dividend_yield DECIMAL(12, 4),
    payout_ratio DECIMAL(12, 4),
    dividend_date DATE,
    ex_dividend_date DATE,
    last_split_factor VARCHAR(20),
    last_split_date DATE,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 재무제표 - 손익계산서
CREATE TABLE income_statements (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) REFERENCES stocks(symbol),
    fiscal_date_ending DATE NOT NULL,
    reported_currency VARCHAR(10),
    gross_profit BIGINT,
    total_revenue BIGINT,
    cost_of_revenue BIGINT,
    cost_of_goods_and_services_sold BIGINT,
    operating_income BIGINT,
    selling_general_and_administrative BIGINT,
    research_and_development BIGINT,
    operating_expenses BIGINT,
    investment_income_net BIGINT,
    net_interest_income BIGINT,
    interest_income BIGINT,
    interest_expense BIGINT,
    non_interest_income BIGINT,
    other_non_operating_income BIGINT,
    depreciation BIGINT,
    depreciation_and_amortization BIGINT,
    income_before_tax BIGINT,
    income_tax_expense BIGINT,
    interest_and_debt_expense BIGINT,
    net_income_from_continuing_operations BIGINT,
    comprehensive_income_net_of_tax BIGINT,
    ebit BIGINT,
    ebitda BIGINT,
    net_income BIGINT,
    is_quarterly BOOLEAN NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(symbol, fiscal_date_ending, is_quarterly)
);

-- 재무제표 - 대차대조표
CREATE TABLE balance_sheets (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) REFERENCES stocks(symbol),
    fiscal_date_ending DATE NOT NULL,
    reported_currency VARCHAR(10),
    total_assets BIGINT,
    total_current_assets BIGINT,
    cash_and_cash_equivalents_at_carrying_value BIGINT,
    cash_and_short_term_investments BIGINT,
    inventory BIGINT,
    current_net_receivables BIGINT,
    total_non_current_assets BIGINT,
    property_plant_equipment BIGINT,
    accumulated_depreciation_amortization_ppe BIGINT,
    intangible_assets BIGINT,
    intangible_assets_excluding_goodwill BIGINT,
    goodwill BIGINT,
    investments BIGINT,
    long_term_investments BIGINT,
    short_term_investments BIGINT,
    other_current_assets BIGINT,
    other_non_current_assets BIGINT,
    total_liabilities BIGINT,
    total_current_liabilities BIGINT,
    current_accounts_payable BIGINT,
    deferred_revenue BIGINT,
    current_debt BIGINT,
    short_term_debt BIGINT,
    total_non_current_liabilities BIGINT,
    capital_lease_obligations BIGINT,
    long_term_debt BIGINT,
    current_long_term_debt BIGINT,
    long_term_debt_noncurrent BIGINT,
    short_long_term_debt_total BIGINT,
    other_current_liabilities BIGINT,
    other_non_current_liabilities BIGINT,
    total_shareholder_equity BIGINT,
    treasury_stock BIGINT,
    retained_earnings BIGINT,
    common_stock BIGINT,
    common_stock_shares_outstanding BIGINT,
    is_quarterly BOOLEAN NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(symbol, fiscal_date_ending, is_quarterly)
);

-- 재무제표 - 현금흐름표
CREATE TABLE cash_flows (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) REFERENCES stocks(symbol),
    fiscal_date_ending DATE NOT NULL,
    reported_currency VARCHAR(10),
    operating_cashflow BIGINT,
    payments_for_operating_activities BIGINT,
    proceeds_from_operating_activities BIGINT,
    change_in_operating_liabilities BIGINT,
    change_in_operating_assets BIGINT,
    depreciation_depletion_and_amortization BIGINT,
    capital_expenditures BIGINT,
    change_in_receivables BIGINT,
    change_in_inventory BIGINT,
    profit_loss BIGINT,
    cashflow_from_investment BIGINT,
    cashflow_from_financing BIGINT,
    proceeds_from_repayments_of_short_term_debt BIGINT,
    payments_for_repurchase_of_common_stock BIGINT,
    payments_for_repurchase_of_equity BIGINT,
    payments_for_repurchase_of_preferred_stock BIGINT,
    dividend_payout BIGINT,
    dividend_payout_common_stock BIGINT,
    dividend_payout_preferred_stock BIGINT,
    proceeds_from_issuance_of_common_stock BIGINT,
    proceeds_from_issuance_of_long_term_debt_and_capital_securities BIGINT,
    proceeds_from_issuance_of_preferred_stock BIGINT,
    proceeds_from_repurchase_of_equity BIGINT,
    proceeds_from_sale_of_treasury_stock BIGINT,
    change_in_cash_and_cash_equivalents BIGINT,
    change_in_exchange_rate BIGINT,
    net_income BIGINT,
    is_quarterly BOOLEAN NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(symbol, fiscal_date_ending, is_quarterly)
);

-- 뉴스 정보 테이블
CREATE TABLE news_articles (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL UNIQUE,
    time_published TIMESTAMP WITH TIME ZONE NOT NULL,
    authors TEXT[],
    summary TEXT,
    source VARCHAR(100),
    category_within_source VARCHAR(100),
    source_domain VARCHAR(100),
    overall_sentiment_score DECIMAL(5, 4),
    overall_sentiment_label VARCHAR(20),
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 뉴스-종목 연결 테이블 (다대다 관계)
CREATE TABLE news_stocks (
    id SERIAL PRIMARY KEY,
    news_id INTEGER REFERENCES news_articles(id) ON DELETE CASCADE,
    symbol VARCHAR(10) REFERENCES stocks(symbol) ON DELETE CASCADE,
    relevance_score DECIMAL(5, 4),
    sentiment_score DECIMAL(5, 4),
    sentiment_label VARCHAR(20),
    UNIQUE(news_id, symbol)
);

-- 주식 추천 정보 테이블
CREATE TABLE stock_recommendations (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) REFERENCES stocks(symbol),
    recommendation_score DECIMAL(5, 4) NOT NULL, -- 1(강력 매도) ~ 10(강력 매수)
    recommendation_label VARCHAR(20) NOT NULL, -- "STRONG_SELL", "SELL", "HOLD", "BUY", "STRONG_BUY"
    summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (symbol)
);

-- 인덱스 생성
CREATE INDEX idx_daily_prices_symbol ON daily_prices(symbol);
CREATE INDEX idx_daily_prices_date ON daily_prices(date);
CREATE INDEX idx_news_stocks_symbol ON news_stocks(symbol);
CREATE INDEX idx_news_stocks_news_id ON news_stocks(news_id);
CREATE INDEX idx_stock_recommendations_symbol ON stock_recommendations(symbol);
CREATE INDEX idx_news_articles_time_published ON news_articles(time_published);
CREATE INDEX idx_income_statements_symbol_date ON income_statements(symbol, fiscal_date_ending);
CREATE INDEX idx_balance_sheets_symbol_date ON balance_sheets(symbol, fiscal_date_ending);
CREATE INDEX idx_cash_flows_symbol_date ON cash_flows(symbol, fiscal_date_ending);