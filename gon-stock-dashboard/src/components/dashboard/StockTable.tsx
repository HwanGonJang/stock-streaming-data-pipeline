import { useState, useMemo, useRef, useEffect } from 'react';
import Link from 'next/link';
import { createPortal } from 'react-dom';
import { ArrowUpDown, ArrowUp, ArrowDown, Star, TrendingUp, TrendingDown } from 'lucide-react';
import { useStockStore } from '@/store/stocks';
import { useUIStore } from '@/store/ui';
import { Table, TableHeader, TableBody, TableHead, TableRow, TableCell, Button, Badge, LoadingTable } from '@/components/ui';
import { formatCurrency, formatPercentage, formatLargeNumber, cn, calculateChange, getStockChangeColor, getRecommendationColor } from '@/lib/utils';
import { COMPANY_NAMES } from '@/lib/constants';
import { SupportedSymbol } from '@/types/api';

interface StockTableProps {
  searchTerm?: string;
}

export default function StockTable({ searchTerm = '' }: StockTableProps) {
  const { 
    recommendations, 
    latestPrices, 
    companyOverviews,
    realTimePrices,
    favorites,
    addToFavorites,
    removeFromFavorites,
    isLoading 
  } = useStockStore();
  
  const { showChangePercent, showVolume } = useUIStore();
  
  const [hoveredCell, setHoveredCell] = useState<string | null>(null);
  const [tooltipPosition, setTooltipPosition] = useState<{x: number, y: number} | null>(null);
  const [mounted, setMounted] = useState(false);
  
  useEffect(() => {
    setMounted(true);
  }, []);
  
  const [sortConfig, setSortConfig] = useState<{
    key: string;
    direction: 'asc' | 'desc';
  }>({
    key: 'recommendationScore',
    direction: 'desc'
  });

  const handleSort = (key: string) => {
    setSortConfig(prev => ({
      key,
      direction: prev.key === key && prev.direction === 'desc' ? 'asc' : 'desc'
    }));
  };

  const sortedData = useMemo(() => {
    if (!recommendations.length) return [];

    const filtered = recommendations.filter(stock => {
      if (!searchTerm) return true;
      return stock.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
             COMPANY_NAMES[stock.symbol as SupportedSymbol]?.toLowerCase().includes(searchTerm.toLowerCase());
    });

    return filtered.sort((a, b) => {
      const { key, direction } = sortConfig;
      let aValue: any = a[key as keyof typeof a];
      let bValue: any = b[key as keyof typeof b];

      // Handle special cases
      if (key === 'price') {
        aValue = realTimePrices[a.symbol]?.price || latestPrices[a.symbol]?.close || 0;
        bValue = realTimePrices[b.symbol]?.price || latestPrices[b.symbol]?.close || 0;
      } else if (key === 'change') {
        const aPrice = realTimePrices[a.symbol]?.price || latestPrices[a.symbol]?.close || 0;
        const aPrevPrice = latestPrices[a.symbol]?.open || 0;
        const bPrice = realTimePrices[b.symbol]?.price || latestPrices[b.symbol]?.close || 0;
        const bPrevPrice = latestPrices[b.symbol]?.open || 0;
        
        aValue = calculateChange(aPrice, aPrevPrice);
        bValue = calculateChange(bPrice, bPrevPrice);
      } else if (key === 'volume') {
        aValue = latestPrices[a.symbol]?.volume || 0;
        bValue = latestPrices[b.symbol]?.volume || 0;
      } else if (key === 'marketCap') {
        aValue = companyOverviews[a.symbol]?.marketCapitalization || 0;
        bValue = companyOverviews[b.symbol]?.marketCapitalization || 0;
      }

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return direction === 'asc' ? aValue.localeCompare(bValue) : bValue.localeCompare(aValue);
      }

      return direction === 'asc' ? aValue - bValue : bValue - aValue;
    });
  }, [recommendations, searchTerm, sortConfig, realTimePrices, latestPrices, companyOverviews]);

  const getSortIcon = (key: string) => {
    if (sortConfig.key !== key) {
      return <ArrowUpDown className="h-4 w-4" />;
    }
    return sortConfig.direction === 'asc' 
      ? <ArrowUp className="h-4 w-4" />
      : <ArrowDown className="h-4 w-4" />;
  };

  const toggleFavorite = (symbol: SupportedSymbol) => {
    if (favorites.includes(symbol)) {
      removeFromFavorites(symbol);
    } else {
      addToFavorites(symbol);
    }
  };
  
  const handleMouseEnter = (event: React.MouseEvent, symbol: string) => {
    const rect = event.currentTarget.getBoundingClientRect();
    setTooltipPosition({
      x: rect.left + rect.width / 2,
      y: rect.top - 10
    });
    setHoveredCell(symbol);
  };
  
  const handleMouseLeave = () => {
    setHoveredCell(null);
    setTooltipPosition(null);
  };
  
  const TooltipPortal = ({ children }: { children: React.ReactNode }) => {
    if (!mounted) return null;
    return createPortal(children, document.body);
  };

  if (isLoading) {
    return <LoadingTable rows={20} columns={showVolume ? 7 : 6} />;
  }

  return (
    <div className="relative">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-12">
              <Star className="h-4 w-4" />
            </TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('symbol')}
                className="font-medium"
              >
                Symbol
                {getSortIcon('symbol')}
              </Button>
            </TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('price')}
                className="font-medium"
              >
                Price
                {getSortIcon('price')}
              </Button>
            </TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('change')}
                className="font-medium"
              >
                Change
                {getSortIcon('change')}
              </Button>
            </TableHead>
            {showVolume && (
              <TableHead>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => handleSort('volume')}
                  className="font-medium"
                >
                  Volume
                  {getSortIcon('volume')}
                </Button>
              </TableHead>
            )}
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('marketCap')}
                className="font-medium"
              >
                Market Cap
                {getSortIcon('marketCap')}
              </Button>
            </TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('recommendationScore')}
                className="font-medium"
              >
                Recommendation
                {getSortIcon('recommendationScore')}
              </Button>
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {sortedData.map((stock) => {
            const symbol = stock.symbol as SupportedSymbol;
            const currentPrice = realTimePrices[symbol]?.price || latestPrices[symbol]?.close || 0;
            const previousPrice = latestPrices[symbol]?.open || 0;
            const change = calculateChange(currentPrice, previousPrice);
            const volume = latestPrices[symbol]?.volume || 0;
            const marketCap = companyOverviews[symbol]?.marketCapitalization || 0;
            const isRealTime = !!realTimePrices[symbol];
            const isFavorite = favorites.includes(symbol);

            return (
              <TableRow key={symbol} className="hover:bg-gray-50 dark:hover:bg-dark-800 cursor-pointer" onClick={() => window.location.href = `/stocks/${symbol}`}>
                <TableCell>
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
                </TableCell>
                <TableCell>
                  <div className="flex items-center space-x-2">
                    <div>
                      <div className="font-medium">{symbol}</div>
                      <div className="text-sm text-gray-500 dark:text-gray-400">
                        {COMPANY_NAMES[symbol] || 'Unknown Company'}
                      </div>
                    </div>
                    {isRealTime && (
                      <div className="h-2 w-2 bg-green-500 rounded-full animate-pulse" />
                    )}
                  </div>
                </TableCell>
                <TableCell>
                  <span className="font-medium">
                    {formatCurrency(currentPrice)}
                  </span>
                </TableCell>
                <TableCell>
                  <div className={cn('flex items-center space-x-1', getStockChangeColor(change))}>
                    {change > 0 ? (
                      <TrendingUp className="h-4 w-4" />
                    ) : change < 0 ? (
                      <TrendingDown className="h-4 w-4" />
                    ) : (
                      <div className="h-4 w-4" />
                    )}
                    <span className="font-medium">
                      {showChangePercent ? formatPercentage(change) : formatCurrency(currentPrice - previousPrice)}
                    </span>
                  </div>
                </TableCell>
                {showVolume && (
                  <TableCell>
                    <span className="text-sm">
                      {formatLargeNumber(volume)}
                    </span>
                  </TableCell>
                )}
                <TableCell>
                  <span className="text-sm">
                    {formatLargeNumber(marketCap)}
                  </span>
                </TableCell>
                <TableCell>
                  <div className="flex items-center space-x-2">
                    <div className="relative">
                      <Badge 
                        variant={stock.recommendationScore >= 0.8 ? 'success' : 
                                stock.recommendationScore >= 0.6 ? 'info' :
                                stock.recommendationScore >= 0.4 ? 'warning' : 'danger'}
                        size="sm"
                        className="cursor-help"
                        onMouseEnter={(e) => handleMouseEnter(e, symbol)}
                        onMouseLeave={handleMouseLeave}
                      >
                        {stock.recommendationLabel}
                      </Badge>
                    </div>
                    <span className={cn('text-sm font-medium', getRecommendationColor(stock.recommendationScore))}>
                      {stock.recommendationScore.toFixed(3)}
                    </span>
                  </div>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
      
      {hoveredCell && tooltipPosition && (
        <TooltipPortal>
          <div 
            className="fixed px-3 py-2 bg-gray-900 text-white text-xs rounded-lg pointer-events-none whitespace-nowrap shadow-lg"
            style={{
              left: tooltipPosition.x,
              top: tooltipPosition.y,
              transform: 'translate(-50%, -100%)',
              zIndex: 10000
            }}
          >
            Recommendation Scale:<br/>
            Strong Buy (8.000 – 10.000): Indicates a strong expectation of upward movement.<br/>
            Buy (6.500 – 7.999): Suggests favorable conditions for buying.<br/>
            Hold (4.500 – 6.499): Advises holding the stock with no strong bias.<br/>
            Sell (3.000 – 4.499): Recommends selling due to weaker outlook.<br/>
            Strong Sell (0.0000 – 2.999): Strongly advises selling to avoid potential losses.
          </div>
        </TooltipPortal>
      )}
    </div>
  );
}