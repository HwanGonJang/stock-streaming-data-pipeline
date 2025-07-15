# Alpha Vantage API 명세서 및 데이터 동기화 가이드

## API 개요
- **기본 URL**: `https://www.alphavantage.co/query`
- **인증**: API 키를 파라미터로 전달 (`apikey=YOUR_API_KEY`)
- **요청 제한**: 무료 계정은 일 최대 25회, 분당 5회 (프리미엄 계정은 상이)
- **응답 형식**: JSON

## 0. 사용 주식 종목
AAPL,MSFT,GOOGL,AMZN,TSLA,META,NVDA,AVGO,CRM,ORCL,NFLX,ADBE,AMD,INTC,PYPL,CSCO,QCOM,TXN,AMAT,PLTR
위 20개의 주식 종목에 대해서만 데이터를 조회하여 동기화합니다.

## 1. 주식 종목 리스트 (LISTING_STATUS)

### API 정보
- **함수**: `LISTING_STATUS`
- **목적**: 모든 주식 종목 리스트 조회
- **동기화 주기**: 주 1회 (기업 상장/상장폐지 정보 업데이트)

### 요청
```
GET https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=YOUR_API_KEY
```

### 응답 예시
```csv
symbol,name,exchange,assetType,ipoDate,delistingDate,status
AAPL,Apple Inc,NYSE,Stock,1980-12-12,,Active
MSFT,Microsoft Corporation,NASDAQ,Stock,1986-03-13,,Active
...
```

### 저장 테이블
- `stocks` 테이블

### 매핑 로직
```python
# 예시 코드
import csv
import requests
from io import StringIO
import psycopg2

def sync_stock_listings():
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params={
            "function": "LISTING_STATUS",
            "apikey": "YOUR_API_KEY"
        }
    )
    
    # CSV 파싱
    csv_data = StringIO(response.text)
    reader = csv.DictReader(csv_data)
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 기존 데이터 삭제 또는 업데이트 로직
    for row in reader:
        # UPSERT 쿼리
        cursor.execute("""
            INSERT INTO stocks (symbol, name, exchange, asset_type, ipo_date, delisting_date, status)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (symbol) 
            DO UPDATE SET 
                name = EXCLUDED.name,
                exchange = EXCLUDED.exchange,
                asset_type = EXCLUDED.asset_type,
                ipo_date = EXCLUDED.ipo_date,
                delisting_date = EXCLUDED.delisting_date,
                status = EXCLUDED.status,
                last_updated = CURRENT_TIMESTAMP
        """, (
            row['symbol'], 
            row['name'], 
            row['exchange'], 
            row['assetType'],
            row['ipoDate'] if row['ipoDate'] else None,
            row['delistingDate'] if row['delistingDate'] else None,
            row['status']
        ))
    
    conn.commit()
    cursor.close()
    conn.close()
```

## 2. 일별 주가 데이터 (TIME_SERIES_DAILY)

### API 정보
- **함수**: `TIME_SERIES_DAILY`
- **목적**: 개별 종목의 일별 OHLCV 데이터 조회 (캔들차트용)
- **동기화 주기**: 일 1회 (장 마감 후)
- **파라미터**: 
  - `symbol`: 주식 심볼
  - `outputsize`: `full` (최대 20년) 또는 `compact` (최근 100일)

### 요청
```
GET https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=AAPL&outputsize=compact&apikey=YOUR_API_KEY
```

### 응답 예시
```json
{
    "Meta Data": {
        "1. Information": "Daily Prices (open, high, low, close) and Volumes",
        "2. Symbol": "AAPL",
        "3. Last Refreshed": "2025-07-10",
        "4. Output Size": "Compact",
        "5. Time Zone": "US/Eastern"
    },
    "Time Series (Daily)": {
        "2025-07-10": {
            "1. open": "191.5100",
            "2. high": "192.8700",
            "3. low": "190.2300",
            "4. close": "192.5800",
            "5. volume": "42994060"
        },
        "2025-07-09": {
            "1. open": "190.5000",
            "2. high": "191.9200",
            "3. low": "189.7800",
            "4. close": "191.4500",
            "5. volume": "45567700"
        },
        // ...and so on
    }
}
```

### 저장 테이블
- `daily_prices` 테이블

### 매핑 로직
```python
def sync_daily_prices(symbol):
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params={
            "function": "TIME_SERIES_DAILY",
            "symbol": symbol,
            "outputsize": "compact",  # 최근 100일만 가져오기
            "apikey": "YOUR_API_KEY"
        }
    )
    data = response.json()
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 데이터 파싱 및 저장
    time_series = data.get("Time Series (Daily)", {})
    for date, values in time_series.items():
        cursor.execute("""
            INSERT INTO daily_prices (symbol, date, open, high, low, close, volume)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (symbol, date) 
            DO UPDATE SET 
                open = EXCLUDED.open,
                high = EXCLUDED.high,
                low = EXCLUDED.low,
                close = EXCLUDED.close,
                volume = EXCLUDED.volume
        """, (
            symbol,
            date,
            float(values["1. open"]),
            float(values["2. high"]),
            float(values["3. low"]),
            float(values["4. close"]),
            int(values["5. volume"])
        ))
    
    conn.commit()
    cursor.close()
    conn.close()
```

## 3. 기업 기본 정보 (OVERVIEW)

### API 정보
- **함수**: `OVERVIEW`
- **목적**: 기업의 기본 정보 및 재무 지표 조회
- **동기화 주기**: 주 1회 (분기 보고서 발표 시기에는 더 자주)
- **파라미터**: `symbol`: 주식 심볼

### 요청
```
GET https://www.alphavantage.co/query?function=OVERVIEW&symbol=AAPL&apikey=YOUR_API_KEY
```

