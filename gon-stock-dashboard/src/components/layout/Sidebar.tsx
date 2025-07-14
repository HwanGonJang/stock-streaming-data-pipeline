import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { 
  Home, 
  TrendingUp, 
  Newspaper, 
  ChevronRight,
  ChevronDown,
  Info
} from 'lucide-react';
import { useUIStore } from '@/store/ui';
import { useStockStore } from '@/store/stocks';
import { Button, Badge } from '@/components/ui';
import { cn } from '@/lib/utils';
import { SUPPORTED_SYMBOLS } from '@/lib/constants';

const navigationItems = [
  { name: 'Dashboard', href: '/', icon: Home },
  { name: 'Stocks', href: '/stocks', icon: TrendingUp },
  { name: 'News', href: '/news', icon: Newspaper },
  { name: 'About', href: '/about', icon: Info },
];

export default function Sidebar() {
  const router = useRouter();
  const { sidebarOpen, setSidebarOpen, compactMode } = useUIStore();
  const { favorites } = useStockStore();
  const [stocksExpanded, setStocksExpanded] = useState(false);

  const isActive = (href: string) => {
    if (href === '/') {
      return router.pathname === '/';
    }
    return router.pathname.startsWith(href);
  };

  const handleStockClick = (symbol: string) => {
    router.push(`/stocks/${symbol}`);
    if (window.innerWidth < 768) {
      setSidebarOpen(false);
    }
  };

  if (compactMode) {
    return (
      <div className="fixed left-0 top-16 bottom-0 w-16 bg-white dark:bg-dark-900 border-r border-gray-200 dark:border-dark-700 flex flex-col z-40">
        <nav className="flex-1 px-2 py-4 space-y-2">
          {navigationItems.map((item) => (
            <Link
              key={item.name}
              href={item.href}
              className={cn(
                'flex items-center justify-center w-12 h-12 rounded-lg text-sm font-medium transition-colors hover-transition',
                isActive(item.href)
                  ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/20 dark:text-primary-400'
                  : 'text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-dark-800'
              )}
              title={item.name}
            >
              <item.icon className="h-5 w-5" />
            </Link>
          ))}
        </nav>
      </div>
    );
  }

  return (
    <>
      {/* Overlay for mobile */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 bg-black bg-opacity-50 md:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={cn(
        'fixed left-0 top-16 bottom-0 w-64 bg-white dark:bg-dark-900 border-r border-gray-200 dark:border-dark-700 flex flex-col z-40 transition-transform duration-300',
        sidebarOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'
      )}>
        <nav className="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
          {navigationItems.map((item) => (
            <div key={item.name}>
              <Link
                href={item.href}
                className={cn(
                  'flex items-center px-3 py-2 rounded-lg text-sm font-medium transition-colors hover-transition',
                  isActive(item.href)
                    ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/20 dark:text-primary-400'
                    : 'text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-dark-800'
                )}
              >
                <item.icon className="h-5 w-5 mr-3" />
                {item.name}
              </Link>

              {/* Expandable stock list for Stocks menu */}
              {item.name === 'Stocks' && (
                <div className="ml-6 mt-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setStocksExpanded(!stocksExpanded)}
                    className="w-full justify-start text-gray-500 dark:text-gray-400"
                  >
                    {stocksExpanded ? (
                      <ChevronDown className="h-4 w-4 mr-2" />
                    ) : (
                      <ChevronRight className="h-4 w-4 mr-2" />
                    )}
                    All Stocks
                  </Button>
                  
                  {stocksExpanded && (
                    <div className="mt-2 space-y-1 max-h-64 overflow-y-auto">
                      {SUPPORTED_SYMBOLS.map((symbol) => (
                        <button
                          key={symbol}
                          onClick={() => handleStockClick(symbol)}
                          className={cn(
                            'w-full text-left px-3 py-1 rounded text-sm transition-colors hover-transition',
                            router.query.symbol === symbol
                              ? 'bg-primary-50 text-primary-600 dark:bg-primary-900/10 dark:text-primary-400'
                              : 'text-gray-600 hover:bg-gray-50 dark:text-gray-400 dark:hover:bg-dark-800'
                          )}
                        >
                          {symbol}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </nav>

        {/* Footer */}
        <div className="px-4 py-4 border-t border-gray-200 dark:border-dark-700">
          <div className="text-xs text-gray-500 dark:text-gray-400 space-y-1">
            <p>Gon Stock Dashboard v1.0</p>
            <p>Real-time market data</p>
          </div>
        </div>
      </div>
    </>
  );
}