# Gon Stock Dashboard - API Specification

## 📋 API 개요

본 문서는 Gon Stock Dashboard 프로젝트의 모든 GET API 엔드포인트에 대한 상세 명세서입니다.

### 기본 정보
- **Base URL**: `https://api.hwangonjang.com`
- **Content-Type**: `application/json`
- **Authentication**: None (현재 버전)

### 공통 응답 형식
모든 API는 다음과 같은 공통 응답 형식을 사용합니다:

```json
{
  "traceId": "string",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    // 실제 응답 데이터
  }
}
```

### 페이지네이션 응답 형식
페이지네이션이 적용된 API의 응답 형식:

```json
{
  "traceId": "string",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "content": [
      // 실제 데이터 배열
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false
      },
      "pageNumber": 0,
      "pageSize": 20,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 100,
    "totalPages": 5,
    "last": false,
    "first": true,
    "numberOfElements": 20,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  }
}
```

### 오류 응답 형식
오류 발생시 다음과 같은 형식으로 응답합니다:

```json
{
  "traceId": "string",
  "status": 404,
  "code": "Stock-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "message": "Could not find stock."
  }
}
```

### 공통 쿼리 파라미터

#### 페이지네이션 (Pageable)
모든 리스트 API에서 사용 가능한 페이지네이션 파라미터:

- `page` (query, integer, optional): 페이지 번호 (0부터 시작, 기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: API별로 상이)
- `sort` (query, string, optional): 정렬 조건 (예: "date,desc", "symbol,asc")

**사용 예시:**
```
GET /v1/daily-prices/AAPL?page=0&size=50&sort=date,desc
```

#### 날짜 형식
- **날짜**: `yyyy-MM-dd` (예: 2024-01-01)
- **날짜+시간**: `yyyy-MM-dd HH:mm:ss` (예: 2024-01-01 14:30:00)

### 지원 종목 목록
다음 20개 나스닥 기술 주식을 지원합니다:
`["AAPL", "MSFT", "GOOGL", "AMZN", "META", "NVDA", "TSLA", "AVGO", "CRM", "ORCL", "NFLX", "ADBE", "AMD", "INTC", "PYPL", "CSCO", "QCOM", "TXN", "AMAT", "PLTR"]`

---

## 🏢 주식 기본 정보 API

### 1. 주식 기본 정보 조회
**메인 페이지**: 종목 리스트 표시용

```http
GET /v1/stocks/info/{symbol}
```

**Parameters:**
- `symbol` (path): 주식 심볼 (예: AAPL, MSFT)

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "symbol": "AAPL",
    "name": "Apple Inc.",
    "exchange": "NASDAQ",
    "assetType": "Common Stock",
    "ipoDate": "1980-12-12",
    "delistingDate": null,
    "status": "Active",
    "lastUpdatedAt": "2024-01-01T00:00:00"
  }
}
```

**사용 목적**: 메인 페이지 종목 리스트에서 회사명, 거래소 정보 표시

### 2. 회사 개요 정보 조회
**상세 페이지**: 기업 정보 및 주요 지표 표시용

```http
GET /v1/company-overview/{symbol}
```

**Parameters:**
- `symbol` (path): 주식 심볼

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "symbol": "AAPL",
    "description": "Apple Inc. designs, manufactures, and markets smartphones...",
    "currency": "USD",
    "country": "USA",
    "sector": "Technology",
    "industry": "Consumer Electronics",
    "address": "One Apple Park Way, Cupertino, CA 95014",
    "fiscalYearEnd": "September",
    "latestQuarter": "2023-12-31",
    "marketCapitalization": 3000000000000,
    "ebitda": 120000000000,
    "peRatio": 28.5,
    "pegRatio": 2.1,
    "bookValue": 4.25,
    "dividendPerShare": 0.96,
    "dividendYield": 0.0045,
    "eps": 6.13,
    "revenuePerShareTtm": 23.45,
    "profitMargin": 0.25,
    "operatingMarginTtm": 0.30,
    "returnOnAssetsTtm": 0.20,
    "returnOnEquityTtm": 0.175,
    "revenueTtm": 400000000000,
    "grossProfitTtm": 170000000000,
    "dilutedEpsTtm": 6.13,
    "quarterlyEarningsGrowthYoy": 0.08,
    "quarterlyRevenueGrowthYoy": 0.05,
    "analystTargetPrice": 200.0,
    "trailingPe": 28.5,
    "forwardPe": 26.0,
    "priceToSalesRatioTtm": 7.5,
    "priceToBookRatio": 45.0,
    "evToRevenue": 7.2,
    "evToEbitda": 24.0,
    "beta": 1.2,
    "fiftyTwoWeekHigh": 200.0,
    "fiftyTwoWeekLow": 150.0,
    "fiftyDayMovingAverage": 180.0,
    "twoHundredDayMovingAverage": 170.0,
    "sharesOutstanding": 15000000000,
    "sharesFloat": 15000000000,
    "sharesShort": 100000000,
    "sharesShortPriorMonth": 95000000,
    "shortRatio": 1.2,
    "shortPercentOutstanding": 0.67,
    "shortPercentFloat": 0.67,
    "percentInsiders": 0.1,
    "percentInstitutions": 60.0,
    "forwardAnnualDividendRate": 1.0,
    "forwardAnnualDividendYield": 0.005,
    "payoutRatio": 0.16,
    "dividendDate": "2024-02-15",
    "exDividendDate": "2024-02-10",
    "lastSplitFactor": "4:1",
    "lastSplitDate": "2020-08-31",
    "lastUpdated": "2024-01-01T00:00:00"
  }
}
```