### 응답 예시
```json
{
    "Symbol": "AAPL",
    "AssetType": "Common Stock",
    "Name": "Apple Inc",
    "Description": "Apple Inc. designs, manufactures, and markets smartphones, personal computers...",
    "Exchange": "NASDAQ",
    "Currency": "USD",
    "Country": "USA",
    "Sector": "Technology",
    "Industry": "Consumer Electronics",
    "Address": "One Apple Park Way, Cupertino, CA, United States, 95014",
    "FiscalYearEnd": "September",
    "LatestQuarter": "2025-03-31",
    "MarketCapitalization": "3200000000000",
    "EBITDA": "130000000000",
    "PERatio": "32.5",
    "PEGRatio": "2.5",
    "BookValue": "4.5",
    "DividendPerShare": "0.96",
    "DividendYield": "0.005",
    "EPS": "5.9",
    "RevenuePerShareTTM": "25.2",
    "ProfitMargin": "0.24",
    "OperatingMarginTTM": "0.30",
    "ReturnOnAssetsTTM": "0.19",
    "ReturnOnEquityTTM": "0.42",
    "RevenueTTM": "400000000000",
    "GrossProfitTTM": "190000000000",
    "DilutedEPSTTM": "5.9",
    "QuarterlyEarningsGrowthYOY": "0.07",
    "QuarterlyRevenueGrowthYOY": "0.05",
    "AnalystTargetPrice": "210.0",
    "TrailingPE": "32.5",
    "ForwardPE": "28.1",
    "PriceToSalesRatioTTM": "8.0",
    "PriceToBookRatio": "42.8",
    "EVToRevenue": "8.0",
    "EVToEBITDA": "24.6",
    "Beta": "1.2",
    "52WeekHigh": "195.0",
    "52WeekLow": "150.0",
    "50DayMovingAverage": "185.0",
    "200DayMovingAverage": "175.0",
    "SharesOutstanding": "16000000000",
    "SharesFloat": "15900000000",
    "SharesShort": "100000000",
    "SharesShortPriorMonth": "95000000",
    "ShortRatio": "1.2",
    "ShortPercentOutstanding": "0.0063",
    "ShortPercentFloat": "0.0063",
    "PercentInsiders": "0.0001",
    "PercentInstitutions": "0.6",
    "ForwardAnnualDividendRate": "0.96",
    "ForwardAnnualDividendYield": "0.005",
    "PayoutRatio": "0.16",
    "DividendDate": "2025-05-15",
    "ExDividendDate": "2025-05-10",
    "LastSplitFactor": "4:1",
    "LastSplitDate": "2020-08-31"
}
```

### 저장 테이블
- `company_overview` 테이블

### 매핑 로직
```python
def sync_company_overview(symbol):
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params={
            "function": "OVERVIEW",
            "symbol": symbol,
            "apikey": "YOUR_API_KEY"
        }
    )
    data = response.json()
    
    # 응답이 비어있거나 에러가 있는 경우 처리
    if "Symbol" not in data:
        print(f"Error fetching data for {symbol}: {data}")
        return
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 데이터 변환 및 정리 (문자열 -> 숫자 변환 등)
    # None 처리 함수
    def parse_num(val, converter=float):
        try:
            return converter(val) if val else None
        except (ValueError, TypeError):
            return None
    
    cursor.execute("""
        INSERT INTO company_overview (
            symbol, description, currency, country, sector, industry, 
            address, fiscal_year_end, latest_quarter, market_capitalization, 
            ebitda, pe_ratio, peg_ratio, book_value, dividend_per_share, 
            dividend_yield, eps, revenue_per_share_ttm, profit_margin, 
            operating_margin_ttm, return_on_assets_ttm, return_on_equity_ttm, 
            revenue_ttm, gross_profit_ttm, diluted_eps_ttm, 
            quarterly_earnings_growth_yoy, quarterly_revenue_growth_yoy, 
            analyst_target_price, trailing_pe, forward_pe, 
            price_to_sales_ratio_ttm, price_to_book_ratio, ev_to_revenue, 
            ev_to_ebitda, beta, fifty_two_week_high, fifty_two_week_low, 
            fifty_day_moving_average, two_hundred_day_moving_average, 
            shares_outstanding, shares_float, shares_short, 
            shares_short_prior_month, short_ratio, short_percent_outstanding, 
            short_percent_float, percent_insiders, percent_institutions, 
            forward_annual_dividend_rate, forward_annual_dividend_yield, 
            payout_ratio, dividend_date, ex_dividend_date, 
            last_split_factor, last_split_date
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT (symbol) 
        DO UPDATE SET 
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
    """, (
        data["Symbol"],
        data.get("Description"),
        data.get("Currency"),
        data.get("Country"),
        data.get("Sector"),
        data.get("Industry"),
        data.get("Address"),
        data.get("FiscalYearEnd"),
        data.get("LatestQuarter"),
        parse_num(data.get("MarketCapitalization"), int),
        parse_num(data.get("EBITDA"), int),
        parse_num(data.get("PERatio")),
        parse_num(data.get("PEGRatio")),
        parse_num(data.get("BookValue")),
        parse_num(data.get("DividendPerShare")),
        parse_num(data.get("DividendYield")),
        parse_num(data.get("EPS")),
        parse_num(data.get("RevenuePerShareTTM")),
        parse_num(data.get("ProfitMargin")),
        parse_num(data.get("OperatingMarginTTM")),
        parse_num(data.get("ReturnOnAssetsTTM")),
        parse_num(data.get("ReturnOnEquityTTM")),
        parse_num(data.get("RevenueTTM"), int),
        parse_num(data.get("GrossProfitTTM"), int),
        parse_num(data.get("DilutedEPSTTM")),
        parse_num(data.get("QuarterlyEarningsGrowthYOY")),
        parse_num(data.get("QuarterlyRevenueGrowthYOY")),
        parse_num(data.get("AnalystTargetPrice")),
        parse_num(data.get("TrailingPE")),
        parse_num(data.get("ForwardPE")),
        parse_num(data.get("PriceToSalesRatioTTM")),
        parse_num(data.get("PriceToBookRatio")),
        parse_num(data.get("EVToRevenue")),
        parse_num(data.get("EVToEBITDA")),
        parse_num(data.get("Beta")),
        parse_num(data.get("52WeekHigh")),
        parse_num(data.get("52WeekLow")),
        parse_num(data.get("50DayMovingAverage")),
        parse_num(data.get("200DayMovingAverage")),
        parse_num(data.get("SharesOutstanding"), int),
        parse_num(data.get("SharesFloat"), int),
        parse_num(data.get("SharesShort"), int),
        parse_num(data.get("SharesShortPriorMonth"), int),
        parse_num(data.get("ShortRatio")),
        parse_num(data.get("ShortPercentOutstanding")),
        parse_num(data.get("ShortPercentFloat")),
        parse_num(data.get("PercentInsiders")),
        parse_num(data.get("PercentInstitutions")),
        parse_num(data.get("ForwardAnnualDividendRate")),
        parse_num(data.get("ForwardAnnualDividendYield")),
        parse_num(data.get("PayoutRatio")),
        data.get("DividendDate"),
        data.get("ExDividendDate"),
        data.get("LastSplitFactor"),
        data.get("LastSplitDate")
    ))
    
    conn.commit()
    cursor.close()
    conn.close()
```

