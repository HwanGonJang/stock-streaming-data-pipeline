import { useMemo } from 'react';
import { TrendingUp, TrendingDown, DollarSign, Activity, Users, Clock } from 'lucide-react';
import { useStockStore } from '@/store/stocks';
import { Card, CardContent, LoadingSkeleton } from '@/components/ui';
import { formatCurrency, formatLargeNumber, formatPercentage, calculateChange, cn } from '@/lib/utils';
import { SUPPORTED_SYMBOLS } from '@/lib/constants';

export default function MarketSummary() {
  const { 
    recommendations, 
    latestPrices, 
    realTimePrices,
    companyOverviews,
    lastUpdated,
    isLoading 
  } = useStockStore();

  const marketStats = useMemo(() => {
    if (!recommendations.length) return null;

    const stocks = SUPPORTED_SYMBOLS.map(symbol => {
      const currentPrice = realTimePrices[symbol]?.price || latestPrices[symbol]?.close || 0;
      const previousPrice = latestPrices[symbol]?.open || 0;
      const change = calculateChange(currentPrice, previousPrice);
      const volume = latestPrices[symbol]?.volume || 0;
      const marketCap = companyOverviews[symbol]?.marketCapitalization || 0;
      const recommendation = recommendations.find(r => r.symbol === symbol);

      return {
        symbol,
        currentPrice,
        previousPrice,
        change,
        volume,
        marketCap,
        recommendationScore: recommendation?.recommendationScore || 0,
      };
    }).filter(stock => stock.currentPrice > 0);

    const totalMarketCap = stocks.reduce((sum, stock) => sum + stock.marketCap, 0);
    const totalVolume = stocks.reduce((sum, stock) => sum + stock.volume, 0);
    const averageChange = stocks.reduce((sum, stock) => sum + stock.change, 0) / stocks.length;
    const averageRecommendation = stocks.reduce((sum, stock) => sum + stock.recommendationScore, 0) / stocks.length;

    const gainers = stocks.filter(stock => stock.change > 0).length;
    const losers = stocks.filter(stock => stock.change < 0).length;
    const unchanged = stocks.filter(stock => stock.change === 0).length;

    const topGainer = stocks.reduce((prev, current) => 
      prev.change > current.change ? prev : current
    );
    const topLoser = stocks.reduce((prev, current) => 
      prev.change < current.change ? prev : current
    );

    return {
      totalMarketCap,
      totalVolume,
      averageChange,
      averageRecommendation,
      gainers,
      losers,
      unchanged,
      topGainer,
      topLoser,
      totalStocks: stocks.length,
    };
  }, [recommendations, latestPrices, realTimePrices, companyOverviews]);

  if (isLoading || !marketStats) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <Card key={index}>
            <CardContent className="p-6">
              <LoadingSkeleton rows={3} />
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  const summaryCards = [
    {
      title: 'Market Overview',
      value: formatLargeNumber(marketStats.totalMarketCap),
      subtitle: 'Total Market Cap',
      icon: DollarSign,
      color: 'text-blue-600 dark:text-blue-400',
      bgColor: 'bg-blue-100 dark:bg-blue-900/20',
      change: marketStats.averageChange,
      changeText: `${formatPercentage(marketStats.averageChange)} avg change`,
    },
    {
      title: 'Trading Volume',
      value: formatLargeNumber(marketStats.totalVolume),
      subtitle: 'Total Volume',
      icon: Activity,
      color: 'text-green-600 dark:text-green-400',
      bgColor: 'bg-green-100 dark:bg-green-900/20',
      change: 0,
      changeText: `${marketStats.totalStocks} stocks tracked`,
    },
    {
      title: 'Market Sentiment',
      value: `${(marketStats.averageRecommendation * 100).toFixed(1)}%`,
      subtitle: 'Avg Recommendation',
      icon: Users,
      color: 'text-purple-600 dark:text-purple-400',
      bgColor: 'bg-purple-100 dark:bg-purple-900/20',
      change: marketStats.averageRecommendation - 0.5,
      changeText: marketStats.averageRecommendation >= 0.6 ? 'Positive sentiment' : 'Neutral sentiment',
    },
    {
      title: 'Market Movers',
      value: `${marketStats.gainers}/${marketStats.losers}`,
      subtitle: 'Gainers/Losers',
      icon: TrendingUp,
      color: marketStats.gainers > marketStats.losers ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400',
      bgColor: marketStats.gainers > marketStats.losers ? 'bg-green-100 dark:bg-green-900/20' : 'bg-red-100 dark:bg-red-900/20',
      change: marketStats.gainers - marketStats.losers,
      changeText: `${marketStats.unchanged} unchanged`,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {summaryCards.map((card, index) => (
          <Card key={index} className="hover:shadow-lg transition-shadow">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className={cn('p-2 rounded-lg', card.bgColor)}>
                    <card.icon className={cn('h-6 w-6', card.color)} />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                      {card.title}
                    </p>
                    <p className="text-2xl font-bold text-gray-900 dark:text-white">
                      {card.value}
                    </p>
                  </div>
                </div>
              </div>
              <div className="mt-4 flex items-center justify-between">
                <span className="text-sm text-gray-500 dark:text-gray-400">
                  {card.subtitle}
                </span>
                <span className={cn(
                  'text-sm font-medium',
                  card.change > 0 ? 'text-green-600 dark:text-green-400' : 
                  card.change < 0 ? 'text-red-600 dark:text-red-400' : 
                  'text-gray-600 dark:text-gray-400'
                )}>
                  {card.changeText}
                </span>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Top Movers */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold flex items-center space-x-2">
                <TrendingUp className="h-5 w-5 text-green-600" />
                <span>Top Gainer</span>
              </h3>
              <span className="text-sm text-gray-500 dark:text-gray-400">
                {formatPercentage(marketStats.topGainer.change)}
              </span>
            </div>
            <div className="flex items-center space-x-3">
              <div className="text-2xl font-bold text-green-600">
                {marketStats.topGainer.symbol}
              </div>
              <div className="text-right">
                <div className="text-lg font-semibold">
                  {formatCurrency(marketStats.topGainer.currentPrice)}
                </div>
                <div className="text-sm text-green-600">
                  +{formatCurrency(marketStats.topGainer.currentPrice - marketStats.topGainer.previousPrice)}
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold flex items-center space-x-2">
                <TrendingDown className="h-5 w-5 text-red-600" />
                <span>Top Loser</span>
              </h3>
              <span className="text-sm text-gray-500 dark:text-gray-400">
                {formatPercentage(marketStats.topLoser.change)}
              </span>
            </div>
            <div className="flex items-center space-x-3">
              <div className="text-2xl font-bold text-red-600">
                {marketStats.topLoser.symbol}
              </div>
              <div className="text-right">
                <div className="text-lg font-semibold">
                  {formatCurrency(marketStats.topLoser.currentPrice)}
                </div>
                <div className="text-sm text-red-600">
                  {formatCurrency(marketStats.topLoser.currentPrice - marketStats.topLoser.previousPrice)}
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Last Updated */}
      <div className="text-center">
        <div className="inline-flex items-center space-x-2 text-sm text-gray-500 dark:text-gray-400">
          <Clock className="h-4 w-4" />
          <span>Last updated: {(() => {
            const utcDate = new Date(lastUpdated);
            const edtDate = new Date(utcDate.getTime() - (4 * 60 * 60 * 1000)); // UTC-4 for EDT
            return edtDate.toLocaleString('en-US', {
              year: 'numeric',
              month: 'short',
              day: 'numeric',
              hour: '2-digit',
              minute: '2-digit',
              second: '2-digit',
              hour12: true
            }) + ' EDT';
          })()}</span>
        </div>
      </div>
    </div>
  );
}