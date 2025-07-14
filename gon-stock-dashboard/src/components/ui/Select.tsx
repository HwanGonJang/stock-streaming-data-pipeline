import { forwardRef, SelectHTMLAttributes } from 'react';
import { cn } from '@/lib/utils';

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  options: Array<{
    value: string;
    label: string;
    disabled?: boolean;
  }>;
  placeholder?: string;
  error?: string;
  label?: string;
  onValueChange?: (value: string) => void;
}

const Select = forwardRef<HTMLSelectElement, SelectProps>(
  (
    {
      className,
      options,
      placeholder,
      error,
      label,
      disabled,
      onValueChange,
      ...props
    },
    ref
  ) => {
    const baseStyles = 'block w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm placeholder-gray-400 focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500 disabled:cursor-not-allowed disabled:bg-gray-50 disabled:text-gray-500 dark:border-dark-600 dark:bg-dark-800 dark:text-gray-100 dark:placeholder-gray-500 dark:focus:border-primary-400 dark:disabled:bg-dark-700';
    
    const errorStyles = error 
      ? 'border-red-500 focus:border-red-500 focus:ring-red-500 dark:border-red-400' 
      : '';
    
    return (
      <div className="space-y-1">
        {label && (
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
            {label}
          </label>
        )}
        <select
          ref={ref}
          className={cn(
            baseStyles,
            errorStyles,
            className
          )}
          disabled={disabled}
          onChange={(e) => {
            if (onValueChange) {
              onValueChange(e.target.value);
            }
            if (props.onChange) {
              props.onChange(e);
            }
          }}
          {...props}
        >
          {placeholder && (
            <option value="" disabled>
              {placeholder}
            </option>
          )}
          {options.map((option) => (
            <option
              key={option.value}
              value={option.value}
              disabled={option.disabled}
            >
              {option.label}
            </option>
          ))}
        </select>
        {error && (
          <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
        )}
      </div>
    );
  }
);

Select.displayName = 'Select';

export default Select;