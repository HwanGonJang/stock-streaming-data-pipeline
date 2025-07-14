import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/lib/utils';

interface BadgeProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'outline';
  size?: 'sm' | 'md' | 'lg';
}

const Badge = forwardRef<HTMLDivElement, BadgeProps>(
  ({ className, variant = 'default', size = 'md', ...props }, ref) => {
    const baseStyles = 'inline-flex items-center rounded-full font-medium transition-all duration-200';
    
    const variants = {
      default: 'bg-gray-100 text-gray-800 dark:bg-dark-700 dark:text-gray-200',
      success: 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400',
      warning: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400',
      danger: 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400',
      info: 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400',
      outline: 'border border-gray-300 text-gray-700 dark:border-dark-600 dark:text-gray-300',
    };
    
    const sizes = {
      sm: 'px-2 py-0.5 text-xs',
      md: 'px-2.5 py-1 text-xs',
      lg: 'px-3 py-1.5 text-sm',
    };
    
    return (
      <div
        ref={ref}
        className={cn(
          baseStyles,
          variants[variant],
          sizes[size],
          className
        )}
        {...props}
      />
    );
  }
);

Badge.displayName = 'Badge';

export default Badge;