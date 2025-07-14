import { apiClient } from './client';
import {
  StockInfo,
  CompanyOverview,
  BalanceSheet,
  IncomeStatement,
  CashFlow,
  NewsArticle,
  StockNews,
  StockRecommendation,
  DailyPrice,
  RealTimeStock,
  FinancialDataParams,
  NewsParams,
  DailyPriceParams,
  PaginationParams,
  SupportedSymbol,
} from '@/types/api';

export class StocksApi {
  // Stock Basic Information
  async getStockInfo(symbol: SupportedSymbol): Promise<StockInfo> {
    return apiClient.get<StockInfo>(`/v1/stocks/info/${symbol}`);
  }

  async getCompanyOverview(symbol: SupportedSymbol): Promise<CompanyOverview> {
    return apiClient.get<CompanyOverview>(`/v1/company-overview/${symbol}`);
  }

  // Financial Data
  async getBalanceSheet(
    symbol: SupportedSymbol,
    params?: FinancialDataParams
  ): Promise<BalanceSheet> {
    return apiClient.get<BalanceSheet>(`/v1/balance-sheets/${symbol}`, { params });
  }

  async getIncomeStatement(
    symbol: SupportedSymbol,
    params?: FinancialDataParams
  ): Promise<IncomeStatement> {
    return apiClient.get<IncomeStatement>(`/v1/income-statements/${symbol}`, { params });
  }

  async getCashFlow(
    symbol: SupportedSymbol,
    params?: FinancialDataParams
  ): Promise<CashFlow> {
    return apiClient.get<CashFlow>(`/v1/cash-flow/${symbol}`, { params });
  }

  // Latest Financial Data
  async getLatestBalanceSheet(
    symbol: SupportedSymbol,
    isQuarterly: boolean = false
  ): Promise<BalanceSheet> {
    return apiClient.get<BalanceSheet>(
      `/v1/balance-sheets/${symbol}/latest`,
      { params: { isQuarterly } }
    );
  }

  async getLatestIncomeStatement(
    symbol: SupportedSymbol,
    isQuarterly: boolean = false
  ): Promise<IncomeStatement> {
    return apiClient.get<IncomeStatement>(
      `/v1/income-statements/${symbol}/latest`,
      { params: { isQuarterly } }
    );
  }

  async getLatestCashFlow(
    symbol: SupportedSymbol,
    isQuarterly: boolean = false
  ): Promise<CashFlow> {
    return apiClient.get<CashFlow>(
      `/v1/cash-flow/${symbol}/latest`,
      { params: { isQuarterly } }
    );
  }

  // News
  async getAllNews(params?: NewsParams): Promise<NewsArticle[]> {
    const response = await apiClient.get<{ content: NewsArticle[]; totalElements: number; }>('/v1/news', { params });
    return response.content || [];
  }

  async getStockNews(
    symbol: SupportedSymbol,
    params?: NewsParams
  ): Promise<StockNews[]> {
    const response = await apiClient.get<{ content: StockNews[]; totalElements: number; }>(`/v1/news/symbol/${symbol}`, { params });
    return response.content || [];
  }

  async getNewsById(id: number): Promise<NewsArticle> {
    return apiClient.get<NewsArticle>(`/v1/news/${id}`);
  }

  // Stock Recommendations
  async getStockRecommendation(symbol: SupportedSymbol): Promise<StockRecommendation> {
    return apiClient.get<StockRecommendation>(`/v1/stocks/recommendations/${symbol}`);
  }

  async getAllStockRecommendations(params?: PaginationParams): Promise<StockRecommendation[]> {
    return apiClient.get<StockRecommendation[]>('/v1/stocks/recommendations', { params });
  }

  // Daily Price Data
  async getDailyPrices(
    symbol: SupportedSymbol,
    params?: DailyPriceParams
  ): Promise<DailyPrice[]> {
    const response = await apiClient.get<{ content: DailyPrice[]; totalElements: number; }>(`/v1/daily-prices/${symbol}`, { params });
    return response.content || [];
  }

  async getLatestDailyPrice(symbol: SupportedSymbol): Promise<DailyPrice> {
    return apiClient.get<DailyPrice>(`/v1/daily-prices/${symbol}/latest`);
  }

  // Real-time Data
  async getLatestRealTimeStock(symbol: SupportedSymbol): Promise<RealTimeStock> {
    return apiClient.get<RealTimeStock>(`/v1/stocks/stream/${symbol}/latest`);
  }

  // Server-Sent Events for real-time streaming
  subscribeToRealTimeStock(symbol: SupportedSymbol, useKoreanTimeSimulation: boolean = false): EventSource {
    return apiClient.createSSE(`/v1/stocks/stream/${symbol}`, {
      intervalSeconds: 5,
      useKoreanTimeSimulation
    });
  }

  // Batch operations for dashboard
  async getDashboardData(symbols: SupportedSymbol[]): Promise<{
    recommendations: StockRecommendation[];
    latestPrices: Record<string, DailyPrice>;
    companyOverviews: Record<string, CompanyOverview>;
  }> {
    const [recommendations, ...priceResults] = await Promise.all([
      this.getAllStockRecommendations({ size: 20 }),
      ...symbols.map((symbol) => this.getLatestDailyPrice(symbol).catch(() => null)),
    ]);

    const [, ...overviewResults] = await Promise.all([
      Promise.resolve(),
      ...symbols.map((symbol) => this.getCompanyOverview(symbol).catch(() => null)),
    ]);

    const latestPrices: Record<string, DailyPrice> = {};
    const companyOverviews: Record<string, CompanyOverview> = {};

    symbols.forEach((symbol, index) => {
      if (priceResults[index]) {
        latestPrices[symbol] = priceResults[index];
      }
      if (overviewResults[index]) {
        companyOverviews[symbol] = overviewResults[index];
      }
    });

    return {
      recommendations,
      latestPrices,
      companyOverviews,
    };
  }

  async getStockDetailData(symbol: SupportedSymbol): Promise<{
    info: StockInfo;
    overview: CompanyOverview;
    recommendation: StockRecommendation;
    dailyPrices: DailyPrice[];
    news: StockNews[];
    latestPrice: RealTimeStock;
  }> {
    const [info, overview, recommendation, dailyPrices, news, latestPrice] = await Promise.all([
      this.getStockInfo(symbol),
      this.getCompanyOverview(symbol),
      this.getStockRecommendation(symbol),
      this.getDailyPrices(symbol, { size: 100 }),
      this.getStockNews(symbol, { size: 10 }),
      this.getLatestRealTimeStock(symbol),
    ]);

    return {
      info,
      overview,
      recommendation,
      dailyPrices,
      news,
      latestPrice,
    };
  }
}

export const stocksApi = new StocksApi();