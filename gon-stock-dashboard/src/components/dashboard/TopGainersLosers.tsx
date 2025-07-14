import Link from 'next/link';
import { TrendingUp, TrendingDown, ArrowRight } from 'lucide-react';
import { useStockStore } from '@/store/stocks';
import { Card, CardHeader, CardTitle, CardContent, Button, LoadingCard } from '@/components/ui';
import { formatCurrency, formatPercentage, cn } from '@/lib/utils';
import { COMPANY_NAMES, SUPPORTED_SYMBOLS } from '@/lib/constants';
import { SupportedSymbol } from '@/types/api';

export default function TopGainersLosers() {
  const { 
    latestPrices, 
    realTimePrices,
    isLoading 
  } = useStockStore();

  // Calculate gainers and losers
  const stocksWithChanges = SUPPORTED_SYMBOLS.map((symbol) => {
    const latestPrice = latestPrices[symbol];
    const realTimePrice = realTimePrices[symbol];
    
    if (!latestPrice) return null;
    
    const currentPrice = realTimePrice?.price || latestPrice.close;
    const change = currentPrice - latestPrice.open;
    const changePercent = (change / latestPrice.open) * 100;
    
    return {
      symbol,
      currentPrice,
      change,
      changePercent,
      isRealTime: !!realTimePrice
    };
  }).filter(Boolean);

  const topGainers = [...stocksWithChanges]
    .sort((a, b) => b!.changePercent - a!.changePercent)
    .slice(0, 3);

  const topLosers = [...stocksWithChanges]
    .sort((a, b) => a!.changePercent - b!.changePercent)
    .slice(0, 3);

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <TrendingUp className="h-5 w-5 text-green-600" />
              <span>Top Gainers</span>
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

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <TrendingDown className="h-5 w-5 text-red-600" />
              <span>Top Losers</span>
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
      </div>
    );
  }

  const StockItem = ({ stock, index, type }: { stock: any, index: number, type: 'gainer' | 'loser' }) => (
    <div className="flex items-center justify-between p-4 bg-gray-50 dark:bg-dark-800 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-700 transition-colors">
      <div className="flex items-center space-x-4">
        <div className="flex items-center space-x-2">
          <span className={cn(
            'text-lg font-bold',
            type === 'gainer' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
          )}>
            #{index + 1}
          </span>
        </div>
        
        <div className="flex-1">
          <div className="flex items-center space-x-2">
            <Link href={`/stocks/${stock.symbol}`} className="hover:text-primary-600 dark:hover:text-primary-400">
              <h3 className="font-medium text-lg">{stock.symbol}</h3>
            </Link>
            {stock.isRealTime && (
              <div className="h-2 w-2 bg-green-500 rounded-full animate-pulse" />
            )}
          </div>
          <p className="text-sm text-gray-600 dark:text-gray-400">
            {COMPANY_NAMES[stock.symbol as SupportedSymbol] || 'Unknown Company'}
          </p>
        </div>
      </div>

      <div className="text-right space-y-1">
        <div className="text-lg font-semibold">
          {formatCurrency(stock.currentPrice)}
        </div>
        <div className={cn(
          'flex items-center text-sm font-medium',
          stock.change >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
        )}>
          {stock.change >= 0 ? (
            <TrendingUp className="h-4 w-4 mr-1" />
          ) : (
            <TrendingDown className="h-4 w-4 mr-1" />
          )}
          {formatPercentage(stock.changePercent)}
        </div>
      </div>
    </div>
  );

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <TrendingUp className="h-5 w-5 text-green-600" />
              <span>Top Gainers</span>
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
            {topGainers.map((stock, index) => (
              <StockItem key={stock!.symbol} stock={stock} index={index} type="gainer" />
            ))}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <TrendingDown className="h-5 w-5 text-red-600" />
              <span>Top Losers</span>
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
            {topLosers.map((stock, index) => (
              <StockItem key={stock!.symbol} stock={stock} index={index} type="loser" />
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}