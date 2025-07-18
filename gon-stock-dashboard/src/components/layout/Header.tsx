import { Moon, Sun, Clock } from 'lucide-react';
import { useTheme } from '@/lib/theme-provider';
import { useUIStore } from '@/store/ui';
import { Button } from '@/components/ui';
import { useState, useEffect } from 'react';

export default function Header() {
  const { theme, setTheme } = useTheme();
  const { toggleSidebar } = useUIStore();
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const toggleTheme = () => {
    if (theme === 'dark') {
      setTheme('light');
    } else if (theme === 'light') {
      setTheme('system');
    } else {
      setTheme('dark');
    }
  };

  const getThemeIcon = () => {
    if (theme === 'dark') return <Moon className="h-4 w-4" />;
    if (theme === 'light') return <Sun className="h-4 w-4" />;
    return <Sun className="h-4 w-4" />;
  };

  const getMarketStatus = () => {
    const utcHour = currentTime.getUTCHours();
    
    // US Market: UTC 08:00-24:00 (considering market hours and extended hours)
    const usMarketOpen = utcHour >= 8 && utcHour < 24;
    
    // Korean Market: UTC 19:00-11:00 (overnight UTC)
    const koreanMarketOpen = utcHour >= 19 || utcHour < 11;
    
    return { usMarketOpen, koreanMarketOpen };
  };

  const { usMarketOpen, koreanMarketOpen } = getMarketStatus();

  return (
    <header className="bg-white dark:bg-dark-900 border-b border-gray-200 dark:border-dark-700 fixed top-0 left-0 right-0 z-50 h-16">
      <div className="flex items-center justify-between px-4 py-3 h-full">
        {/* Left side - Logo and Menu */}
        <div className="flex items-center space-x-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleSidebar}
            className="md:hidden"
          >
            <div className="flex flex-col space-y-1">
              <span className="block h-0.5 w-6 bg-current"></span>
              <span className="block h-0.5 w-6 bg-current"></span>
              <span className="block h-0.5 w-6 bg-current"></span>
            </div>
          </Button>
          
          <div className="flex items-center space-x-2">
            <div className="h-8 w-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
              {/* TODO: Replace with actual logo */}
              <span className="text-white font-bold text-sm">G</span>
            </div>
            <h1 className="text-xl font-bold text-gray-900 dark:text-white">
              Gon Stock Dashboard
            </h1>
          </div>
        </div>

        {/* Right side - Actions */}
        <div className="flex items-center space-x-4">
          {/* Market Status */}
          <div className="hidden sm:flex items-center space-x-3">
            <div className="flex items-center space-x-1">
              <div className={`h-2 w-2 rounded-full ${usMarketOpen ? 'bg-green-500' : 'bg-red-500'}`} />
              <span className="text-xs font-medium text-gray-700 dark:text-gray-300">US</span>
            </div>
            <div className="flex items-center space-x-1">
              <div className={`h-2 w-2 rounded-full ${koreanMarketOpen ? 'bg-green-500' : 'bg-red-500'}`} />
              <span className="text-xs font-medium text-gray-700 dark:text-gray-300">KR</span>
            </div>
            <div className="flex items-center space-x-1 text-xs text-gray-500 dark:text-gray-400">
              <Clock className="h-3 w-3" />
              <span>{currentTime.toLocaleTimeString('en-US', { 
                hour: '2-digit', 
                minute: '2-digit',
                hour12: false,
                timeZone: 'UTC'
              })} UTC</span>
            </div>
          </div>
          
          {/* Theme toggle */}
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleTheme}
            className="relative"
          >
            {getThemeIcon()}
          </Button>
        </div>
      </div>
    </header>
  );
}