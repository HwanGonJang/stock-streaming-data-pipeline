import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Head from 'next/head';
import { useStockStore } from '@/store/stocks';
import { useUIStore } from '@/store/ui';
import { SUPPORTED_SYMBOLS } from '@/lib/constants';
import { Card, Badge } from '@/components/ui';
import { TrendingUp, TrendingDown, Star } from 'lucide-react';
import { cn } from '@/lib/utils';
import Layout from '@/components/layout/Layout';

export default function StocksPage() {
  const router = useRouter();
  const { latestPrices, recommendations, favorites, addToFavorites, removeFromFavorites } = useStockStore();
  const { theme } = useUIStore();

  const toggleFavorite = (symbol: string) => {
    if (favorites.includes(symbol)) {
      removeFromFavorites(symbol);
    } else {
      addToFavorites(symbol);
    }
  };

  useEffect(() => {
    // Optionally fetch initial data here
  }, []);

  const handleStockClick = (symbol: string) => {
    router.push(`/stocks/${symbol}`);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(price);
  };

  const formatPercentage = (value: number) => {
    const sign = value >= 0 ? '+' : '';
    return `${sign}${value.toFixed(2)}%`;
  };

  const getRecommendationColor = (score: number) => {
    if (score >= 0.8) return 'text-green-600 dark:text-green-400';
    if (score >= 0.6) return 'text-blue-600 dark:text-blue-400';
    if (score >= 0.4) return 'text-yellow-600 dark:text-yellow-400';
    if (score >= 0.2) return 'text-orange-600 dark:text-orange-400';
    return 'text-red-600 dark:text-red-400';
  };

  return (
    <>
      <Head>
        <title>All Stocks - Gon Stock Dashboard</title>
        <meta name="description" content="View all supported stocks with real-time prices and recommendations" />
      </Head>

      <Layout>
        <div className="space-y-6">
          {/* Header Section */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                All Stocks
              </h1>
              <p className="text-gray-600 dark:text-gray-400 mt-1">
                View all {SUPPORTED_SYMBOLS.length} supported stocks with real-time prices and recommendations
              </p>
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {SUPPORTED_SYMBOLS.map((symbol) => {
          const price = latestPrices[symbol];
          const recommendation = recommendations.find(r => r.symbol === symbol);
          const isFavorite = favorites.includes(symbol);

          return (
            <Card
              key={symbol}
              className="cursor-pointer hover:shadow-md transition-shadow"
              onClick={() => handleStockClick(symbol)}
            >
              <div className="p-4">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="font-semibold text-lg text-gray-900 dark:text-white">
                    {symbol}
                  </h3>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleFavorite(symbol);
                    }}
                    className={cn(
                      'p-1 rounded-full hover:bg-gray-100 dark:hover:bg-dark-800',
                      isFavorite ? 'text-yellow-500' : 'text-gray-400'
                    )}
                  >
                    <Star className={cn('h-4 w-4', isFavorite && 'fill-current')} />
                  </button>
                </div>

                {price && (
                  <div className="space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="text-xl font-bold text-gray-900 dark:text-white">
                        {formatPrice(price.close)}
                      </span>
                      <div className={cn(
                        'flex items-center text-sm font-medium',
                        (price.close - price.open) >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
                      )}>
                        {(price.close - price.open) >= 0 ? (
                          <TrendingUp className="h-4 w-4 mr-1" />
                        ) : (
                          <TrendingDown className="h-4 w-4 mr-1" />
                        )}
                        {formatPercentage(((price.close - price.open) / price.open) * 100)}
                      </div>
                    </div>

                    <div className="text-sm text-gray-500 dark:text-gray-400">
                      {formatPrice(price.close - price.open)} today
                    </div>
                  </div>
                )}

                {recommendation && (
                  <div className="mt-3 pt-3 border-t border-gray-200 dark:border-dark-700">
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-gray-500 dark:text-gray-400">
                        Recommendation
                      </span>
                      <div className="relative group">
                        <Badge
                          variant="secondary"
                          className={cn(
                            'text-xs font-medium cursor-help',
                            getRecommendationColor(recommendation.recommendationScore)
                          )}
                        >
                          {recommendation.recommendationLabel}
                        </Badge>
                        <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10">
                          Recommendation Scale:<br/>
                          Strong Buy (0.8000 – 1.0000): Indicates a strong expectation of upward movement.<br/>
                          Buy (0.6000 – 0.7999): Suggests favorable conditions for buying.<br/>
                          Hold (0.4000 – 0.5999): Advises holding the stock with no strong bias.<br/>
                          Sell (0.2000 – 0.3999): Recommends selling due to weaker outlook.<br/>
                          Strong Sell (0.0000 – 0.1999): Strongly advises selling to avoid potential losses.
                        </div>
                      </div>
                    </div>
                    <div className="mt-1 text-sm font-medium text-gray-900 dark:text-white">
                      Score: {recommendation.recommendationScore.toFixed(2)}/1.0
                    </div>
                  </div>
                )}

                {!price && (
                  <div className="text-sm text-gray-500 dark:text-gray-400">
                    No data available
                  </div>
                )}
              </div>
            </Card>
          );
          })}
          </div>
        </div>
      </Layout>
    </>
  );
}