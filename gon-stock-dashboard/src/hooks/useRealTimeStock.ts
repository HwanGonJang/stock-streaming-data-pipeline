import { useEffect, useRef, useState } from 'react';
import { SupportedSymbol, RealTimeStock } from '@/types/api';
import { stocksApi } from '@/lib/api/stocks';
import { useStockStore } from '@/store/stocks';
import { useUIStore } from '@/store/ui';
import { SSE_RECONNECT_INTERVAL } from '@/lib/constants';

interface UseRealTimeStockReturn {
  isConnected: boolean;
  error: string | null;
  lastUpdate: number;
  reconnect: () => void;
  disconnect: () => void;
}

export function useRealTimeStock(symbol: SupportedSymbol, useKoreanTimeSimulation: boolean = false): UseRealTimeStockReturn {
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdate, setLastUpdate] = useState(0);
  
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 10;
  
  const updateRealTimePrice = useStockStore(state => state.updateRealTimePrice);
  const addNotification = useUIStore(state => state.addNotification);
  const notificationsEnabled = useUIStore(state => state.notificationsEnabled);
  
  const connect = () => {
    try {
      // Close existing connection
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
      
      const eventSource = stocksApi.subscribeToRealTimeStock(symbol, useKoreanTimeSimulation);
      eventSourceRef.current = eventSource;
      
      eventSource.onopen = () => {
        setIsConnected(true);
        setError(null);
        reconnectAttempts.current = 0;
        
        if (notificationsEnabled && reconnectAttempts.current > 0) {
          addNotification({
            type: 'success',
            title: 'Connected',
            message: `Real-time data for ${symbol} is now streaming`,
          });
        }
      };
      
      eventSource.onmessage = (event) => {
        try {
          const data: RealTimeStock = JSON.parse(event.data);
          updateRealTimePrice(symbol, data);
          setLastUpdate(Date.now());
        } catch (parseError) {
          console.error('Failed to parse SSE data:', parseError);
          setError('Failed to parse real-time data');
        }
      };
      
      eventSource.onerror = (error) => {
        console.error('SSE error:', error);
        setIsConnected(false);
        
        if (reconnectAttempts.current < maxReconnectAttempts) {
          setError(`Connection lost. Reconnecting... (${reconnectAttempts.current + 1}/${maxReconnectAttempts})`);
          
          reconnectTimeoutRef.current = setTimeout(() => {
            reconnectAttempts.current++;
            connect();
          }, SSE_RECONNECT_INTERVAL);
        } else {
          setError('Failed to connect to real-time data. Please refresh the page.');
          
          if (notificationsEnabled) {
            addNotification({
              type: 'error',
              title: 'Connection Failed',
              message: `Failed to connect to real-time data for ${symbol}`,
              autoHide: false,
            });
          }
        }
      };
      
    } catch (error) {
      console.error('Failed to create SSE connection:', error);
      setError('Failed to establish real-time connection');
      setIsConnected(false);
    }
  };
  
  const disconnect = () => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }
    
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }
    
    setIsConnected(false);
    setError(null);
    reconnectAttempts.current = 0;
  };
  
  const reconnect = () => {
    disconnect();
    reconnectAttempts.current = 0;
    connect();
  };
  
  useEffect(() => {
    connect();
    
    return () => {
      disconnect();
    };
  }, [symbol, useKoreanTimeSimulation]); // eslint-disable-line react-hooks/exhaustive-deps
  
  // Handle page visibility change
  useEffect(() => {
    const handleVisibilityChange = () => {
      if (document.visibilityState === 'visible') {
        if (!isConnected && reconnectAttempts.current < maxReconnectAttempts) {
          reconnect();
        }
      } else {
        // Optionally disconnect when page is hidden to save resources
        // disconnect();
      }
    };
    
    document.addEventListener('visibilitychange', handleVisibilityChange);
    
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  }, [isConnected]); // eslint-disable-line react-hooks/exhaustive-deps
  
  return {
    isConnected,
    error,
    lastUpdate,
    reconnect,
    disconnect,
  };
}

// Hook for multiple symbols
export function useRealTimeStocks(symbols: SupportedSymbol[], useKoreanTimeSimulation: boolean = false) {
  const connections = symbols.map(symbol => useRealTimeStock(symbol, useKoreanTimeSimulation));
  
  const reconnectAll = () => {
    connections.forEach(conn => conn.reconnect());
  };
  
  const disconnectAll = () => {
    connections.forEach(conn => conn.disconnect());
  };
  
  const connectionsMap = connections.reduce((acc, conn, index) => {
    acc[symbols[index]] = conn;
    return acc;
  }, {} as Record<SupportedSymbol, UseRealTimeStockReturn>);
  
  return {
    connections: connectionsMap,
    isAnyConnected: connections.some(conn => conn.isConnected),
    allConnected: connections.every(conn => conn.isConnected),
    reconnectAll,
    disconnectAll,
  };
}