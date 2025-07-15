import Link from 'next/link';
import { TrendingUp, Star, ArrowRight } from 'lucide-react';
import { useStockStore } from '@/store/stocks';
import { Card, CardHeader, CardTitle, CardContent, Button, Badge, LoadingCard } from '@/components/ui';
import { formatCurrency, getRecommendationColor, cn } from '@/lib/utils';
import { COMPANY_NAMES } from '@/lib/constants';
import { SupportedSymbol } from '@/types/api';

export default function StockRecommendations() {
  const { 
    recommendations, 
    latestPrices, 
    realTimePrices,
    favorites,
    addToFavorites,
    removeFromFavorites,
    isLoading 
  } = useStockStore();

  // Get top 3 recommendations
  const topRecommendations = recommendations
    .filter(stock => stock.recommendationScore >= 0.7)
    .sort((a, b) => b.recommendationScore - a.recommendationScore)
    .slice(0, 3);

  const toggleFavorite = (symbol: SupportedSymbol) => {
    if (favorites.includes(symbol)) {
      removeFromFavorites(symbol);
    } else {
      addToFavorites(symbol);
    }
  };

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <TrendingUp className="h-5 w-5 text-primary-600" />
            <span>Top Stock Recommendations</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {Array.from({ length: 3 }).map((_, index) => (
              <LoadingCard key={index} />
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (!topRecommendations.length) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <TrendingUp className="h-5 w-5 text-primary-600" />
            <span>Top Stock Recommendations</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8 text-gray-500 dark:text-gray-400">
            <TrendingUp className="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p>No recommendations available at the moment.</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <TrendingUp className="h-5 w-5 text-primary-600" />
            <span>Top Stock Recommendations</span>
          </div>
          <Link href="/stocks">
            <Button variant="ghost" size="sm">
              View All
              <ArrowRight className="h-4 w-4 ml-1" />
            </Button>
          </Link>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {topRecommendations.map((stock, index) => {
            const symbol = stock.symbol as SupportedSymbol;
            const currentPrice = realTimePrices[symbol]?.price || latestPrices[symbol]?.close || 0;
            const isRealTime = !!realTimePrices[symbol];
            const isFavorite = favorites.includes(symbol);

            return (
              <div
                key={symbol}
                className="flex items-center justify-between p-4 bg-gray-50 dark:bg-dark-800 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-700 transition-colors cursor-pointer"
                onClick={() => window.location.href = `/stocks/${symbol}`}
              >
                <div className="flex items-center space-x-4">
                  <div className="flex items-center space-x-2">
                    <span className="text-lg font-bold text-primary-600 dark:text-primary-400">
                      #{index + 1}
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => toggleFavorite(symbol)}
                      className="p-1 h-8 w-8"
                    >
                      <Star
                        className={cn(
                          'h-4 w-4',
                          isFavorite ? 'fill-yellow-400 text-yellow-400' : 'text-gray-400'
                        )}
                      />
                    </Button>
                  </div>
                  
                  <div className="flex-1">
                    <div className="flex items-center space-x-2">
                      <h3 className="font-medium text-lg">{symbol}</h3>
                      {isRealTime && (
                        <div className="h-2 w-2 bg-green-500 rounded-full animate-pulse" />
                      )}
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {COMPANY_NAMES[symbol] || 'Unknown Company'}
                    </p>
                    <p className="text-sm text-gray-500 dark:text-gray-500 mt-1 line-clamp-2">
                      {stock.summary}
                    </p>
                  </div>
                </div>

                <div className="text-right space-y-2">
                  <div className="text-lg font-semibold">
                    {formatCurrency(currentPrice)}
                  </div>
                  <div className="flex items-center space-x-2">
                    <div className="relative group">
                      <Badge 
                        variant={stock.recommendationScore >= 0.8 ? 'success' : 'info'}
                        size="sm"
                        className="cursor-help"
                      >
                        {stock.recommendationLabel}
                      </Badge>
                      <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10 text-left">
                        Recommendation Scale:<br/>
                        Strong Buy (8.000 – 10.000): Indicates a strong expectation of upward movement.<br/>
                        Buy (6.500 – 7.999): Suggests favorable conditions for buying.<br/>
                        Hold (4.500 – 6.499): Advises holding the stock with no strong bias.<br/>
                        Sell (3.000 – 4.499): Recommends selling due to weaker outlook.<br/>
                        Strong Sell (0.0000 – 2.999): Strongly advises selling to avoid potential losses.
                      </div>
                    </div>
                    <span className={cn('text-sm font-medium', getRecommendationColor(stock.recommendationScore))}>
                      {stock.recommendationScore.toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}