**사용 목적**: 
- 상세 페이지 기업 정보 섹션
- 주요 재무 지표 표시 (P/E, EPS, 배당률, 시가총액 등)
- 52주 최고/최저가, 이동평균선 정보

---

## 💰 주식 금융 정보 API

### 3. 대차대조표 조회
**상세 페이지**: 재무제표 정보 표시용

```http
GET /v1/balance-sheets/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**Query Parameters (ModelAttribute):**
- `isQuarterly` (query, boolean, optional): 분기별 여부 (true: 분기, false: 연간)
- `startDate` (query, string, optional): 시작 날짜 (yyyy-MM-dd)
- `endDate` (query, string, optional): 종료 날짜 (yyyy-MM-dd)
- `year` (query, integer, optional): 특정 연도
- `quarter` (query, integer, optional): 특정 분기 (1-4)
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 50)
- `sort` (query, string, optional): 정렬 조건

**사용 예시:**
```
GET /v1/balance-sheets/AAPL?isQuarterly=true&year=2024&quarter=1&page=0&size=20
GET /v1/balance-sheets/AAPL?startDate=2023-01-01&endDate=2023-12-31&sort=fiscalDateEnding,desc
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "symbol": "AAPL",
    "fiscalDateEnding": "2023-12-31",
    "reportedCurrency": "USD",
    "totalAssets": 350000000000,
    "totalCurrentAssets": 150000000000,
    "cashAndShortTermInvestments": 80000000000,
    "inventory": 8000000000,
    "currentNetReceivables": 25000000000,
    "totalNonCurrentAssets": 200000000000,
    "propertyPlantEquipment": 45000000000,
    "goodwill": 5000000000,
    "intangibleAssets": 15000000000,
    "totalLiabilities": 200000000000,
    "totalCurrentLiabilities": 120000000000,
    "currentAccountsPayable": 50000000000,
    "shortTermDebt": 20000000000,
    "totalNonCurrentLiabilities": 80000000000,
    "longTermDebt": 60000000000,
    "totalShareholderEquity": 150000000000,
    "commonStock": 60000000000,
    "retainedEarnings": 90000000000,
    "lastUpdated": "2024-01-01T00:00:00"
  }
}
```

### 4. 손익계산서 조회

```http
GET /v1/income-statements/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**Query Parameters (ModelAttribute):**
- `isQuarterly` (query, boolean, optional): 분기별 여부 (true: 분기, false: 연간)
- `startDate` (query, string, optional): 시작 날짜 (yyyy-MM-dd)
- `endDate` (query, string, optional): 종료 날짜 (yyyy-MM-dd)
- `year` (query, integer, optional): 특정 연도
- `quarter` (query, integer, optional): 특정 분기 (1-4)
- `minRevenue` (query, integer, optional): 최소 매출 필터
- `minNetIncome` (query, integer, optional): 최소 순이익 필터
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 50)
- `sort` (query, string, optional): 정렬 조건