## 4. 손익계산서 (INCOME_STATEMENT)

### API 정보
- **함수**: `INCOME_STATEMENT`
- **목적**: 기업의 연간/분기별 손익계산서 조회
- **동기화 주기**: 분기별 (기업 실적 발표 후)
- **파라미터**: `symbol`: 주식 심볼

### 요청
```
GET https://www.alphavantage.co/query?function=INCOME_STATEMENT&symbol=AAPL&apikey=YOUR_API_KEY
```

### 응답 예시
```json
{
    "symbol": "AAPL",
    "annualReports": [
        {
            "fiscalDateEnding": "2024-09-30",
            "reportedCurrency": "USD",
            "grossProfit": "170000000000",
            "totalRevenue": "400000000000",
            "costOfRevenue": "230000000000",
            "costofGoodsAndServicesSold": "230000000000",
            "operatingIncome": "120000000000",
            "sellingGeneralAndAdministrative": "25000000000",
            "researchAndDevelopment": "25000000000",
            "operatingExpenses": "50000000000",
            "investmentIncomeNet": "3000000000",
            "netInterestIncome": "-1000000000",
            "interestIncome": "2000000000",
            "interestExpense": "3000000000",
            "nonInterestIncome": "400000000000",
            "otherNonOperatingIncome": "1000000000",
            "depreciation": "12000000000",
            "depreciationAndAmortization": "12000000000",
            "incomeBeforeTax": "124000000000",
            "incomeTaxExpense": "20000000000",
            "interestAndDebtExpense": "3000000000",
            "netIncomeFromContinuingOperations": "104000000000",
            "comprehensiveIncomeNetOfTax": "104000000000",
            "ebit": "127000000000",
            "ebitda": "139000000000",
            "netIncome": "104000000000"
        }
        // 추가 연간 보고서...
    ],
    "quarterlyReports": [
        // 분기별 보고서 (구조는 연간과 동일)
    ]
}
```

### 저장 테이블
- `income_statements` 테이블

### 매핑 로직
```python
def sync_income_statements(symbol):
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params={
            "function": "INCOME_STATEMENT",
            "symbol": symbol,
            "apikey": "YOUR_API_KEY"
        }
    )
    data = response.json()
    
    # 응답 검증
    if "symbol" not in data:
        print(f"Error fetching income statement for {symbol}: {data}")
        return
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 연간 보고서 처리
    for report in data.get("annualReports", []):
        save_income_statement(cursor, symbol, report, is_quarterly=False)
    
    # 분기별 보고서 처리
    for report in data.get("quarterlyReports", []):
        save_income_statement(cursor, symbol, report, is_quarterly=True)
    
    conn.commit()
    cursor.close()
    conn.close()

def save_income_statement(cursor, symbol, report, is_quarterly):
    # None 처리 함수
    def parse_num(val, converter=int):
        try:
            return converter(val) if val else None
        except (ValueError, TypeError):
            return None
    
    cursor.execute("""
        INSERT INTO income_statements (
            symbol, fiscal_date_ending, reported_currency, gross_profit, 
            total_revenue, cost_of_revenue, cost_of_goods_and_services_sold, 
            operating_income, selling_general_and_administrative, 
            research_and_development, operating_expenses, investment_income_net, 
            net_interest_income, interest_income, interest_expense, 
            non_interest_income, other_non_operating_income, depreciation, 
            depreciation_and_amortization, income_before_tax, income_tax_expense, 
            interest_and_debt_expense, net_income_from_continuing_operations, 
            comprehensive_income_net_of_tax, ebit, ebitda, net_income, is_quarterly
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT (symbol, fiscal_date_ending, is_quarterly) 
        DO UPDATE SET 
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
    """, (
        symbol,
        report.get("fiscalDateEnding"),
        report.get("reportedCurrency"),
        parse_num(report.get("grossProfit")),
        parse_num(report.get("totalRevenue")),
        parse_num(report.get("costOfRevenue")),
        parse_num(report.get("costofGoodsAndServicesSold")),
        parse_num(report.get("operatingIncome")),
        parse_num(report.get("sellingGeneralAndAdministrative")),
        parse_num(report.get("researchAndDevelopment")),
        parse_num(report.get("operatingExpenses")),
        parse_num(report.get("investmentIncomeNet")),
        parse_num(report.get("netInterestIncome")),
        parse_num(report.get("interestIncome")),
        parse_num(report.get("interestExpense")),
        parse_num(report.get("nonInterestIncome")),
        parse_num(report.get("otherNonOperatingIncome")),
        parse_num(report.get("depreciation")),
        parse_num(report.get("depreciationAndAmortization")),
        parse_num(report.get("incomeBeforeTax")),
        parse_num(report.get("incomeTaxExpense")),
        parse_num(report.get("interestAndDebtExpense")),
        parse_num(report.get("netIncomeFromContinuingOperations")),
        parse_num(report.get("comprehensiveIncomeNetOfTax")),
        parse_num(report.get("ebit")),
        parse_num(report.get("ebitda")),
        parse_num(report.get("netIncome")),
        is_quarterly
    ))
```

