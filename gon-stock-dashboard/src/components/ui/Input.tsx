import { forwardRef, InputHTMLAttributes } from 'react';
import { cn } from '@/lib/utils';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  helper?: string;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      className,
      type = 'text',
      label,
      error,
      leftIcon,
      rightIcon,
      helper,
      disabled,
      ...props
    },
    ref
  ) => {
    const baseStyles = 'block w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm placeholder-gray-400 focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500 disabled:cursor-not-allowed disabled:bg-gray-50 disabled:text-gray-500 dark:border-dark-600 dark:bg-dark-800 dark:text-gray-100 dark:placeholder-gray-500 dark:focus:border-primary-400 dark:disabled:bg-dark-700';
    
    const errorStyles = error 
      ? 'border-red-500 focus:border-red-500 focus:ring-red-500 dark:border-red-400' 
      : '';
    
    const iconPadding = leftIcon ? 'pl-10' : rightIcon ? 'pr-10' : '';
    
    return (
      <div className="space-y-1">
        {label && (
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
            {label}
          </label>
        )}
        <div className="relative">
          {leftIcon && (
            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
              <span className="text-gray-400 dark:text-gray-500">{leftIcon}</span>
            </div>
          )}
          <input
            ref={ref}
            type={type}
            className={cn(
              baseStyles,
              errorStyles,
              iconPadding,
              className
            )}
            disabled={disabled}
            {...props}
          />
          {rightIcon && (
            <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <span className="text-gray-400 dark:text-gray-500">{rightIcon}</span>
            </div>
          )}
        </div>
        {error && (
          <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
        )}
        {helper && !error && (
          <p className="text-sm text-gray-500 dark:text-gray-400">{helper}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;