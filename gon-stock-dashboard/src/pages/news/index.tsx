import { useEffect, useState } from 'react';
import Head from 'next/head';
import { useStockStore } from '@/store/stocks';
import { Card, Badge } from '@/components/ui';
import { ExternalLink, Calendar, Clock } from 'lucide-react';
import { SUPPORTED_SYMBOLS } from '@/lib/constants';
import { cn } from '@/lib/utils';
import Layout from '@/components/layout/Layout';

interface NewsItem {
  id: string;
  newsArticle: {
    id: number;
    title: string;
    url: string;
    timePublished: string;
    summary: string;
    bannerImage?: string;
    source: string;
  };
  symbol: string;
  relevanceScore: number;
  sentimentScore: number;
  sentimentLabel: string;
}

export default function NewsPage() {
  const { news, allNews } = useStockStore();
  const [selectedSymbol, setSelectedSymbol] = useState<string>('all');
  const [filteredNews, setFilteredNews] = useState<NewsItem[]>([]);

  useEffect(() => {
    if (selectedSymbol === 'all') {
      // Show all news from allNews (direct news articles)
      if (allNews && Array.isArray(allNews)) {
        const sortedNews = [...allNews].sort((a, b) => 
          new Date(b.timePublished).getTime() - new Date(a.timePublished).getTime()
        );
        // Convert to NewsItem format for compatibility
        const convertedNews: NewsItem[] = sortedNews.map(item => ({
          id: item.id.toString(),
          newsArticle: {
            id: item.id,
            title: item.title,
            url: item.url,
            timePublished: item.timePublished,
            summary: item.summary,
            source: item.source,
            bannerImage: item.bannerImage
          },
          symbol: 'ALL',
          relevanceScore: 0,
          sentimentScore: item.overallSentimentScore || 0,
          sentimentLabel: item.overallSentimentLabel || 'Neutral'
        }));
        setFilteredNews(convertedNews);
      } else {
        setFilteredNews([]);
      }
    } else {
      // Show news for specific symbol
      if (!news || !news[selectedSymbol]) {
        setFilteredNews([]);
        return;
      }
      
      const symbolNewsData = news[selectedSymbol];
      
      if (!symbolNewsData?.content || !Array.isArray(symbolNewsData.content)) {
        setFilteredNews([]);
        return;
      }
      
      const sortedNews = [...symbolNewsData.content].sort((a, b) => 
        new Date(b.newsArticle.timePublished).getTime() - new Date(a.newsArticle.timePublished).getTime()
      );
      setFilteredNews(sortedNews);
    }
  }, [news, allNews, selectedSymbol]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  const getTimeAgo = (dateString: string) => {
    const now = Date.now();
    const diff = now - new Date(dateString).getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days}d ago`;
    if (hours > 0) return `${hours}h ago`;
    if (minutes > 0) return `${minutes}m ago`;
    return 'Just now';
  };

  const getCategoryColor = (category: string) => {
    switch (category.toLowerCase()) {
      case 'earnings':
        return 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400';
      case 'merger':
        return 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400';
      case 'acquisition':
        return 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400';
      case 'press release':
        return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400';
      default:
        return 'bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400';
    }
  };

  return (
    <>
      <Head>
        <title>Market News - Gon Stock Dashboard</title>
        <meta name="description" content="Latest market news and updates for all supported stocks" />
      </Head>

      <Layout>
        <div className="space-y-6">
          {/* Header Section */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                Market News
              </h1>
              <p className="text-gray-600 dark:text-gray-400 mt-1">
                Latest market news and updates for all supported stocks - {filteredNews.length} articles
              </p>
            </div>
          </div>

          {/* Filter tabs */}
          <div className="flex space-x-2 overflow-x-auto pb-2">
            <button
              onClick={() => setSelectedSymbol('all')}
              className={cn(
                'px-4 py-2 rounded-lg text-sm font-medium whitespace-nowrap transition-colors',
                selectedSymbol === 'all'
                  ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/20 dark:text-primary-400'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200 dark:bg-dark-800 dark:text-gray-400 dark:hover:bg-dark-700'
              )}
            >
              All News
            </button>
            {SUPPORTED_SYMBOLS.map((symbol) => (
              <button
                key={symbol}
                onClick={() => setSelectedSymbol(symbol)}
                className={cn(
                  'px-4 py-2 rounded-lg text-sm font-medium whitespace-nowrap transition-colors',
                  selectedSymbol === symbol
                    ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/20 dark:text-primary-400'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200 dark:bg-dark-800 dark:text-gray-400 dark:hover:bg-dark-700'
                )}
              >
                {symbol}
              </button>
            ))}
          </div>

          {/* News list */}
          <div className="space-y-4">
            {filteredNews.length === 0 ? (
              <Card className="p-8 text-center">
                <div className="text-gray-500 dark:text-gray-400">
                  No news available for{' '}
                  {selectedSymbol === 'all' ? 'any symbols' : selectedSymbol}
                </div>
              </Card>
            ) : (
              filteredNews.map((item) => (
                <Card key={item.id} className="p-6 hover:shadow-md transition-shadow">
                  <div className="flex items-start space-x-4">
                    {item.newsArticle.bannerImage && (
                      <img
                        src={item.newsArticle.bannerImage}
                        alt={item.newsArticle.title}
                        className="w-16 h-16 object-cover rounded-lg flex-shrink-0"
                      />
                    )}
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center space-x-2 mb-2">
                        <Badge 
                          variant={item.sentimentLabel === 'Positive' ? 'success' : 
                                  item.sentimentLabel === 'Negative' ? 'danger' : 'default'}
                          size="sm"
                        >
                          {item.sentimentLabel}
                        </Badge>
                        <Badge variant="outline" className="text-xs">
                          {item.symbol}
                        </Badge>
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          {getTimeAgo(item.newsArticle.timePublished)}
                        </span>
                      </div>
                      
                      <h3 className="font-semibold text-lg text-gray-900 dark:text-white mb-2 line-clamp-2">
                        {item.newsArticle.title}
                      </h3>
                      
                      <p className="text-gray-600 dark:text-gray-300 mb-3 line-clamp-3">
                        {item.newsArticle.summary}
                      </p>
                      
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-4 text-sm text-gray-500 dark:text-gray-400">
                          <div className="flex items-center space-x-1">
                            <Calendar className="h-4 w-4" />
                            <span>{formatDate(item.newsArticle.timePublished)}</span>
                          </div>
                          <div className="flex items-center space-x-1">
                            <Clock className="h-4 w-4" />
                            <span>{formatTime(item.newsArticle.timePublished)}</span>
                          </div>
                          <span className="font-medium">{item.newsArticle.source}</span>
                        </div>
                        
                        <a
                          href={item.newsArticle.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="flex items-center space-x-1 text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 text-sm font-medium"
                        >
                          <span>Read more</span>
                          <ExternalLink className="h-4 w-4" />
                        </a>
                      </div>
                    </div>
                  </div>
                </Card>
              ))
          )}
          </div>
        </div>
      </Layout>
    </>
  );
}