import { cn } from '@/lib/utils';

interface LoadingProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export function Loading({ size = 'md', className }: LoadingProps) {
  const sizes = {
    sm: 'h-4 w-4',
    md: 'h-8 w-8',
    lg: 'h-12 w-12',
  };
  
  return (
    <div className={cn('flex items-center justify-center', className)}>
      <div
        className={cn(
          'animate-spin rounded-full border-2 border-gray-300 border-t-primary-600',
          sizes[size]
        )}
      />
    </div>
  );
}

export function LoadingSpinner({ size = 'md', className }: LoadingProps) {
  const sizes = {
    sm: 'h-4 w-4',
    md: 'h-6 w-6',
    lg: 'h-8 w-8',
  };
  
  return (
    <div
      className={cn(
        'animate-spin rounded-full border-2 border-gray-300 border-t-primary-600',
        sizes[size],
        className
      )}
    />
  );
}

interface LoadingSkeletonProps {
  className?: string;
  rows?: number;
}

export function LoadingSkeleton({ className, rows = 1 }: LoadingSkeletonProps) {
  return (
    <div className={cn('space-y-3', className)}>
      {Array.from({ length: rows }).map((_, index) => (
        <div
          key={index}
          className="h-4 bg-gray-200 dark:bg-dark-700 rounded shimmer"
        />
      ))}
    </div>
  );
}

export function LoadingCard({ className }: { className?: string }) {
  return (
    <div className={cn('p-6 space-y-4', className)}>
      <div className="flex items-center space-x-4">
        <div className="h-12 w-12 bg-gray-200 dark:bg-dark-700 rounded-full shimmer" />
        <div className="flex-1 space-y-2">
          <div className="h-4 bg-gray-200 dark:bg-dark-700 rounded shimmer" />
          <div className="h-3 bg-gray-200 dark:bg-dark-700 rounded shimmer w-3/4" />
        </div>
      </div>
      <div className="space-y-2">
        <div className="h-4 bg-gray-200 dark:bg-dark-700 rounded shimmer" />
        <div className="h-4 bg-gray-200 dark:bg-dark-700 rounded shimmer w-5/6" />
        <div className="h-4 bg-gray-200 dark:bg-dark-700 rounded shimmer w-4/6" />
      </div>
    </div>
  );
}

export function LoadingTable({ rows = 5, columns = 4, className }: {
  rows?: number;
  columns?: number;
  className?: string;
}) {
  return (
    <div className={cn('space-y-4', className)}>
      {Array.from({ length: rows }).map((_, rowIndex) => (
        <div key={rowIndex} className="flex space-x-4">
          {Array.from({ length: columns }).map((_, colIndex) => (
            <div
              key={colIndex}
              className="h-6 bg-gray-200 dark:bg-dark-700 rounded shimmer flex-1"
            />
          ))}
        </div>
      ))}
    </div>
  );
}

export default Loading;