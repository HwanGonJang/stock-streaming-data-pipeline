import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import {
  StockInfo,
  CompanyOverview,
  StockRecommendation,
  DailyPrice,
  RealTimeStock,
  StockNews,
  SupportedSymbol,
} from '@/types/api';
import { stocksApi } from '@/lib/api/stocks';

interface StockState {
  // Dashboard data
  recommendations: StockRecommendation[];
  latestPrices: Record<string, DailyPrice>;
  companyOverviews: Record<string, CompanyOverview>;
  realTimePrices: Record<string, RealTimeStock>;
  news: Record<string, any[]>;
  allNews: any[];
  
  // Stock detail data
  selectedStock: SupportedSymbol | null;
  stockInfo: StockInfo | null;
  stockOverview: CompanyOverview | null;
  stockRecommendation: StockRecommendation | null;
  dailyPrices: DailyPrice[];
  stockNews: StockNews[];
  
  // UI State
  isLoading: boolean;
  error: string | null;
  lastUpdated: number;
  
  // Sort and filter
  sortBy: string;
  sortOrder: 'asc' | 'desc';
  favorites: SupportedSymbol[];
  
  // Actions
  fetchDashboardData: () => Promise<void>;
  fetchStockDetail: (symbol: SupportedSymbol) => Promise<void>;
  updateRealTimePrice: (symbol: SupportedSymbol, price: RealTimeStock) => void;
  setSortBy: (sortBy: string, sortOrder: 'asc' | 'desc') => void;
  addToFavorites: (symbol: SupportedSymbol) => void;
  removeFromFavorites: (symbol: SupportedSymbol) => void;
  clearError: () => void;
  setSelectedStock: (symbol: SupportedSymbol | null) => void;
}

export const useStockStore = create<StockState>()(
  devtools(
    persist(
      (set, get) => ({
        // Initial state
        recommendations: [],
        latestPrices: {},
        companyOverviews: {},
        realTimePrices: {},
        news: {},
        allNews: [],
        
        selectedStock: null,
        stockInfo: null,
        stockOverview: null,
        stockRecommendation: null,
        dailyPrices: [],
        stockNews: [],
        
        isLoading: false,
        error: null,
        lastUpdated: 0,
        
        sortBy: 'recommendationScore',
        sortOrder: 'desc',
        favorites: [],
        
        // Actions
        fetchDashboardData: async () => {
          set({ isLoading: true, error: null });
          
          try {
            // const symbols: SupportedSymbol[] = [
            //   'AAPL', 'MSFT', 'GOOGL', 'AMZN', 'META', 'NVDA', 'TSLA', 'AVGO',
            //   'CRM', 'ORCL', 'NFLX', 'ADBE', 'AMD', 'INTC', 'PYPL', 'CSCO',
            //   'QCOM', 'TXN', 'AMAT', 'PLTR'
            // ];
            const symbols: SupportedSymbol[] = [
              'AAPL', 'MSFT'
            ];

            const [dashboardData, allNews] = await Promise.all([
              stocksApi.getDashboardData(symbols),
              stocksApi.getAllNews({ size: 20 }).catch(error => {
                console.error('Failed to fetch all news:', error);
                return [];
              })
            ]);

            // Fetch news for each symbol
            const newsResults = await Promise.all(
              symbols.map(symbol => 
                stocksApi.getStockNews(symbol, { size: 10 }).catch(error => {
                  console.error(`Failed to fetch news for ${symbol}:`, error);
                  return { content: [] };
                })
              )
            );

            // Organize news by symbol
            const newsBySymbol: Record<string, any> = {};
            symbols.forEach((symbol, index) => {
              newsBySymbol[symbol] = { content: newsResults[index] };
            });
            
            set({
              recommendations: dashboardData.recommendations,
              latestPrices: dashboardData.latestPrices,
              companyOverviews: dashboardData.companyOverviews,
              news: newsBySymbol,
              allNews: allNews,
              lastUpdated: Date.now(),
              isLoading: false,
            });
          } catch (error) {
            set({
              error: error instanceof Error ? error.message : 'Failed to fetch dashboard data',
              isLoading: false,
            });
          }
        },
        
        fetchStockDetail: async (symbol: SupportedSymbol) => {
          set({ isLoading: true, error: null, selectedStock: symbol });
          
          try {
            const data = await stocksApi.getStockDetailData(symbol);
            
            set({
              stockInfo: data.info,
              stockOverview: data.overview,
              stockRecommendation: data.recommendation,
              dailyPrices: data.dailyPrices,
              stockNews: data.news,
              realTimePrices: {
                ...get().realTimePrices,
                [symbol]: data.latestPrice,
              },
              lastUpdated: Date.now(),
              isLoading: false,
            });
          } catch (error) {
            set({
              error: error instanceof Error ? error.message : 'Failed to fetch stock detail',
              isLoading: false,
            });
          }
        },
        
        updateRealTimePrice: (symbol: SupportedSymbol, price: RealTimeStock) => {
          set((state) => ({
            realTimePrices: {
              ...state.realTimePrices,
              [symbol]: price,
            },
            lastUpdated: Date.now(),
          }));
        },
        
        setSortBy: (sortBy: string, sortOrder: 'asc' | 'desc') => {
          set({ sortBy, sortOrder });
        },
        
        addToFavorites: (symbol: SupportedSymbol) => {
          set((state) => ({
            favorites: [...state.favorites.filter(s => s !== symbol), symbol],
          }));
        },
        
        removeFromFavorites: (symbol: SupportedSymbol) => {
          set((state) => ({
            favorites: state.favorites.filter(s => s !== symbol),
          }));
        },
        
        clearError: () => {
          set({ error: null });
        },
        
        setSelectedStock: (symbol: SupportedSymbol | null) => {
          set({ selectedStock: symbol });
        },
      }),
      {
        name: 'stock-store',
        partialize: (state) => ({
          favorites: state.favorites,
          sortBy: state.sortBy,
          sortOrder: state.sortOrder,
        }),
      }
    ),
    {
      name: 'stock-store',
    }
  )
);