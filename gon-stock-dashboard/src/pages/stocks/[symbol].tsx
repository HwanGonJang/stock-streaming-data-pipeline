import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';
import { ArrowLeft, ExternalLink, TrendingUp, DollarSign, Calendar, Building } from 'lucide-react';
import { useStockStore } from '@/store/stocks';
import { useRealTimeStock } from '@/hooks/useRealTimeStock';
import Layout from '@/components/layout/Layout';
import StockChart from '@/components/charts/StockChart';
import { Card, CardHeader, CardTitle, CardContent, Button, Badge, LoadingCard, LoadingSkeleton } from '@/components/ui';
import { formatCurrency, formatPercentage, formatLargeNumber, cn, calculateChange, getStockChangeColor, getRecommendationColor } from '@/lib/utils';
import { COMPANY_NAMES, FINANCIAL_METRICS } from '@/lib/constants';
import { SupportedSymbol } from '@/types/api';
import { isValidSymbol } from '@/lib/utils';

const StockDetail: NextPage = () => {
  const router = useRouter();
  const { symbol } = router.query;
  const [activeTab, setActiveTab] = useState<'overview' | 'financials' | 'news'>('overview');
  const [useKoreanTimeSimulation, setUseKoreanTimeSimulation] = useState(false);
  
  const {
    stockInfo,
    stockOverview,
    stockRecommendation,
    dailyPrices,
    stockNews,
    realTimePrices,
    fetchStockDetail,
    error,
  } = useStockStore();

  const symbolStr = symbol as string;
  const isValidStockSymbol = symbolStr && isValidSymbol(symbolStr);
  const stockSymbol = symbolStr?.toUpperCase() as SupportedSymbol;

  // Real-time data connection
  const realTimeConnection = useRealTimeStock(stockSymbol, useKoreanTimeSimulation);

  // Fetch stock detail data
  useEffect(() => {
    if (isValidStockSymbol) {
      fetchStockDetail(stockSymbol);
    }
  }, [stockSymbol, isValidStockSymbol, fetchStockDetail]);

  // Handle invalid symbol
  if (!isValidStockSymbol) {
    return (
      <Layout>
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Invalid Stock Symbol
          </h1>
          <p className="text-gray-600 dark:text-gray-400 mb-8">
            The stock symbol &quot;{symbol}&quot; is not supported.
          </p>
          <Link href="/">
            <Button>
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Dashboard
            </Button>
          </Link>
        </div>
      </Layout>
    );
  }

  const currentPrice = realTimePrices[stockSymbol]?.price || dailyPrices[0]?.open || 0;
  const previousPrice = dailyPrices[0]?.open || 0;
  const change = calculateChange(currentPrice, previousPrice);

  const tabs = [
    { id: 'overview', label: 'Overview', icon: TrendingUp },
    { id: 'financials', label: 'Financials', icon: DollarSign },
    { id: 'news', label: 'News', icon: Calendar },
  ];

  return (
    <>
      <Head>
        <title>{stockSymbol} - Stock Detail | Gon Stock Dashboard</title>
        <meta name="description" content={`Real-time stock data and analysis for ${stockSymbol} - ${COMPANY_NAMES[stockSymbol]}`} />
      </Head>

      <Layout>
          <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <Link href="/">
                  <Button variant="outline" size="sm">
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    Back
                  </Button>
                </Link>
                
                <div className="flex items-center space-x-3">
                  <div>
                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                      {stockSymbol}
                    </h1>
                    <p className="text-gray-600 dark:text-gray-400">
                      {COMPANY_NAMES[stockSymbol] || 'Loading...'}
                    </p>
                  </div>
                  {realTimeConnection.isConnected && (
                    <div className="h-3 w-3 bg-green-500 rounded-full animate-pulse" />
                  )}
                </div>
              </div>
              
              <div className="flex items-center space-x-2">
                <label className="flex items-center space-x-2 text-sm">
                  <input
                    type="checkbox"
                    checked={useKoreanTimeSimulation}
                    onChange={(e) => setUseKoreanTimeSimulation(e.target.checked)}
                    className="w-4 h-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                  />
                  <span className="text-gray-700 dark:text-gray-300">Korean Time Simulation</span>
                </label>
              </div>
            </div>

            {/* Price Header */}
            <Card>
              <CardContent className="p-6">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                  <div className="md:col-span-2">
                    <div className="flex items-baseline space-x-3">
                      <span className="text-4xl font-bold text-gray-900 dark:text-white">
                        {formatCurrency(currentPrice)}
                      </span>
                      <div className={cn('flex items-center space-x-1', getStockChangeColor(change))}>
                        <span className="text-lg font-semibold">
                          {change >= 0 ? '+' : ''}{formatCurrency(currentPrice - previousPrice)}
                        </span>
                        <span className="text-sm">
                          ({formatPercentage(change)})
                        </span>
                      </div>
                    </div>
                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
                      Last updated: {realTimePrices[stockSymbol]?.tradeTimestamp ? 
                        (() => {
                          const utcDate = new Date(realTimePrices[stockSymbol].tradeTimestamp);
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
                        })() : 
                        'Loading...'}
                    </p>
                  </div>
                  
                  <div className="space-y-3">
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">Market Cap</p>
                      <p className="font-semibold">
                        {stockOverview ? formatLargeNumber(stockOverview.marketCapitalization) : 'Loading...'}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">P/E Ratio</p>
                      <p className="font-semibold">
                        {stockOverview ? stockOverview.peRatio.toFixed(2) : 'Loading...'}
                      </p>
                    </div>
                  </div>
                  
                  <div className="space-y-3">
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">52W Range</p>
                      <p className="font-semibold">
                        {stockOverview ? 
                          `${formatCurrency(stockOverview.fiftyTwoWeekLow)} - ${formatCurrency(stockOverview.fiftyTwoWeekHigh)}` : 
                          'Loading...'}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">Recommendation</p>
                      <div className="flex items-center space-x-2">
                        {stockRecommendation ? (
                          <>
                            <div className="relative group">
                              <Badge 
                                variant={stockRecommendation.recommendationScore >= 0.8 ? 'success' : 
                                        stockRecommendation.recommendationScore >= 0.6 ? 'info' :
                                        stockRecommendation.recommendationScore >= 0.4 ? 'warning' : 'danger'}
                                size="sm"
                                className="cursor-help"
                              >
                                {stockRecommendation.recommendationLabel}
                              </Badge>
                              <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10">
                                Recommendation Scale:<br/>
                                Strong Buy (8.000 – 10.000): Indicates a strong expectation of upward movement.<br/>
                                Buy (6.500 – 7.999): Suggests favorable conditions for buying.<br/>
                                Hold (4.500 – 6.499): Advises holding the stock with no strong bias.<br/>
                                Sell (3.000 – 4.499): Recommends selling due to weaker outlook.<br/>
                                Strong Sell (0.0000 – 2.999): Strongly advises selling to avoid potential losses.
                              </div>
                            </div>
                            <span className={cn('text-sm font-medium', getRecommendationColor(stockRecommendation.recommendationScore))}>
                              {stockRecommendation.recommendationScore.toFixed(2)}
                            </span>
                          </>
                        ) : (
                          <LoadingSkeleton rows={1} />
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Chart */}
            <StockChart data={dailyPrices} symbol={stockSymbol} />

            {/* Tabs */}
            <div className="border-b border-gray-200 dark:border-dark-600">
              <nav className="-mb-px flex space-x-8">
                {tabs.map((tab) => (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id as any)}
                    className={cn(
                      'flex items-center space-x-2 py-2 px-1 border-b-2 font-medium text-sm',
                      activeTab === tab.id
                        ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'
                    )}
                  >
                    <tab.icon className="h-4 w-4" />
                    <span>{tab.label}</span>
                  </button>
                ))}
              </nav>
            </div>

            {/* Tab Content */}
            <div className="space-y-6">
              {activeTab === 'overview' && (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  <Card>
                    <CardHeader>
                      <CardTitle className="flex items-center space-x-2">
                        <Building className="h-5 w-5 text-primary-600" />
                        <span>Company Overview</span>
                      </CardTitle>
                    </CardHeader>
                    <CardContent className="pt-6">
                      {stockOverview ? (
                        <div className="space-y-4">
                          <p className="text-sm text-gray-600 dark:text-gray-400">
                            {stockOverview.description}
                          </p>
                          <div className="grid grid-cols-2 gap-4">
                            <div>
                              <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Sector</p>
                              <p className="text-sm">{stockOverview.sector}</p>
                            </div>
                            <div>
                              <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Industry</p>
                              <p className="text-sm">{stockOverview.industry}</p>
                            </div>
                            <div>
                              <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Country</p>
                              <p className="text-sm">{stockOverview.country}</p>
                            </div>
                            <div>
                              <p className="text-sm font-medium text-gray-500 dark:text-gray-400">Exchange</p>
                              <p className="text-sm">{stockInfo?.exchange || 'NASDAQ'}</p>
                            </div>
                          </div>
                        </div>
                      ) : (
                        <LoadingCard />
                      )}
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader>
                      <CardTitle>Investment Recommendation by LLM</CardTitle>
                    </CardHeader>
                    <CardContent className="pt-6">
                      {stockRecommendation ? (
                        <div className="space-y-4">
                          <div className="flex items-center space-x-3">
                            <div className="relative group">
                              <Badge 
                                variant={stockRecommendation.recommendationScore >= 0.8 ? 'success' : 
                                        stockRecommendation.recommendationScore >= 0.6 ? 'info' :
                                        stockRecommendation.recommendationScore >= 0.4 ? 'warning' : 'danger'}
                                size="lg"
                                className="cursor-help"
                              >
                                {stockRecommendation.recommendationLabel}
                              </Badge>
                              <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10">
                                Recommendation Scale:<br/>
                                Strong Buy (8.000 – 10.000): Indicates a strong expectation of upward movement.<br/>
                                Buy (6.500 – 7.999): Suggests favorable conditions for buying.<br/>
                                Hold (4.500 – 6.499): Advises holding the stock with no strong bias.<br/>
                                Sell (3.000 – 4.499): Recommends selling due to weaker outlook.<br/>
                                Strong Sell (0.0000 – 2.999): Strongly advises selling to avoid potential losses.
                              </div>
                            </div>
                            <span className={cn('text-2xl font-bold', getRecommendationColor(stockRecommendation.recommendationScore))}>
                              {stockRecommendation.recommendationScore.toFixed(2)}
                            </span>
                          </div>
                          <div className="text-sm text-gray-600 dark:text-gray-400 whitespace-pre-wrap">
                            {stockRecommendation.summary}
                          </div>
                          <div className="text-xs text-gray-500 dark:text-gray-400">
                            Last updated: {(() => {
                              const utcDate = new Date(stockRecommendation.lastUpdatedAt);
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
                            })()}
                          </div>
                        </div>
                      ) : (
                        <LoadingCard />
                      )}
                    </CardContent>
                  </Card>
                </div>
              )}

              {activeTab === 'financials' && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {Object.entries(FINANCIAL_METRICS).map(([category, metrics]) => (
                    <Card key={category}>
                      <CardHeader>
                        <CardTitle className="capitalize">{category}</CardTitle>
                      </CardHeader>
                      <CardContent className="pt-6">
                        <div className="space-y-3">
                          {metrics.map((metric) => (
                            <div key={metric.key} className="flex justify-between items-center">
                              <span className="text-sm text-gray-600 dark:text-gray-400">{metric.label}</span>
                              <span className="font-medium">
                                {stockOverview ? (
                                  metric.format === 'percentage' ? 
                                    formatPercentage((stockOverview as any)[metric.key] * 100) :
                                  metric.format === 'currency' ?
                                    formatCurrency((stockOverview as any)[metric.key]) :
                                    (stockOverview as any)[metric.key]?.toFixed(2) || 'N/A'
                                ) : (
                                  'Loading...'
                                )}
                              </span>
                            </div>
                          ))}
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}

              {activeTab === 'news' && (
                <div className="space-y-4">
                  {stockNews?.length ? (
                    stockNews.map((news) => (
                      <Card key={news.id}>
                        <CardContent className="p-6 pt-6">
                          <div className="flex items-start space-x-4">
                            <div className="flex-1">
                              <h3 className="font-semibold text-lg mb-2">
                                <a 
                                  href={news.newsArticle.url}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  className="hover:text-primary-600 dark:hover:text-primary-400"
                                >
                                  {news.newsArticle.title}
                                </a>
                              </h3>
                              <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                                {news.newsArticle.summary}
                              </p>
                              <div className="flex items-center space-x-4 text-sm text-gray-500 dark:text-gray-400">
                                <span>{news.newsArticle.source}</span>
                                <span>{new Date(news.newsArticle.timePublished).toLocaleDateString()}</span>
                                <Badge 
                                  variant={news.sentimentLabel === 'Positive' ? 'success' : 
                                          news.sentimentLabel === 'Negative' ? 'danger' : 'default'}
                                  size="sm"
                                >
                                  {news.sentimentLabel}
                                </Badge>
                              </div>
                            </div>
                            <a 
                              href={news.newsArticle.url}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="inline-flex items-center justify-center rounded-lg p-2 text-sm font-medium transition-colors hover:bg-gray-100 dark:hover:bg-dark-800"
                            >
                              <ExternalLink className="h-4 w-4" />
                            </a>
                          </div>
                        </CardContent>
                      </Card>
                    ))
                  ) : (
                    <Card>
                      <CardContent className="p-8 pt-8 text-center">
                        <Calendar className="h-12 w-12 mx-auto mb-4 text-gray-400" />
                        <p className="text-gray-500 dark:text-gray-400">
                          No news available for {stockSymbol}
                        </p>
                      </CardContent>
                    </Card>
                  )}
                </div>
              )}
            </div>

            {/* Error Display */}
            {error && (
              <Card className="border-red-200 bg-red-50 dark:border-red-800 dark:bg-red-900/10">
                <CardContent className="p-4">
                  <div className="flex items-center space-x-2">
                    <div className="h-2 w-2 bg-red-500 rounded-full" />
                    <span className="text-red-700 dark:text-red-400 font-medium">
                      Error: {error}
                    </span>
                  </div>
                </CardContent>
              </Card>
            )}
          </div>
        </Layout>
    </>
  );
};

export default StockDetail;