## 5. 대차대조표 (BALANCE_SHEET)

### API 정보
- **함수**: `BALANCE_SHEET`
- **목적**: 기업의 연간/분기별 대차대조표 조회
- **동기화 주기**: 분기별 (기업 실적 발표 후)
- **파라미터**: `symbol`: 주식 심볼

### 요청
```
GET https://www.alphavantage.co/query?function=BALANCE_SHEET&symbol=AAPL&apikey=YOUR_API_KEY
```

### 응답 예시
```json
{
    "symbol": "AAPL",
    "annualReports": [
        {
            "fiscalDateEnding": "2024-09-30",
            "reportedCurrency": "USD",
            "totalAssets": "380000000000",
            "totalCurrentAssets": "150000000000",
            "cashAndCashEquivalentsAtCarryingValue": "50000000000",
            "cashAndShortTermInvestments": "70000000000",
            "inventory": "5000000000",
            "currentNetReceivables": "25000000000",
            "totalNonCurrentAssets": "230000000000",
            "propertyPlantEquipment": "40000000000",
            "accumulatedDepreciationAmortizationPPE": "60000000000",
            "intangibleAssets": "1000000000",
            "intangibleAssetsExcludingGoodwill": "500000000",
            "goodwill": "500000000",
            "investments": "100000000000",
            "longTermInvestments": "100000000000",
            "shortTermInvestments": "20000000000",
            "otherCurrentAssets": "10000000000",
            "otherNonCurrentAssets": "10000000000",
            "totalLiabilities": "270000000000",
            "totalCurrentLiabilities": "120000000000",
            "currentAccountsPayable": "60000000000",
            "deferredRevenue": "5000000000",
            "currentDebt": "10000000000",
            "shortTermDebt": "10000000000",
            "totalNonCurrentLiabilities": "150000000000",
            "capitalLeaseObligations": "5000000000",
            "longTermDebt": "120000000000",
            "currentLongTermDebt": "10000000000",
            "longTermDebtNoncurrent": "110000000000",
            "shortLongTermDebtTotal": "120000000000",
            "otherCurrentLiabilities": "25000000000",
            "otherNonCurrentLiabilities": "15000000000",
            "totalShareholderEquity": "110000000000",
            "treasuryStock": "-50000000000",
            "retainedEarnings": "100000000000",
            "commonStock": "60000000000",
            "commonStockSharesOutstanding": "16000000000"
        }
        // 추가 연간 보고서...
    ],
    "quarterlyReports": [
        // 분기별 보고서 (구조는 연간과 동일)
    ]
}
```

### 저장 테이블
- `balance_sheets` 테이블

### 매핑 로직
```python
def sync_balance_sheets(symbol):
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params={
            "function": "BALANCE_SHEET",
            "symbol": symbol,
            "apikey": "YOUR_API_KEY"
        }
    )
    data = response.json()
    
    # 응답 검증
    if "symbol" not in data:
        print(f"Error fetching balance sheet for {symbol}: {data}")
        return
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 연간 보고서 처리
    for report in data.get("annualReports", []):
        save_balance_sheet(cursor, symbol, report, is_quarterly=False)
    
    # 분기별 보고서 처리
    for report in data.get("quarterlyReports", []):
        save_balance_sheet(cursor, symbol, report, is_quarterly=True)
    
    conn.commit()
    cursor.close()
    conn.close()

def save_balance_sheet(cursor, symbol, report, is_quarterly):
    # None 처리 함수
    def parse_num(val, converter=int):
        try:
            return converter(val) if val else None
        except (ValueError, TypeError):
            return None
    
    cursor.execute("""
        INSERT INTO balance_sheets (
            symbol, fiscal_date_ending, reported_currency, total_assets, 
            total_current_assets, cash_and_cash_equivalents_at_carrying_value, 
            cash_and_short_term_investments, inventory, current_net_receivables, 
            total_non_current_assets, property_plant_equipment, 
            accumulated_depreciation_amortization_ppe, intangible_assets, 
            intangible_assets_excluding_goodwill, goodwill, investments, 
            long_term_investments, short_term_investments, other_current_assets, 
            other_non_current_assets, total_liabilities, total_current_liabilities, 
            current_accounts_payable, deferred_revenue, current_debt, short_term_debt, 
            total_non_current_liabilities, capital_lease_obligations, long_term_debt, 
            current_long_term_debt, long_term_debt_noncurrent, short_long_term_debt_total, 
            other_current_liabilities, other_non_current_liabilities, 
            total_shareholder_equity, treasury_stock, retained_earnings, 
            common_stock, common_stock_shares_outstanding, is_quarterly
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT (symbol, fiscal_date_ending, is_quarterly) 
        DO UPDATE SET 
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
    """, (
        symbol,
        report.get("fiscalDateEnding"),
        report.get("reportedCurrency"),
        parse_num(report.get("totalAssets")),
        parse_num(report.get("totalCurrentAssets")),
        parse_num(report.get("cashAndCashEquivalentsAtCarryingValue")),
        parse_num(report.get("cashAndShortTermInvestments")),
        parse_num(report.get("inventory")),
        parse_num(report.get("currentNetReceivables")),
        parse_num(report.get("totalNonCurrentAssets")),
        parse_num(report.get("propertyPlantEquipment")),
        parse_num(report.get("accumulatedDepreciationAmortizationPPE")),
        parse_num(report.get("intangibleAssets")),
        parse_num(report.get("intangibleAssetsExcludingGoodwill")),
        parse_num(report.get("goodwill")),
        parse_num(report.get("investments")),
        parse_num(report.get("longTermInvestments")),
        parse_num(report.get("shortTermInvestments")),
        parse_num(report.get("otherCurrentAssets")),
        parse_num(report.get("otherNonCurrentAssets")),
        parse_num(report.get("totalLiabilities")),
        parse_num(report.get("totalCurrentLiabilities")),
        parse_num(report.get("currentAccountsPayable")),
        parse_num(report.get("deferredRevenue")),
        parse_num(report.get("currentDebt")),
        parse_num(report.get("shortTermDebt")),
        parse_num(report.get("totalNonCurrentLiabilities")),
        parse_num(report.get("capitalLeaseObligations")),
        parse_num(report.get("longTermDebt")),
        parse_num(report.get("currentLongTermDebt")),
        parse_num(report.get("longTermDebtNoncurrent")),
        parse_num(report.get("shortLongTermDebtTotal")),
        parse_num(report.get("otherCurrentLiabilities")),
        parse_num(report.get("otherNonCurrentLiabilities")),
        parse_num(report.get("totalShareholderEquity")),
        parse_num(report.get("treasuryStock")),
        parse_num(report.get("retainedEarnings")),
        parse_num(report.get("commonStock")),
        parse_num(report.get("commonStockSharesOutstanding")),
        is_quarterly
    ))
```

