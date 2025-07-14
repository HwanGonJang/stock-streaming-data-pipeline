import { NextPage } from 'next';
import Head from 'next/head';
import { 
  Info, 
  TrendingUp, 
  BarChart3, 
  Clock, 
  Database, 
  Globe, 
  Sparkles,
  Users,
  BookOpen,
  Activity
} from 'lucide-react';
import Layout from '@/components/layout/Layout';
import { Card, CardHeader, CardTitle, CardContent, Badge } from '@/components/ui';

const About: NextPage = () => {
  return (
    <>
      <Head>
        <title>About - Gon Stock Dashboard</title>
        <meta name="description" content="Learn about Gon Stock Dashboard features and how to use the platform" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <Layout>
        <div className="space-y-8">
          {/* Header Section */}
          <div className="text-center mb-12">
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-4">
              About Gon Stock Dashboard
            </h1>
            <p className="text-xl text-gray-600 dark:text-gray-400">
              Real-time stock market data and investment insights
            </p>
          </div>

          {/* Overview */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="flex items-center space-x-2 text-xl">
                <Info className="h-6 w-6" />
                <span>Platform Overview</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="prose dark:prose-invert max-w-none">
                <p className="text-gray-700 dark:text-gray-300 leading-relaxed text-lg">
                  Gon Stock Dashboard is a comprehensive investment analysis platform that provides real-time stock market data. 
                  It offers real-time prices, recommendation information, and news for 20 major NASDAQ technology stocks in one place.
                </p>
              </div>
            </CardContent>
          </Card>

          {/* Key Features */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="flex items-center space-x-2 text-xl">
                <Sparkles className="h-6 w-6" />
                <span>Key Features</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <div className="flex items-start space-x-3">
                  <div className="bg-blue-100 dark:bg-blue-900/20 p-2 rounded-lg">
                    <TrendingUp className="h-5 w-5 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white mb-2">Real-time Stock Tracking</h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                      Live stock prices and change rates updated in real-time
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-3">
                  <div className="bg-green-100 dark:bg-green-900/20 p-2 rounded-lg">
                    <BarChart3 className="h-5 w-5 text-green-600 dark:text-green-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white mb-2">Investment Recommendation System</h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                      AI-based buy/sell recommendations and investment scores
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-3">
                  <div className="bg-purple-100 dark:bg-purple-900/20 p-2 rounded-lg">
                    <Globe className="h-5 w-5 text-purple-600 dark:text-purple-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white mb-2">Real-time News</h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                      Latest stock-related news and market trends
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-3">
                  <div className="bg-orange-100 dark:bg-orange-900/20 p-2 rounded-lg">
                    <Activity className="h-5 w-5 text-orange-600 dark:text-orange-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white mb-2">Interactive Charts</h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                      Charts displaying price and volume simultaneously
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-3">
                  <div className="bg-red-100 dark:bg-red-900/20 p-2 rounded-lg">
                    <Clock className="h-5 w-5 text-red-600 dark:text-red-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white mb-2">Auto Refresh</h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                      Automatic data updates every 30 seconds
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-3">
                  <div className="bg-indigo-100 dark:bg-indigo-900/20 p-2 rounded-lg">
                    <Users className="h-5 w-5 text-indigo-600 dark:text-indigo-400" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white mb-2">Favorites Management</h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                      Manage your favorite stocks with bookmarking
                    </p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Tracked Stocks */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="flex items-center space-x-2 text-xl">
                <Database className="h-6 w-6" />
                <span>Tracked Stocks (20)</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-3">
                {[
                  'AAPL', 'MSFT', 'GOOGL', 'AMZN', 'TSLA',
                  'META', 'NFLX', 'NVDA', 'AMD', 'INTC',
                  'ORCL', 'CRM', 'ADBE', 'PYPL', 'SHOP',
                  'SPOT', 'ZOOM', 'UBER', 'LYFT', 'DOCU'
                ].map((symbol) => (
                  <Badge key={symbol} variant="outline" className="justify-center py-2">
                    {symbol}
                  </Badge>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* How to Use */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="flex items-center space-x-2 text-xl">
                <BookOpen className="h-6 w-6" />
                <span>User Guide</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="space-y-8">
                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white mb-4">1. Using the Dashboard</h3>
                  <ul className="space-y-3 text-gray-700 dark:text-gray-300">
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-blue-600 dark:text-blue-400 mt-1">•</span>
                      <span className="leading-relaxed">Check overall market trends and top gainers/losers on the main dashboard</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-blue-600 dark:text-blue-400 mt-1">•</span>
                      <span className="leading-relaxed">View buy/sell recommended stocks in the investment recommendation section</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-blue-600 dark:text-blue-400 mt-1">•</span>
                      <span className="leading-relaxed">Adjust auto-refresh and display options through the settings button</span>
                    </li>
                  </ul>
                </div>

                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white mb-4">2. Individual Stock Analysis</h3>
                  <ul className="space-y-3 text-gray-700 dark:text-gray-300">
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-green-600 dark:text-green-400 mt-1">•</span>
                      <span className="leading-relaxed">Click on any stock in the table to navigate to its detailed page</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-green-600 dark:text-green-400 mt-1">•</span>
                      <span className="leading-relaxed">View price charts and volume charts simultaneously</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-green-600 dark:text-green-400 mt-1">•</span>
                      <span className="leading-relaxed">Click the star icon to add/remove from favorites</span>
                    </li>
                  </ul>
                </div>

                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white mb-4">3. Understanding the Recommendation System</h3>
                  <ul className="space-y-3 text-gray-700 dark:text-gray-300">
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-purple-600 dark:text-purple-400 mt-1">•</span>
                      <span className="leading-relaxed"><strong>Strong Buy (0.8-1.0):</strong> Strong upward expectation, actively recommend buying</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-purple-600 dark:text-purple-400 mt-1">•</span>
                      <span className="leading-relaxed"><strong>Buy (0.6-0.8):</strong> Upward expectation, recommend buying</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-purple-600 dark:text-purple-400 mt-1">•</span>
                      <span className="leading-relaxed"><strong>Hold (0.4-0.6):</strong> Recommend holding, neutral outlook</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-purple-600 dark:text-purple-400 mt-1">•</span>
                      <span className="leading-relaxed"><strong>Sell (0.2-0.4):</strong> Downward concern, consider selling</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-purple-600 dark:text-purple-400 mt-1">•</span>
                      <span className="leading-relaxed"><strong>Strong Sell (0.0-0.2):</strong> Strong downward concern, actively recommend selling</span>
                    </li>
                  </ul>
                </div>

                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white mb-4">4. News and Updates</h3>
                  <ul className="space-y-3 text-gray-700 dark:text-gray-300">
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-orange-600 dark:text-orange-400 mt-1">•</span>
                      <span className="leading-relaxed">Check the latest market news on the dashboard and news pages</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-orange-600 dark:text-orange-400 mt-1">•</span>
                      <span className="leading-relaxed">All times are displayed in Eastern Daylight Time (EDT)</span>
                    </li>
                    <li className="flex items-start space-x-3">
                      <span className="font-medium text-orange-600 dark:text-orange-400 mt-1">•</span>
                      <span className="leading-relaxed">Data is automatically updated every 30 seconds</span>
                    </li>
                  </ul>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Technical Info */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-xl">Technical Information</CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white mb-4">Data Sources</h3>
                  <ul className="space-y-3 text-sm text-gray-700 dark:text-gray-300">
                    <li className="flex items-start space-x-2">
                      <span className="text-blue-500 mt-1">•</span>
                      <span><strong>Real-time Prices:</strong> Finnhub API</span>
                    </li>
                    <li className="flex items-start space-x-2">
                      <span className="text-blue-500 mt-1">•</span>
                      <span><strong>Company Info:</strong> Alpha Vantage API</span>
                    </li>
                    <li className="flex items-start space-x-2">
                      <span className="text-blue-500 mt-1">•</span>
                      <span><strong>News:</strong> Alpha Vantage News API</span>
                    </li>
                    <li className="flex items-start space-x-2">
                      <span className="text-blue-500 mt-1">•</span>
                      <span><strong>Update Frequency:</strong> 30 seconds</span>
                    </li>
                  </ul>
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white mb-4">Tech Stack</h3>
                  <ul className="space-y-3 text-sm text-gray-700 dark:text-gray-300">
                    <li className="flex items-start space-x-2">
                      <span className="text-green-500 mt-1">•</span>
                      <span><strong>Frontend:</strong> Next.js, React, TypeScript</span>
                    </li>
                    <li className="flex items-start space-x-2">
                      <span className="text-green-500 mt-1">•</span>
                      <span><strong>State Management:</strong> Zustand</span>
                    </li>
                    <li className="flex items-start space-x-2">
                      <span className="text-green-500 mt-1">•</span>
                      <span><strong>Styling:</strong> Tailwind CSS</span>
                    </li>
                    <li className="flex items-start space-x-2">
                      <span className="text-green-500 mt-1">•</span>
                      <span><strong>Charts:</strong> Recharts</span>
                    </li>
                  </ul>
                </div>
              </div>
            </CardContent>
          </Card>

        </div>
      </Layout>
    </>
  );
};

export default About;