import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatCurrency(
  value: number,
  currency: string = 'USD'
): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value);
}

export function formatNumber(value: number): string {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(value);
}

export function formatLargeNumber(value: number): string {
  const absValue = Math.abs(value);
  
  if (absValue >= 1e12) {
    return formatNumber(value / 1e12) + 'T';
  } else if (absValue >= 1e9) {
    return formatNumber(value / 1e9) + 'B';
  } else if (absValue >= 1e6) {
    return formatNumber(value / 1e6) + 'M';
  } else if (absValue >= 1e3) {
    return formatNumber(value / 1e3) + 'K';
  }
  
  return formatNumber(value);
}

export function formatPercentage(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'percent',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value / 100);
}

export function getStockChangeColor(change: number): string {
  if (change > 0) return 'text-bull';
  if (change < 0) return 'text-bear';
  return 'text-neutral';
}

export function getStockChangeBgColor(change: number): string {
  if (change > 0) return 'bg-bull bg-opacity-10';
  if (change < 0) return 'bg-bear bg-opacity-10';
  return 'bg-neutral bg-opacity-10';
}

export function getStockChangeIcon(change: number): string {
  if (change > 0) return '↑';
  if (change < 0) return '↓';
  return '→';
}

export function getRecommendationColor(score: number): string {
  if (score >= 0.8) return 'text-green-600 dark:text-green-400';
  if (score >= 0.6) return 'text-blue-600 dark:text-blue-400';
  if (score >= 0.4) return 'text-yellow-600 dark:text-yellow-400';
  if (score >= 0.2) return 'text-orange-600 dark:text-orange-400';
  return 'text-red-600 dark:text-red-400';
}

export function getRecommendationBgColor(score: number): string {
  if (score >= 0.8) return 'bg-green-100 dark:bg-green-900/20';
  if (score >= 0.6) return 'bg-blue-100 dark:bg-blue-900/20';
  if (score >= 0.4) return 'bg-yellow-100 dark:bg-yellow-900/20';
  if (score >= 0.2) return 'bg-orange-100 dark:bg-orange-900/20';
  return 'bg-red-100 dark:bg-red-900/20';
}

export function debounce<T extends (...args: any[]) => void>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: NodeJS.Timeout;
  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delay);
  };
}

export function throttle<T extends (...args: any[]) => void>(
  func: T,
  limit: number
): (...args: Parameters<T>) => void {
  let inThrottle: boolean;
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  };
}

export function calculateChange(current: number, previous: number): number {
  return previous === 0 ? 0 : ((current - previous) / previous) * 100;
}

export function isValidSymbol(symbol: string): boolean {
  // const validSymbols = [
  //   'AAPL', 'MSFT', 'GOOGL', 'AMZN', 'META', 'NVDA', 'TSLA', 'AVGO',
  //   'CRM', 'ORCL', 'NFLX', 'ADBE', 'AMD', 'INTC', 'PYPL', 'CSCO',
  //   'QCOM', 'TXN', 'AMAT', 'PLTR'
  // ];
  const validSymbols = [
    'AAPL', 'MSFT'
  ];

  return validSymbols.includes(symbol.toUpperCase());
}

export function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}