## 6. 현금흐름표 (CASH_FLOW)

### API 정보
- **함수**: `CASH_FLOW`
- **목적**: 기업의 연간/분기별 현금흐름표 조회
- **동기화 주기**: 분기별 (기업 실적 발표 후)
- **파라미터**: `symbol`: 주식 심볼

### 요청
```
GET https://www.alphavantage.co/query?function=CASH_FLOW&symbol=AAPL&apikey=YOUR_API_KEY
```

### 응답 예시
```json
{
    "symbol": "AAPL",
    "annualReports": [
        {
            "fiscalDateEnding": "2024-09-30",
            "reportedCurrency": "USD",
            "operatingCashflow": "120000000000",
            "paymentsForOperatingActivities": "80000000000",
            "proceedsFromOperatingActivities": "200000000000",
            "changeInOperatingLiabilities": "10000000000",
            "changeInOperatingAssets": "-5000000000",
            "depreciationDepletionAndAmortization": "12000000000",
            "capitalExpenditures": "-15000000000",
            "changeInReceivables": "-3000000000",
            "changeInInventory": "-1000000000",
            "profitLoss": "104000000000",
            "cashflowFromInvestment": "-20000000000",
            "cashflowFromFinancing": "-80000000000",
            "proceedsFromRepaymentsOfShortTermDebt": "5000000000",
            "paymentsForRepurchaseOfCommonStock": "-75000000000",
            "paymentsForRepurchaseOfEquity": "-75000000000",
            "paymentsForRepurchaseOfPreferredStock": "0",
            "dividendPayout": "-15000000000",
            "dividendPayoutCommonStock": "-15000000000",
            "dividendPayoutPreferredStock": "0",
            "proceedsFromIssuanceOfCommonStock": "5000000000",
            "proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet": "10000000000",
            "proceedsFromIssuanceOfPreferredStock": "0",
            "proceedsFromRepurchaseOfEquity": "0",
            "proceedsFromSaleOfTreasuryStock": "0",
            "changeInCashAndCashEquivalents": "20000000000",
            "changeInExchangeRate": "1000000000",
            "netIncome": "104000000000"
        }
        // 추가 연간 보고서...
    ],
    "quarterlyReports": [
        // 분기별 보고서 (구조는 연간과 동일)
    ]
}
```

### 저장 테이블
- `cash_flows` 테이블