**사용 예시:**
```
GET /v1/income-statements/AAPL?isQuarterly=false&year=2024
GET /v1/income-statements/AAPL?startDate=2023-01-01&endDate=2023-12-31&minRevenue=100000000000
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "symbol": "AAPL",
    "fiscalDateEnding": "2023-12-31",
    "reportedCurrency": "USD",
    "totalRevenue": 400000000000,
    "costOfRevenue": 230000000000,
    "grossProfit": 170000000000,
    "operatingIncome": 120000000000,
    "netIncome": 100000000000,
    "ebitda": 130000000000,
    "eps": 6.13,
    "dilutedEps": 6.13,
    "operatingCashflow": 110000000000,
    "researchAndDevelopment": 25000000000,
    "sellingGeneralAdministrative": 25000000000,
    "interestExpense": 3000000000,
    "incomeTaxExpense": 17000000000,
    "lastUpdated": "2024-01-01T00:00:00"
  }
}
```

### 5. 현금흐름표 조회

```http
GET /v1/cash-flow/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**Query Parameters (ModelAttribute):**
- `isQuarterly` (query, boolean, optional): 분기별 여부 (true: 분기, false: 연간)
- `startDate` (query, string, optional): 시작 날짜 (yyyy-MM-dd)
- `endDate` (query, string, optional): 종료 날짜 (yyyy-MM-dd)
- `year` (query, integer, optional): 특정 연도
- `quarter` (query, integer, optional): 특정 분기 (1-4)
- `minNetIncome` (query, integer, optional): 최소 순이익 필터
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 50)
- `sort` (query, string, optional): 정렬 조건

**사용 예시:**
```
GET /v1/cash-flow/AAPL?isQuarterly=true&year=2024&quarter=2
GET /v1/cash-flow/AAPL?startDate=2023-01-01&endDate=2023-12-31&minNetIncome=50000000000
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "symbol": "AAPL",
    "fiscalDateEnding": "2023-12-31",
    "reportedCurrency": "USD",
    "operatingCashflow": 110000000000,
    "paymentsForOperatingActivities": -100000000000,
    "proceedsFromOperatingActivities": 210000000000,
    "capitalExpenditures": -12000000000,
    "investmentCashflow": -5000000000,
    "cashflowFromInvestment": -5000000000,
    "cashflowFromFinancing": -80000000000,
    "proceedsFromRepaymentsOfShortTermDebt": 0,
    "paymentsForRepurchaseOfCommonStock": -75000000000,
    "paymentsForRepurchaseOfEquity": -75000000000,
    "dividendPayout": -15000000000,
    "changeInCashAndCashEquivalents": 25000000000,
    "cashAndCashEquivalentsAtCarryingValue": 50000000000,
    "lastUpdated": "2024-01-01T00:00:00"
  }
}
```

**사용 목적**: 상세 페이지 재무제표 섹션에서 기업의 재무 건전성 분석

---

## 📰 주식 뉴스 정보 API

### 6. 전체 뉴스 조회
**메인 페이지**: 주요 뉴스 섹션용

```http
GET /v1/news
```

**Query Parameters (ModelAttribute):**
- `startDate` (query, string, optional): 시작 날짜+시간 (yyyy-MM-dd HH:mm:ss)
- `endDate` (query, string, optional): 종료 날짜+시간 (yyyy-MM-dd HH:mm:ss)
- `source` (query, string, optional): 뉴스 소스 필터
- `category` (query, string, optional): 뉴스 카테고리 필터
- `sourceDomain` (query, string, optional): 소스 도메인 필터
- `sentimentLabel` (query, string, optional): 감정 라벨 필터 (Positive, Negative, Neutral)
- `minSentimentScore` (query, number, optional): 최소 감정 점수
- `maxSentimentScore` (query, number, optional): 최대 감정 점수
- `keyword` (query, string, optional): 키워드 검색
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 50)
- `sort` (query, string, optional): 정렬 조건

**사용 예시:**
```
GET /v1/news?startDate=2024-01-01 00:00:00&endDate=2024-01-31 23:59:59&page=0&size=10
GET /v1/news?sentimentLabel=Positive&minSentimentScore=0.7&sort=timePublished,desc
GET /v1/news?keyword=Apple&source=Reuters&category=Technology
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": [
    {
      "id": 1,
      "title": "Apple Reports Record Q4 Revenue",
      "url": "https://example.com/news/1",
      "timePublished": "2024-01-01T08:00:00",
      "summary": "Apple Inc. reported record fourth-quarter revenue driven by strong iPhone sales...",
      "bannerImage": "https://example.com/images/apple-news.jpg",
      "source": "Reuters",
      "categoryWithinSource": "Technology",
      "sourceUrl": "https://reuters.com",
      "topics": ["Technology", "Earnings"],
      "overallSentimentScore": 0.7,
      "overallSentimentLabel": "Positive",
      "tickerSentiment": [
        {
          "ticker": "AAPL",
          "relevanceScore": 0.95,
          "tickerSentimentScore": 0.75,
          "tickerSentimentLabel": "Positive"
        }
      ]
    }
  ]
}
```

### 7. 종목별 뉴스 조회
**상세 페이지**: 종목별 뉴스 섹션용

```http
GET /v1/news/symbol/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**Query Parameters (ModelAttribute):**
- `startDate` (query, string, optional): 시작 날짜+시간 (yyyy-MM-dd HH:mm:ss)
- `endDate` (query, string, optional): 종료 날짜+시간 (yyyy-MM-dd HH:mm:ss)
- `source` (query, string, optional): 뉴스 소스 필터
- `sentimentLabel` (query, string, optional): 감정 라벨 필터 (Positive, Negative, Neutral)
- `minSentimentScore` (query, number, optional): 최소 감정 점수
- `maxSentimentScore` (query, number, optional): 최대 감정 점수
- `minRelevanceScore` (query, number, optional): 최소 관련도 점수
- `keyword` (query, string, optional): 키워드 검색
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 50)
- `sort` (query, string, optional): 정렬 조건

