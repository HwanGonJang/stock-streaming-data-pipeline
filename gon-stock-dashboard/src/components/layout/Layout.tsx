import { useEffect } from 'react';
import { Toaster } from 'react-hot-toast';
import { useUIStore } from '@/store/ui';
import { useStockStore } from '@/store/stocks';
import Header from './Header';
import Sidebar from './Sidebar';
import NotificationCenter from './NotificationCenter';
import { cn } from '@/lib/utils';

interface LayoutProps {
  children: React.ReactNode;
}

export default function Layout({ children }: LayoutProps) {
  const { compactMode, setIsMobile } = useUIStore();
  const { fetchDashboardData } = useStockStore();

  // Handle responsive design
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 768);
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    return () => window.removeEventListener('resize', handleResize);
  }, [setIsMobile]);

  // Fetch initial data
  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  const sidebarWidth = compactMode ? 'pl-16' : 'md:pl-64';

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-dark-900 overflow-x-auto overflow-y-visible">
      <Header />
      <Sidebar />
      
      <main className={cn('pt-16', sidebarWidth)}>
        <div className="container mx-auto px-4 py-6 overflow-visible">
          {children}
        </div>
      </main>

      <NotificationCenter />
      
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: 'var(--toast-bg)',
            color: 'var(--toast-color)',
          },
        }}
      />
    </div>
  );
}