### 매핑 로직
```python
def sync_cash_flows(symbol):
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params={
            "function": "CASH_FLOW",
            "symbol": symbol,
            "apikey": "YOUR_API_KEY"
        }
    )
    data = response.json()
    
    # 응답 검증
    if "symbol" not in data:
        print(f"Error fetching cash flow for {symbol}: {data}")
        return
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 연간 보고서 처리
    for report in data.get("annualReports", []):
        save_cash_flow(cursor, symbol, report, is_quarterly=False)
    
    # 분기별 보고서 처리
    for report in data.get("quarterlyReports", []):
        save_cash_flow(cursor, symbol, report, is_quarterly=True)
    
    conn.commit()
    cursor.close()
    conn.close()

def save_cash_flow(cursor, symbol, report, is_quarterly):
    # None 처리 함수
    def parse_num(val, converter=int):
        try:
            return converter(val) if val else None
        except (ValueError, TypeError):
            return None
    
    cursor.execute("""
        INSERT INTO cash_flows (
            symbol, fiscal_date_ending, reported_currency, operating_cashflow, 
            payments_for_operating_activities, proceeds_from_operating_activities, 
            change_in_operating_liabilities, change_in_operating_assets, 
            depreciation_depletion_and_amortization, capital_expenditures, 
            change_in_receivables, change_in_inventory, profit_loss, 
            cashflow_from_investment, cashflow_from_financing, 
            proceeds_from_repayments_of_short_term_debt, 
            payments_for_repurchase_of_common_stock, 
            payments_for_repurchase_of_equity, 
            payments_for_repurchase_of_preferred_stock, 
            dividend_payout, dividend_payout_common_stock, 
            dividend_payout_preferred_stock, 
            proceeds_from_issuance_of_common_stock, 
            proceeds_from_issuance_of_long_term_debt_and_capital_securities, 
            proceeds_from_issuance_of_preferred_stock, 
            proceeds_from_repurchase_of_equity, 
            proceeds_from_sale_of_treasury_stock, 
            change_in_cash_and_cash_equivalents, 
            change_in_exchange_rate, net_income, is_quarterly
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT (symbol, fiscal_date_ending, is_quarterly) 
        DO UPDATE SET 
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
    """, (
        symbol,
        report.get("fiscalDateEnding"),
        report.get("reportedCurrency"),
        parse_num(report.get("operatingCashflow")),
        parse_num(report.get("paymentsForOperatingActivities")),
        parse_num(report.get("proceedsFromOperatingActivities")),
        parse_num(report.get("changeInOperatingLiabilities")),
        parse_num(report.get("changeInOperatingAssets")),
        parse_num(report.get("depreciationDepletionAndAmortization")),
        parse_num(report.get("capitalExpenditures")),
        parse_num(report.get("changeInReceivables")),
        parse_num(report.get("changeInInventory")),
        parse_num(report.get("profitLoss")),
        parse_num(report.get("cashflowFromInvestment")),
        parse_num(report.get("cashflowFromFinancing")),
        parse_num(report.get("proceedsFromRepaymentsOfShortTermDebt")),
        parse_num(report.get("paymentsForRepurchaseOfCommonStock")),
        parse_num(report.get("paymentsForRepurchaseOfEquity")),
        parse_num(report.get("paymentsForRepurchaseOfPreferredStock")),
        parse_num(report.get("dividendPayout")),
        parse_num(report.get("dividendPayoutCommonStock")),
        parse_num(report.get("dividendPayoutPreferredStock")),
        parse_num(report.get("proceedsFromIssuanceOfCommonStock")),
        parse_num(report.get("proceedsFromIssuanceOfLongTermDebtAndCapitalSecuritiesNet")),
        parse_num(report.get("proceedsFromIssuanceOfPreferredStock")),
        parse_num(report.get("proceedsFromRepurchaseOfEquity")),
        parse_num(report.get("proceedsFromSaleOfTreasuryStock")),
        parse_num(report.get("changeInCashAndCashEquivalents")),
        parse_num(report.get("changeInExchangeRate")),
        parse_num(report.get("netIncome")),
        is_quarterly
    ))
```

## 7. 뉴스 및 감성 분석 (NEWS_SENTIMENT)

### API 정보
- **함수**: `NEWS_SENTIMENT`
- **목적**: 주식 종목 관련 뉴스 및 감성 분석 조회
- **동기화 주기**: 일 1회 (또는 실시간)
- **파라미터**: 
  - `tickers`: 주식 심볼(들) (쉼표로 구분)
  - `topics`: 뉴스 주제 (선택적)
  - `time_from`: 시작 시간 (선택적)
  - `limit`: 최대 뉴스 수 (선택적)

### 요청
```
GET https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=AAPL&apikey=YOUR_API_KEY
```

### 응답 예시
```json
{
    "feed": [
        {
            "title": "Apple announces new product lineup",
            "url": "https://example.com/apple-news",
            "time_published": "20250710T132000",
            "authors": ["John Smith", "Jane Doe"],
            "summary": "Apple Inc announced a new line of products today...",
            "banner_image": "https://example.com/image.jpg",
            "source": "Example News",
            "category_within_source": "Technology",
            "source_domain": "example.com",
            "topics": [
                {"topic": "Technology", "relevance_score": "0.9"},
                {"topic": "Finance", "relevance_score": "0.3"}
            ],
            "overall_sentiment_score": 0.2,
            "overall_sentiment_label": "Somewhat-Bullish",
            "ticker_sentiment": [
                {
                    "ticker": "AAPL",
                    "relevance_score": "0.9",
                    "ticker_sentiment_score": "0.3",
                    "ticker_sentiment_label": "Somewhat-Bullish"
                },
                {
                    "ticker": "MSFT",
                    "relevance_score": "0.2",
                    "ticker_sentiment_score": "0.1",
                    "ticker_sentiment_label": "Neutral"
                }
            ]
        }
        // 추가 뉴스 항목...
    ],
    "items": "50"
}
```

### 저장 테이블
- `news_articles` 테이블
- `news_stocks` 테이블 (다대다 관계)