**사용 예시:**
```
GET /v1/news/symbol/AAPL?startDate=2024-01-01 00:00:00&endDate=2024-01-31 23:59:59
GET /v1/news/symbol/AAPL?sentimentLabel=Positive&minRelevanceScore=0.8&sort=relevanceScore,desc
GET /v1/news/symbol/AAPL?keyword=iPhone&source=Reuters&page=0&size=20
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": [
    {
      "id": 1,
      "newsArticle": {
        "id": 1,
        "title": "Apple Reports Record Q4 Revenue",
        "url": "https://example.com/news/1",
        "timePublished": "2024-01-01T08:00:00",
        "summary": "Apple Inc. reported record fourth-quarter revenue...",
        "bannerImage": "https://example.com/images/apple-news.jpg",
        "source": "Reuters"
      },
      "symbol": "AAPL",
      "relevanceScore": 0.95,
      "sentimentScore": 0.75,
      "sentimentLabel": "Positive"
    }
  ]
}
```

**사용 목적**: 
- 메인 페이지 주요 뉴스 표시 (전체 뉴스에서 최신 5-10개)
- 상세 페이지 종목별 뉴스 리스트
- 뉴스 감정 분석 점수를 통한 시장 분위기 파악

---

## 📊 주식 투자 정보 API

### 8. 주식 추천 정보 조회
**메인 페이지**: 주식 추천 섹션 및 종목 리스트 추천 점수 표시용
**상세 페이지**: 투자 정보 섹션용

```http
GET /v1/stocks/recommendations/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**사용 예시:**
```
GET /v1/stocks/recommendations/AAPL
GET /v1/stocks/recommendations/NVDA
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "id": 1,
    "symbol": "AAPL",
    "recommendationScore": 0.8750,
    "recommendationLabel": "Strong Buy",
    "summary": "Apple's strong fundamentals, innovative product pipeline, and solid financial performance make it an attractive investment opportunity. The company's expansion into services and strong ecosystem create sustainable competitive advantages.",
    "createdAt": "2024-01-01T00:00:00",
    "lastUpdatedAt": "2024-01-01T00:00:00"
  }
}
```

### 9. 전체 주식 추천 리스트 조회
**메인 페이지**: 추천 점수 기준 정렬용

```http
GET /v1/stocks/recommendations
```

**Query Parameters (Pageable):**
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 20)
- `sort` (query, string, optional): 정렬 조건 (기본값: recommendationScore,desc)

**사용 예시:**
```
GET /v1/stocks/recommendations?page=0&size=10&sort=recommendationScore,desc
GET /v1/stocks/recommendations?page=0&size=20&sort=lastUpdatedAt,desc
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": [
    {
      "id": 1,
      "symbol": "NVDA",
      "recommendationScore": 0.9250,
      "recommendationLabel": "Strong Buy",
      "summary": "NVIDIA's dominance in AI chips and data center growth...",
      "createdAt": "2024-01-01T00:00:00",
      "lastUpdatedAt": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "symbol": "AAPL",
      "recommendationScore": 0.8750,
      "recommendationLabel": "Strong Buy",
      "summary": "Apple's strong fundamentals and ecosystem...",
      "createdAt": "2024-01-01T00:00:00",
      "lastUpdatedAt": "2024-01-01T00:00:00"
    }
  ]
}
```

**추천 라벨 범주:**
- `Strong Buy` (8.000 - 1.000): 강력 매수
- `Buy` (6.500 - 7.999): 매수
- `Hold` (4.500 - 6.499): 보유
- `Sell` (3.000 - 4.499): 매도
- `Strong Sell` (0.000 - 2.999): 강력 매도

---

## 📈 주식 가격 정보 API

### 10. 일별 주가 데이터 조회
**상세 페이지**: 캔들스틱 차트 및 거래량 차트용

```http
GET /v1/daily-prices/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**Query Parameters (ModelAttribute):**
- `startDate` (query, string, optional): 시작 날짜 (yyyy-MM-dd)
- `endDate` (query, string, optional): 종료 날짜 (yyyy-MM-dd)
- `year` (query, integer, optional): 특정 연도
- `quarter` (query, integer, optional): 특정 분기 (1-4)
- `month` (query, integer, optional): 특정 월 (1-12)
- `minVolume` (query, integer, optional): 최소 거래량 필터
- `sortOrder` (query, string, optional): 정렬 방향 (asc, desc, 기본값: desc)
- `page` (query, integer, optional): 페이지 번호 (기본값: 0)
- `size` (query, integer, optional): 페이지 크기 (기본값: 100)
- `sort` (query, string, optional): 정렬 조건

