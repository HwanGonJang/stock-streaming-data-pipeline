// Common API Response Structure
export interface ApiResponse<T> {
  traceId: string;
  status: number;
  code: string;
  timestamp: string;
  body: T;
}

// Pagination Response
export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    sort: {
      sorted: boolean;
      unsorted: boolean;
    };
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
  };
}

// Stock Basic Information
export interface StockInfo {
  symbol: string;
  name: string;
  exchange: string;
  assetType: string;
  ipoDate: string;
  delistingDate: string | null;
  status: string;
  lastUpdatedAt: string;
}

// Company Overview
export interface CompanyOverview {
  symbol: string;
  description: string;
  currency: string;
  country: string;
  sector: string;
  industry: string;
  address: string;
  fiscalYearEnd: string;
  latestQuarter: string;
  marketCapitalization: number;
  ebitda: number;
  peRatio: number;
  pegRatio: number;
  bookValue: number;
  dividendPerShare: number;
  dividendYield: number;
  eps: number;
  revenuePerShareTtm: number;
  profitMargin: number;
  operatingMarginTtm: number;
  returnOnAssetsTtm: number;
  returnOnEquityTtm: number;
  revenueTtm: number;
  grossProfitTtm: number;
  dilutedEpsTtm: number;
  quarterlyEarningsGrowthYoy: number;
  quarterlyRevenueGrowthYoy: number;
  analystTargetPrice: number;
  trailingPe: number;
  forwardPe: number;
  priceToSalesRatioTtm: number;
  priceToBookRatio: number;
  evToRevenue: number;
  evToEbitda: number;
  beta: number;
  fiftyTwoWeekHigh: number;
  fiftyTwoWeekLow: number;
  fiftyDayMovingAverage: number;
  twoHundredDayMovingAverage: number;
  sharesOutstanding: number;
  sharesFloat: number;
  sharesShort: number;
  sharesShortPriorMonth: number;
  shortRatio: number;
  shortPercentOutstanding: number;
  shortPercentFloat: number;
  percentInsiders: number;
  percentInstitutions: number;
  forwardAnnualDividendRate: number;
  forwardAnnualDividendYield: number;
  payoutRatio: number;
  dividendDate: string;
  exDividendDate: string;
  lastSplitFactor: string;
  lastSplitDate: string;
  lastUpdated: string;
}

// Financial Statements
export interface BalanceSheet {
  symbol: string;
  fiscalDateEnding: string;
  reportedCurrency: string;
  totalAssets: number;
  totalCurrentAssets: number;
  cashAndShortTermInvestments: number;
  inventory: number;
  currentNetReceivables: number;
  totalNonCurrentAssets: number;
  propertyPlantEquipment: number;
  goodwill: number;
  intangibleAssets: number;
  totalLiabilities: number;
  totalCurrentLiabilities: number;
  currentAccountsPayable: number;
  shortTermDebt: number;
  totalNonCurrentLiabilities: number;
  longTermDebt: number;
  totalShareholderEquity: number;
  commonStock: number;
  retainedEarnings: number;
  lastUpdated: string;
}

export interface IncomeStatement {
  symbol: string;
  fiscalDateEnding: string;
  reportedCurrency: string;
  totalRevenue: number;
  costOfRevenue: number;
  grossProfit: number;
  operatingIncome: number;
  netIncome: number;
  ebitda: number;
  eps: number;
  dilutedEps: number;
  operatingCashflow: number;
  researchAndDevelopment: number;
  sellingGeneralAdministrative: number;
  interestExpense: number;
  incomeTaxExpense: number;
  lastUpdated: string;
}

export interface CashFlow {
  symbol: string;
  fiscalDateEnding: string;
  reportedCurrency: string;
  operatingCashflow: number;
  paymentsForOperatingActivities: number;
  proceedsFromOperatingActivities: number;
  capitalExpenditures: number;
  investmentCashflow: number;
  cashflowFromInvestment: number;
  cashflowFromFinancing: number;
  proceedsFromRepaymentsOfShortTermDebt: number;
  paymentsForRepurchaseOfCommonStock: number;
  paymentsForRepurchaseOfEquity: number;
  dividendPayout: number;
  changeInCashAndCashEquivalents: number;
  cashAndCashEquivalentsAtCarryingValue: number;
  lastUpdated: string;
}

// News
export interface NewsArticle {
  id: number;
  title: string;
  url: string;
  timePublished: string;
  summary: string;
  bannerImage: string;
  source: string;
  categoryWithinSource: string;
  sourceUrl: string;
  topics: string[];
  overallSentimentScore: number;
  overallSentimentLabel: string;
  tickerSentiment: TickerSentiment[];
}

export interface TickerSentiment {
  ticker: string;
  relevanceScore: number;
  tickerSentimentScore: number;
  tickerSentimentLabel: string;
}

export interface StockNews {
  id: number;
  newsArticle: NewsArticle;
  symbol: string;
  relevanceScore: number;
  sentimentScore: number;
  sentimentLabel: string;
}

// Stock Recommendation
export interface StockRecommendation {
  id: number;
  symbol: string;
  recommendationScore: number;
  recommendationLabel: string;
  summary: string;
  createdAt: string;
  lastUpdatedAt: string;
}

// Daily Price Data
export interface DailyPrice {
  id: number;
  symbol: string;
  date: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

// Real-time Stock Data
export interface RealTimeStock {
  uuid: string;
  symbol: string;
  tradeConditions: string;
  price: number;
  volume: number;
  tradeTimestamp: string;
  ingestTimestamp: string;
}

// Query Parameters
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

export interface FinancialDataParams extends PaginationParams {
  isQuarterly?: boolean;
  startDate?: string;
  endDate?: string;
  year?: number;
  quarter?: number;
}

export interface NewsParams extends PaginationParams {
  startDate?: string;
  endDate?: string;
  source?: string;
  category?: string;
  sourceDomain?: string;
  sentimentLabel?: string;
  minSentimentScore?: number;
  maxSentimentScore?: number;
  keyword?: string;
  minRelevanceScore?: number;
}

export interface DailyPriceParams extends PaginationParams {
  startDate?: string;
  endDate?: string;
  year?: number;
  quarter?: number;
  month?: number;
  minVolume?: number;
  sortOrder?: 'asc' | 'desc';
}

// Error Response
export interface ErrorResponse {
  message: string;
}

// Supported Stock Symbols
export const SUPPORTED_SYMBOLS = [
  'AAPL', 'MSFT', 'GOOGL', 'AMZN', 'META', 'NVDA', 'TSLA', 'AVGO',
  'CRM', 'ORCL', 'NFLX', 'ADBE', 'AMD', 'INTC', 'PYPL', 'CSCO',
  'QCOM', 'TXN', 'AMAT', 'PLTR'
] as const;
// export const SUPPORTED_SYMBOLS = [
//   'AAPL', 'MSFT'
// ] as const;

export type SupportedSymbol = typeof SUPPORTED_SYMBOLS[number];

// Recommendation Labels
export type RecommendationLabel = 
  | 'Strong Buy' 
  | 'Buy' 
  | 'Hold' 
  | 'Sell' 
  | 'Strong Sell';

// Sentiment Labels
export type SentimentLabel = 'Positive' | 'Negative' | 'Neutral';