### 매핑 로직
```python
def sync_news_sentiment(symbols=None, topics=None, limit=50):
    # 파라미터 구성
    params = {
        "function": "NEWS_SENTIMENT",
        "apikey": "YOUR_API_KEY",
        "limit": limit
    }
    
    if symbols:
        if isinstance(symbols, list):
            params["tickers"] = ",".join(symbols)
        else:
            params["tickers"] = symbols
    
    if topics:
        if isinstance(topics, list):
            params["topics"] = ",".join(topics)
        else:
            params["topics"] = topics
    
    # API 호출
    response = requests.get(
        "https://www.alphavantage.co/query",
        params=params
    )
    data = response.json()
    
    # 응답 검증
    if "feed" not in data:
        print(f"Error fetching news: {data}")
        return
    
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 뉴스 처리
    for news in data.get("feed", []):
        # 뉴스 기본 정보 저장
        news_id = save_news_article(cursor, news)
        
        # 뉴스와 관련된 주식 정보 저장
        if news_id and "ticker_sentiment" in news:
            for ticker_data in news["ticker_sentiment"]:
                save_news_stock_relation(cursor, news_id, ticker_data)
    
    conn.commit()
    cursor.close()
    conn.close()

def save_news_article(cursor, news):
    # 시간 포맷 변환
    time_published = None
    if "time_published" in news:
        try:
            # 포맷 예: "20250710T132000"
            time_str = news["time_published"]
            time_published = datetime.strptime(time_str, "%Y%m%dT%H%M%S")
        except ValueError:
            try:
                # 다른 가능한 포맷 처리
                time_published = parser.parse(news["time_published"])
            except:
                time_published = datetime.now()
    
    # 저자 리스트 처리
    authors = news.get("authors", [])
    if isinstance(authors, str):
        authors = [authors]
    
    # None 처리 함수
    def parse_num(val, converter=float):
        try:
            return converter(val) if val else None
        except (ValueError, TypeError):
            return None
    
    try:
        cursor.execute("""
            INSERT INTO news_articles (
                title, url, time_published, authors, summary, source, 
                category_within_source, source_domain, 
                overall_sentiment_score, overall_sentiment_label
            )
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (url) 
            DO UPDATE SET 
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
            RETURNING id
        """, (
            news.get("title"),
            news.get("url"),
            time_published,
            authors,
            news.get("summary"),
            news.get("source"),
            news.get("category_within_source"),
            news.get("source_domain"),
            parse_num(news.get("overall_sentiment_score")),
            news.get("overall_sentiment_label")
        ))
        
        # 삽입된 ID 반환
        result = cursor.fetchone()
        return result[0] if result else None
    
    except Exception as e:
        print(f"Error saving news article: {e}")
        return None

def save_news_stock_relation(cursor, news_id, ticker_data):
    # None 처리 함수
    def parse_num(val, converter=float):
        try:
            return converter(val) if val else None
        except (ValueError, TypeError):
            return None
    
    try:
        cursor.execute("""
            INSERT INTO news_stocks (
                news_id, symbol, relevance_score, sentiment_score, sentiment_label
            )
            VALUES (%s, %s, %s, %s, %s)
            ON CONFLICT (news_id, symbol) 
            DO UPDATE SET 
                relevance_score = EXCLUDED.relevance_score,
                sentiment_score = EXCLUDED.sentiment_score,
                sentiment_label = EXCLUDED.sentiment_label
        """, (
            news_id,
            ticker_data.get("ticker"),
            parse_num(ticker_data.get("relevance_score")),
            parse_num(ticker_data.get("ticker_sentiment_score")),
            ticker_data.get("ticker_sentiment_label")
        ))
    
    except Exception as e:
        print(f"Error saving news-stock relation: {e}")
```

## 9. 뉴스 기반 주식 추천 생성

### 목적
- 뉴스 감성 분석 결과와 기술적/기본적 분석을 결합하여 주식 매수/매도 추천 생성

### 처리 로직
```python
def generate_stock_recommendations(symbol):
    # DB 연결
    conn = psycopg2.connect("dbname=your_db user=your_user password=your_pwd")
    cursor = conn.cursor()
    
    # 1. 뉴스 감성 분석 데이터 가져오기 (최근 7일)
    cursor.execute("""
        SELECT 
            ns.symbol,
            AVG(ns.sentiment_score) as avg_sentiment,
            COUNT(ns.news_id) as news_count
        FROM 
            news_stocks ns
        JOIN 
            news_articles na ON ns.news_id = na.id
        WHERE 
            ns.symbol = %s
            AND na.time_published > NOW() - INTERVAL '7 days'
        GROUP BY 
            ns.symbol
    """, (symbol,))
    
    news_sentiment = cursor.fetchone()
    
    # 2. 기술적 분석 데이터 가져오기 (50일 이동평균선 대비 현재가)
    cursor.execute("""
        SELECT 
            dp.close,
            co.fifty_day_moving_average
        FROM 
            daily_prices dp
        JOIN 
            company_overview co ON dp.symbol = co.symbol
        WHERE 
            dp.symbol = %s
        ORDER BY 
            dp.date DESC
        LIMIT 1
    """, (symbol,))
    
    technical_data = cursor.fetchone()
    
    # 3. 기본적 분석 데이터 가져오기 (PER, EPS 등)
    cursor.execute("""
        SELECT 
            pe_ratio,
            eps,
            peg_ratio,
            profit_margin,
            return_on_equity_ttm
        FROM 
            company_overview
        WHERE 
            symbol = %s
    """, (symbol,))
    
    fundamental_data = cursor.fetchone()
    
    # 종합 점수 계산 (10점 만점)
    score = 5  # 기본값 (중립)
    reasons = []
    
    # 뉴스 감성 점수 반영 (최대 ±2점)
    if news_sentiment and news_sentiment[0] is not None:
        avg_sentiment = news_sentiment[1]
        news_count = news_sentiment[2]
        
        if news_count >= 3:  # 최소 3개 이상의 뉴스가 있을 때만 반영
            if avg_sentiment > 0.5:
                score += 2
                reasons.append(f"최근 뉴스 감성이 매우 긍정적 (평균: {avg_sentiment:.2f}, 뉴스 수: {news_count}개)")
            elif avg_sentiment > 0.2:
                score += 1
                reasons.append(f"최근 뉴스 감성이 긍정적 (평균: {avg_sentiment:.2f}, 뉴스 수: {news_count}개)")
            elif avg_sentiment < -0.5:
                score -= 2
                reasons.append(f"최근 뉴스 감성이 매우 부정적 (평균: {avg_sentiment:.2f}, 뉴스 수: {news_count}개)")
            elif avg_sentiment < -0.2:
                score -= 1
                reasons.append(f"최근 뉴스 감성이 부정적 (평균: {avg_sentiment:.2f}, 뉴스 수: {news_count}개)")
    
    # 기술적 분석 점수 반영 (최대 ±2점)
    if technical_data and technical_data[0] is not None and technical_data[1] is not None:
        current_price = technical_data[0]
        ma_50 = technical_data[1]
        
        price_vs_ma = (current_price / ma_50 - 1) * 100  # 이동평균선 대비 백분율
        
        if price_vs_ma > 10:
            score += 2
            reasons.append(f"현재가가 50일 이동평균선보다 크게 높음 (+{price_vs_ma:.2f}%)")
        elif price_vs_ma > 5:
            score += 1
            reasons.append(f"현재가가 50일 이동평균선보다 높음 (+{price_vs_ma:.2f}%)")
        elif price_vs_ma < -10:
            score -= 2
            reasons.append(f"현재가가 50일 이동평균선보다 크게 낮음 ({price_vs_ma:.2f}%)")
        elif price_vs_ma < -5:
            score -= 1
            reasons.append(f"현재가가 50일 이동평균선보다 낮음 ({price_vs_ma:.2f}%)")
    
    # 기본적 분석 점수 반영 (최대 ±2점)
    if fundamental_data and fundamental_data[0] is not None:
        pe_ratio = fundamental_data[0]
        eps = fundamental_data[1]
        peg_ratio = fundamental_data[2]
        profit_margin = fundamental_data[3]
        roe = fundamental_data[4]
        
        fundamental_score = 0
        
        # PER 평가 (업종 평균 대비 할인/할증)
        # (업종 평균 PER는 별도 테이블에서 가져와야 함)
        industry_avg_pe = 20  # 예시 값
        
        if pe_ratio < industry_avg_pe * 0.7:
            fundamental_score += 1
            reasons.append(f"PER({pe_ratio:.2f})이 업종 평균({industry_avg_pe})보다 크게 낮음 (저평가 가능성)")
        elif pe_ratio > industry_avg_pe * 1.3:
            fundamental_score -= 1
            reasons.append(f"PER({pe_ratio:.2f})이 업종 평균({industry_avg_pe})보다 크게 높음 (고평가 가능성)")
        
        # 수익성 평가
        if profit_margin > 0.2:
            fundamental_score += 0.5
            reasons.append(f"높은 수익률 (이익률: {profit_margin*100:.2f}%)")
        elif profit_margin < 0:
            fundamental_score -= 1
            reasons.append(f"적자 기업 (이익률: {profit_margin*100:.2f}%)")
        
        # ROE 평가
        if roe > 0.2:
            fundamental_score += 0.5
            reasons.append(f"높은 자기자본이익률 (ROE: {roe*100:.2f}%)")
        elif roe < 0:
            fundamental_score -= 0.5
            reasons.append(f"부정적인 자기자본이익률 (ROE: {roe*100:.2f}%)")
        
        # 종합 기본적 분석 점수 반영 (최대 ±2점)
        score += max(-2, min(2, fundamental_score))
    
    # 최종 점수 범위 조정 (1~10)
    score = max(1, min(10, score))
    
    # 추천 라벨 생성
    if score >= 9:
        label = "강력 매수"
    elif score >= 7:
        label = "매수"
    elif score >= 6:
        label = "약매수"
    elif score >= 5:
        label = "중립"
    elif score >= 4:
        label = "약매도"
    elif score >= 2:
        label = "매도"
    else:
        label = "강력 매도"
    
    # 추천 이유 종합
    reasoning = " | ".join(reasons)
    
    # DB에 추천 정보 저장
    cursor.execute("""
        INSERT INTO stock_recommendations (
            symbol, recommendation_score, recommendation_label, reasoning, 
            news_based, technical_based, fundamental_based
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT (symbol) 
        DO UPDATE SET 
            recommendation_score = EXCLUDED.recommendation_score,
            recommendation_label = EXCLUDED.recommendation_label,
            reasoning = EXCLUDED.reasoning,
            news_based = EXCLUDED.news_based,
            technical_based = EXCLUDED.technical_based,
            fundamental_based = EXCLUDED.fundamental_based,
            last_updated = CURRENT_TIMESTAMP
    """, (
        symbol, 
        score,
        label,
        reasoning,
        True,   # 뉴스 기반
        True,   # 기술적 분석 기반
        True    # 기본적 분석 기반
    ))
    
    conn.commit()
    cursor.close()
    conn.close()
```