**사용 예시:**
```
GET /v1/daily-prices/AAPL?startDate=2024-01-01&endDate=2024-01-31&sortOrder=desc
GET /v1/daily-prices/AAPL?year=2024&quarter=1&minVolume=10000000&page=0&size=50
GET /v1/daily-prices/AAPL?year=2024&month=3&sort=volume,desc
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": [
    {
      "id": 1,
      "symbol": "AAPL",
      "date": "2024-01-01",
      "open": 185.50,
      "high": 189.25,
      "low": 184.75,
      "close": 188.00,
      "volume": 45000000
    },
    {
      "id": 2,
      "symbol": "AAPL",
      "date": "2023-12-31",
      "open": 183.25,
      "high": 186.00,
      "low": 182.50,
      "close": 185.50,
      "volume": 38000000
    }
  ]
}
```

**사용 목적**: 
- 캔들스틱 차트 생성 (OHLC 데이터)
- 거래량 차트 생성
- 등락률 계산 (전일 대비)

---

## 🔴 실시간 주식 데이터 스트리밍 API

### 11. 실시간 주식 데이터 스트리밍
**메인 페이지**: 실시간 가격 업데이트용
**상세 페이지**: 실시간 차트 업데이트용

```http
GET /v1/stocks/stream/{symbol}
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**Content-Type:** `text/event-stream`

**사용 예시:**
```
GET /v1/stocks/stream/AAPL
GET /v1/stocks/stream/NVDA
```

**Response (Server-Sent Events):**
```
data: {"uuid":"123e4567-e89b-12d3-a456-426614174000","symbol":"AAPL","tradeConditions":"Regular","price":188.25,"volume":1000,"tradeTimestamp":"2024-01-01T14:30:00","ingestTimestamp":"2024-01-01T14:30:01"}

