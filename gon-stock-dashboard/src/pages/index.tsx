import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Head from 'next/head';
import { RefreshCw, Settings } from 'lucide-react';
import { useStockStore } from '@/store/stocks';
import { useUIStore } from '@/store/ui';
import Layout from '@/components/layout/Layout';
import TopGainersLosers from '@/components/dashboard/TopGainersLosers';
import StockRecommendations from '@/components/dashboard/StockRecommendations';
import TopSellRecommendations from '@/components/dashboard/TopSellRecommendations';
import StockTable from '@/components/dashboard/StockTable';
import { Card, CardHeader, CardTitle, CardContent, Button, Select, Switch } from '@/components/ui';

const Dashboard: NextPage = () => {
  const [showFilters, setShowFilters] = useState(false);
  
  const { 
    fetchDashboardData, 
    isLoading,
    news
  } = useStockStore();

  
  const { 
    autoRefresh, 
    refreshInterval, 
    showChangePercent,
    showVolume,
    setAutoRefresh,
    setRefreshInterval,
    setShowChangePercent,
    setShowVolume,
    addNotification
  } = useUIStore();

  // Initial data fetch
  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  // Auto-refresh functionality with fixed 30 second interval
  useEffect(() => {
    if (!autoRefresh) return;

    const interval = setInterval(() => {
      fetchDashboardData();
    }, 30000); // Fixed 30 seconds

    return () => clearInterval(interval);
  }, [autoRefresh, fetchDashboardData]);

  // Handle manual refresh
  const handleRefresh = async () => {
    try {
      await fetchDashboardData();
      addNotification({
        type: 'success',
        title: 'Data Refreshed',
        message: 'Stock data has been updated successfully',
      });
    } catch (error) {
      addNotification({
        type: 'error',
        title: 'Refresh Failed',
        message: 'Failed to refresh stock data',
      });
    }
  };


  return (
    <>
      <Head>
        <title>Gon Stock Dashboard - Real-time Stock Market Data</title>
        <meta name="description" content="Real-time stock market data and investment insights for NASDAQ technology stocks" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <Layout>
          <div className="space-y-6">
            {/* Header Section */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
              <div>
                <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                  Dashboard
                </h1>
                <p className="text-gray-600 dark:text-gray-400 mt-1">
                  Real-time insights for 20 top NASDAQ technology stocks
                </p>
              </div>
              
              <div className="flex items-center space-x-2">
                <Button
                  variant="outline"
                  onClick={handleRefresh}
                  disabled={isLoading}
                  className="flex items-center space-x-2"
                >
                  <RefreshCw className={`h-4 w-4 ${isLoading ? 'animate-spin' : ''}`} />
                  <span className="hidden sm:inline">Refresh</span>
                </Button>
                
                <Button
                  variant="outline"
                  onClick={() => setShowFilters(!showFilters)}
                  className="flex items-center space-x-2"
                >
                  <Settings className="h-4 w-4" />
                  <span className="hidden sm:inline">Settings</span>
                </Button>
              </div>
            </div>

            {/* Settings Panel */}
            {showFilters && (
              <Card>
                <CardHeader>
                  <CardTitle>Dashboard Settings</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                    <Switch
                      checked={autoRefresh}
                      onCheckedChange={setAutoRefresh}
                      label="Auto Refresh"
                      description="Automatically refresh data"
                    />
                    
                    <div className="flex flex-col space-y-2">
                      <label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                        Refresh Interval
                      </label>
                      <div className="text-sm text-gray-500 dark:text-gray-400">
                        Fixed at 30 seconds
                      </div>
                    </div>
                    
                    <Switch
                      checked={showChangePercent}
                      onCheckedChange={setShowChangePercent}
                      label="Show Change %"
                      description="Display percentage change"
                    />
                    
                    <Switch
                      checked={showVolume}
                      onCheckedChange={setShowVolume}
                      label="Show Volume"
                      description="Display trading volume"
                    />
                  </div>
                </CardContent>
              </Card>
            )}

            {/* Top Gainers and Losers */}
            <TopGainersLosers />

            {/* Stock Recommendations */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <StockRecommendations />
              <TopSellRecommendations />
            </div>

            {/* Stock Table */}
            <Card className="overflow-visible">
              <CardHeader>
                <CardTitle>All Stocks</CardTitle>
              </CardHeader>
              <CardContent className="pt-6 overflow-visible">
                <StockTable />
              </CardContent>
            </Card>

            {/* Latest News */}
            <Card>
              <CardHeader>
                <CardTitle>Latest News</CardTitle>
              </CardHeader>
              <CardContent className="pt-6">
                <div className="space-y-6">
                  {(() => {
                    const newsItems = news && Object.keys(news).length > 0 ? 
                      Object.values(news)
                        .filter((newsData: any) => newsData?.content && Array.isArray(newsData.content))
                        .map((newsData: any) => newsData.content)
                        .flat()
                        .slice(0, 5) : [];
                    
                    return newsItems.length > 0 ? (
                      newsItems.map((item: any) => (
                      <div key={item.id} className="flex items-start space-x-3 p-4 rounded-lg border border-gray-200 dark:border-gray-700">
                        <div className="flex-1">
                          <h4 className="font-medium text-gray-900 dark:text-white line-clamp-2 mb-2">
                            {item.newsArticle?.title || item.headline || 'No title'}
                          </h4>
                          <p className="text-sm text-gray-600 dark:text-gray-400 mb-3 line-clamp-2">
                            {item.newsArticle?.summary || item.summary || 'No summary'}
                          </p>
                          <div className="flex items-center space-x-4 text-xs text-gray-500 dark:text-gray-400">
                            <span>{item.newsArticle?.source || item.source}</span>
                            <span>{item.newsArticle?.timePublished ? new Date(item.newsArticle.timePublished).toLocaleString() : 
                                    item.datetime ? new Date(item.datetime * 1000).toLocaleString() : 'No date'}</span>
                          </div>
                        </div>
                        <a
                          href={item.newsArticle?.url || item.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300"
                        >
                          <span className="sr-only">Read more</span>
                          →
                        </a>
                      </div>
                    ))
                    ) : (
                      <div className="text-center py-8 text-gray-500 dark:text-gray-400">
                        <p>No news available at the moment.</p>
                      </div>
                    );
                  })()}
                </div>
              </CardContent>
            </Card>

          </div>
        </Layout>
    </>
  );
};

export default Dashboard;