## 데이터 동기화 스케줄링 가이드

아래는 각 API 호출의 권장 동기화 주기입니다:

| API 엔드포인트 | 동기화 주기 | 우선순위 | 설명 |
|--------------|------------|---------|------|
| LISTING_STATUS | 주 1회 | 낮음 | 모든 주식 종목 목록 업데이트 |
| TIME_SERIES_DAILY | 일 1회 | 높음 | 캔들차트용 일별 주가 데이터 |
| OVERVIEW | 주 1회 | 중간 | 기업 기본 정보 및 재무 지표 |
| INCOME_STATEMENT | 분기 1회 | 중간 | 분기/연간 손익계산서 |
| BALANCE_SHEET | 분기 1회 | 중간 | 분기/연간 대차대조표 |
| CASH_FLOW | 분기 1회 | 중간 | 분기/연간 현금흐름표 |
| NEWS_SENTIMENT | 일 1회 | 높음 | 뉴스 및 감성 분석 |
| 주식 추천 생성 | 일 1회 | 높음 | 뉴스, 기술적, 기본적 분석 기반 추천 |

## 데이터 초기화 스크립트

전체 데이터베이스를 처음 구축할 때 사용할 수 있는 초기화 스크립트입니다:

```python
def initialize_database():
    # 1. 모든 주식 종목 목록 가져오기
    sync_stock_listings()
    
    # 2. 주요 주식 심볼 목록 (예: S&P 500 구성종목)
    symbols = get_sp500_symbols()  # 별도 함수로 구현 필요
    
    # 3. 각 종목별 상세 데이터 동기화
    for symbol in symbols:
        # 일별 주가 데이터
        sync_daily_prices(symbol)
        
        # 기업 정보
        sync_company_overview(symbol)
        
        # 재무제표
        sync_income_statements(symbol)
        sync_balance_sheets(symbol)
        sync_cash_flows(symbol)
    
    # 4. 뉴스 데이터 동기화
    sync_news_sentiment(symbols[:10])  # API 제한으로 일부만 처리
    
    # 5. 시장 지수 동기화
    sync_market_indices()
    
    # 6. 상위 급등/급락 종목 동기화
    sync_top_gainers_losers()
    
    # 7. 주식 추천 정보 생성
    for symbol in symbols:
        generate_stock_recommendations(symbol)

# 메인 실행
if __name__ == "__main__":
    initialize_database()
```