data: {"uuid":"123e4567-e89b-12d3-a456-426614174001","symbol":"AAPL","tradeConditions":"Regular","price":188.50,"volume":1500,"tradeTimestamp":"2024-01-01T14:30:05","ingestTimestamp":"2024-01-01T14:30:06"}
```

### 12. 최신 거래 데이터 조회
**상세 페이지**: 현재 시세 표시용

```http
GET /v1/stocks/stream/{symbol}/latest
```

**Parameters:**
- `symbol` (path, required): 주식 심볼

**사용 예시:**
```
GET /v1/stocks/stream/AAPL/latest
GET /v1/stocks/stream/NVDA/latest
```

**Response:**
```json
{
  "traceId": "abc123",
  "status": 200,
  "code": "Common-001",
  "timestamp": "2024-01-01T00:00:00",
  "body": {
    "uuid": "123e4567-e89b-12d3-a456-426614174000",
    "symbol": "AAPL",
    "tradeConditions": "Regular",
    "price": 188.25,
    "volume": 1000,
    "tradeTimestamp": "2024-01-01T14:30:00",
    "ingestTimestamp": "2024-01-01T14:30:01"
  }
}
```

**사용 목적**: 
- 실시간 가격 차트 업데이트
- 메인 페이지 종목 리스트 실시간 가격 표시
- 현재 시세 및 거래량 정보 제공

---

## 🚫 에러 코드

### 공통 에러 코드
- `Common-001`: 성공 응답

### 주식 관련 에러 코드
- `Stock-001`: 주식 정보를 찾을 수 없음
- `Stock-002`: 대차대조표 정보를 찾을 수 없음
- `Stock-003`: 현금흐름 정보를 찾을 수 없음
- `Stock-004`: 회사 개요 정보를 찾을 수 없음
- `Stock-005`: 일별 주가 정보를 찾을 수 없음
- `Stock-006`: 손익계산서 정보를 찾을 수 없음
- `Stock-007`: 뉴스 정보를 찾을 수 없음
- `Stock-008`: 주식 추천 정보를 찾을 수 없음

---

## 📊 추가 유용한 API 엔드포인트

### 13. 최신 대차대조표 조회
```http
GET /v1/balance-sheets/{symbol}/latest?isQuarterly=false
```

### 14. 최신 손익계산서 조회
```http
GET /v1/income-statements/{symbol}/latest?isQuarterly=false
```

### 15. 최신 현금흐름표 조회
```http
GET /v1/cash-flow/{symbol}/latest?isQuarterly=false
```

### 16. 최신 일별 주가 조회
```http
GET /v1/daily-prices/{symbol}/latest
```

### 17. 연간 재무제표 조회
```http
GET /v1/balance-sheets/{symbol}/annual
GET /v1/income-statements/{symbol}/annual
GET /v1/cash-flow/{symbol}/annual
```

### 18. 분기별 재무제표 조회
```http
GET /v1/balance-sheets/{symbol}/quarterly
GET /v1/income-statements/{symbol}/quarterly
GET /v1/cash-flow/{symbol}/quarterly
```

### 19. 연도별 재무제표 조회
```http
GET /v1/balance-sheets/year/{year}
GET /v1/income-statements/year/{year}
GET /v1/cash-flow/year/{year}
```

### 20. 기간별 일별 주가 조회
```http
GET /v1/daily-prices/range?startDate=2024-01-01&endDate=2024-01-31
```

### 21. 높은 관련도 뉴스 조회
```http
GET /v1/news/high-relevance?minRelevance=0.8
```

### 22. 감정별 뉴스 조회
```http
GET /v1/news/sentiment/{sentimentLabel}
```

### 23. 뉴스 상세 조회
```http
GET /v1/news/{id}
```

### 24. 종목별 뉴스 개수 조회
```http
GET /v1/news/count/symbol/{symbol}
```

### 25. 종목별 뉴스 개수 랭킹 조회
```http
GET /v1/news/count/ranking
```

### 26. 메타데이터 조회 API
```http
GET /v1/news/sources          # 뉴스 소스 목록
GET /v1/news/categories       # 뉴스 카테고리 목록
GET /v1/news/source-domains   # 뉴스 소스 도메인 목록
GET /v1/news/sentiment-labels # 감정 라벨 목록
```

---

## 📝 개발 참고사항

### 1. 실시간 데이터 처리
- **SSE 연결**: EventSource API 사용
- **자동 재연결**: 연결 끊김 시 자동 재연결 로직 구현
- **메모리 관리**: 불필요한 연결 정리

### 2. 성능 최적화
- **데이터 캐싱**: 정적 데이터는 로컬 캐싱 활용
- **배치 처리**: 여러 API 호출을 한 번에 처리
- **가상 스크롤**: 대용량 리스트 처리

### 3. 차트 구현
- **캔들스틱 차트**: daily-prices 데이터 활용
- **실시간 차트**: SSE 데이터로 업데이트
- **거래량 차트**: 가격 차트와 동기화

### 4. 정렬 및 필터링
- **다중 정렬**: 여러 기준으로 정렬 가능
- **실시간 업데이트**: 정렬 상태 유지하며 데이터 업데이트

### 5. 모바일 최적화
- **반응형 디자인**: 화면 크기별 최적화
- **터치 제스처**: 스와이프, 핀치 줌 지원
- **성능 고려**: 모바일 환경에서의 데이터 사용량 최적화

---

이 API 명세서를 바탕으로 토스 증권 스타일의 현대적이고 직관적인 주식 대시보드를 구현하실 수 있습니다. 실시간 데이터 스트리밍과 풍부한 금융 정보를 통해 사용자에게 종합적인 투자 인사이트를 제공하는 것이 목표입니다.