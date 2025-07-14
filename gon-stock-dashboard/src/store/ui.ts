import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

interface UIState {
  // Theme
  theme: 'dark' | 'light' | 'system';
  
  // Layout
  sidebarOpen: boolean;
  compactMode: boolean;
  
  // Dashboard settings
  autoRefresh: boolean;
  refreshInterval: number;
  showChangePercent: boolean;
  showVolume: boolean;
  
  // Chart settings
  chartType: 'line' | 'candlestick';
  chartPeriod: '1D' | '1W' | '1M' | '3M' | '6M' | '1Y' | '2Y' | '5Y';
  showTechnicalIndicators: boolean;
  
  // Notifications
  notifications: Notification[];
  notificationsEnabled: boolean;
  
  // Mobile
  isMobile: boolean;
  
  // Actions
  setTheme: (theme: 'dark' | 'light' | 'system') => void;
  setSidebarOpen: (open: boolean) => void;
  toggleSidebar: () => void;
  setCompactMode: (compact: boolean) => void;
  setAutoRefresh: (autoRefresh: boolean) => void;
  setRefreshInterval: (interval: number) => void;
  setShowChangePercent: (show: boolean) => void;
  setShowVolume: (show: boolean) => void;
  setChartType: (type: 'line' | 'candlestick') => void;
  setChartPeriod: (period: '1D' | '1W' | '1M' | '3M' | '6M' | '1Y' | '2Y' | '5Y') => void;
  setShowTechnicalIndicators: (show: boolean) => void;
  addNotification: (notification: Omit<Notification, 'id' | 'timestamp'>) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
  setNotificationsEnabled: (enabled: boolean) => void;
  setIsMobile: (isMobile: boolean) => void;
}

interface Notification {
  id: string;
  type: 'info' | 'success' | 'warning' | 'error';
  title: string;
  message: string;
  timestamp: number;
  autoHide?: boolean;
  duration?: number;
}

export const useUIStore = create<UIState>()(
  devtools(
    persist(
      (set, get) => ({
        // Initial state
        theme: 'system',
        sidebarOpen: false,
        compactMode: false,
        autoRefresh: true,
        refreshInterval: 30000,
        showChangePercent: true,
        showVolume: true,
        chartType: 'line',
        chartPeriod: '1D',
        showTechnicalIndicators: false,
        notifications: [],
        notificationsEnabled: true,
        isMobile: false,
        
        // Actions
        setTheme: (theme) => set({ theme }),
        
        setSidebarOpen: (open) => set({ sidebarOpen: open }),
        
        toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),
        
        setCompactMode: (compact) => set({ compactMode: compact }),
        
        setAutoRefresh: (autoRefresh) => set({ autoRefresh }),
        
        setRefreshInterval: (interval) => set({ refreshInterval: interval }),
        
        setShowChangePercent: (show) => set({ showChangePercent: show }),
        
        setShowVolume: (show) => set({ showVolume: show }),
        
        setChartType: (type) => set({ chartType: type }),
        
        setChartPeriod: (period) => set({ chartPeriod: period }),
        
        setShowTechnicalIndicators: (show) => set({ showTechnicalIndicators: show }),
        
        addNotification: (notification) => {
          const newNotification: Notification = {
            ...notification,
            id: Math.random().toString(36).substr(2, 9),
            timestamp: Date.now(),
            autoHide: notification.autoHide ?? true,
            duration: notification.duration ?? 5000,
          };
          
          set((state) => ({
            notifications: [...state.notifications, newNotification],
          }));
          
          // Auto-hide notification if enabled
          if (newNotification.autoHide) {
            setTimeout(() => {
              get().removeNotification(newNotification.id);
            }, newNotification.duration);
          }
        },
        
        removeNotification: (id) => {
          set((state) => ({
            notifications: state.notifications.filter(n => n.id !== id),
          }));
        },
        
        clearNotifications: () => set({ notifications: [] }),
        
        setNotificationsEnabled: (enabled) => set({ notificationsEnabled: enabled }),
        
        setIsMobile: (isMobile) => set({ isMobile }),
      }),
      {
        name: 'ui-store',
        partialize: (state) => ({
          theme: state.theme,
          compactMode: state.compactMode,
          autoRefresh: state.autoRefresh,
          refreshInterval: state.refreshInterval,
          showChangePercent: state.showChangePercent,
          showVolume: state.showVolume,
          chartType: state.chartType,
          chartPeriod: state.chartPeriod,
          showTechnicalIndicators: state.showTechnicalIndicators,
          notificationsEnabled: state.notificationsEnabled,
        }),
      }
    ),
    {
      name: 'ui-store',
    }
  )
);