import { SupportedSymbol } from '@/types/api';

// export const SUPPORTED_SYMBOLS: SupportedSymbol[] = [
//   'AAPL', 'MSFT', 'GOOGL', 'AMZN', 'META', 'NVDA', 'TSLA', 'AVGO',
//   'CRM', 'ORCL', 'NFLX', 'ADBE', 'AMD', 'INTC', 'PYPL', 'CSCO',
//   'QCOM', 'TXN', 'AMAT', 'PLTR'
// ];
export const SUPPORTED_SYMBOLS: SupportedSymbol[] = [
  'AAPL', 'MSFT'
];

export const COMPANY_NAMES: Record<SupportedSymbol, string> = {
  AAPL: 'Apple Inc.',
  MSFT: 'Microsoft Corporation',
  GOOGL: 'Alphabet Inc.',
  AMZN: 'Amazon.com Inc.',
  META: 'Meta Platforms Inc.',
  NVDA: 'NVIDIA Corporation',
  TSLA: 'Tesla Inc.',
  AVGO: 'Broadcom Inc.',
  CRM: 'Salesforce Inc.',
  ORCL: 'Oracle Corporation',
  NFLX: 'Netflix Inc.',
  ADBE: 'Adobe Inc.',
  AMD: 'Advanced Micro Devices Inc.',
  INTC: 'Intel Corporation',
  PYPL: 'PayPal Holdings Inc.',
  CSCO: 'Cisco Systems Inc.',
  QCOM: 'QUALCOMM Incorporated',
  TXN: 'Texas Instruments Incorporated',
  AMAT: 'Applied Materials Inc.',
  PLTR: 'Palantir Technologies Inc.'
};

export const RECOMMENDATION_LABELS = {
  'Strong Buy': { min: 0.8, max: 1.0, color: 'text-green-600 dark:text-green-400' },
  'Buy': { min: 0.6, max: 0.7999, color: 'text-blue-600 dark:text-blue-400' },
  'Hold': { min: 0.4, max: 0.5999, color: 'text-yellow-600 dark:text-yellow-400' },
  'Sell': { min: 0.2, max: 0.3999, color: 'text-orange-600 dark:text-orange-400' },
  'Strong Sell': { min: 0.0, max: 0.1999, color: 'text-red-600 dark:text-red-400' }
};

export const SENTIMENT_LABELS = {
  'Positive': { color: 'text-green-600 dark:text-green-400', bg: 'bg-green-100 dark:bg-green-900/20' },
  'Negative': { color: 'text-red-600 dark:text-red-400', bg: 'bg-red-100 dark:bg-red-900/20' },
  'Neutral': { color: 'text-gray-600 dark:text-gray-400', bg: 'bg-gray-100 dark:bg-gray-900/20' }
};

export const CHART_COLORS = {
  primary: '#3b82f6',
  secondary: '#8b5cf6',
  success: '#10b981',
  danger: '#ef4444',
  warning: '#f59e0b',
  info: '#06b6d4',
  bull: '#ff6b6b',
  bear: '#4ecdc4',
  neutral: '#95a5a6'
};

export const SORT_OPTIONS = [
  { value: 'recommendationScore,desc', label: 'Recommendation Score' },
  { value: 'symbol,asc', label: 'Symbol (A-Z)' },
  { value: 'symbol,desc', label: 'Symbol (Z-A)' },
  { value: 'marketCapitalization,desc', label: 'Market Cap (High to Low)' },
  { value: 'marketCapitalization,asc', label: 'Market Cap (Low to High)' },
  { value: 'volume,desc', label: 'Volume (High to Low)' },
  { value: 'volume,asc', label: 'Volume (Low to High)' }
];

export const TIME_PERIODS = {
  '1D': { label: '1 Day', value: 1, unit: 'day' },
  '1W': { label: '1 Week', value: 7, unit: 'day' },
  '1M': { label: '1 Month', value: 1, unit: 'month' },
  '3M': { label: '3 Months', value: 3, unit: 'month' },
  '6M': { label: '6 Months', value: 6, unit: 'month' },
  '1Y': { label: '1 Year', value: 1, unit: 'year' },
  '2Y': { label: '2 Years', value: 2, unit: 'year' },
  '5Y': { label: '5 Years', value: 5, unit: 'year' }
};

export const CHART_TYPES = {
  line: { label: 'Line Chart', icon: '📈' },
  candlestick: { label: 'Candlestick', icon: '📊' },
  volume: { label: 'Volume', icon: '📊' }
};

export const FINANCIAL_METRICS = {
  profitability: [
    { key: 'profitMargin', label: 'Profit Margin', format: 'percentage' },
    { key: 'operatingMarginTtm', label: 'Operating Margin', format: 'percentage' },
    { key: 'returnOnAssetsTtm', label: 'Return on Assets', format: 'percentage' },
    { key: 'returnOnEquityTtm', label: 'Return on Equity', format: 'percentage' }
  ],
  valuation: [
    { key: 'peRatio', label: 'P/E Ratio', format: 'number' },
    { key: 'pegRatio', label: 'PEG Ratio', format: 'number' },
    { key: 'priceToBookRatio', label: 'P/B Ratio', format: 'number' },
    { key: 'priceToSalesRatioTtm', label: 'P/S Ratio', format: 'number' }
  ],
  growth: [
    { key: 'quarterlyEarningsGrowthYoy', label: 'Earnings Growth', format: 'percentage' },
    { key: 'quarterlyRevenueGrowthYoy', label: 'Revenue Growth', format: 'percentage' }
  ],
  dividend: [
    { key: 'dividendYield', label: 'Dividend Yield', format: 'percentage' },
    { key: 'dividendPerShare', label: 'Dividend Per Share', format: 'currency' },
    { key: 'payoutRatio', label: 'Payout Ratio', format: 'percentage' }
  ]
};

export const DEFAULT_PAGINATION = {
  page: 0,
  size: 20
};

export const SSE_RECONNECT_INTERVAL = 5000; // 5 seconds
export const API_RETRY_ATTEMPTS = 3;
export const API_RETRY_DELAY = 1000; // 1 second

export const BREAKPOINTS = {
  sm: 640,
  md: 768,
  lg: 1024,
  xl: 1280,
  '2xl': 1536
};

export const STORAGE_KEYS = {
  theme: 'gon-stock-theme',
  favorites: 'gon-stock-favorites',
  dashboardSettings: 'gon-stock-dashboard-settings',
  chartSettings: 'gon-stock-chart-settings'
};