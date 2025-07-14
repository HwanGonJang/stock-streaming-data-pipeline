import { forwardRef, InputHTMLAttributes } from 'react';
import { cn } from '@/lib/utils';

interface SwitchProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
  label?: string;
  description?: string;
  error?: string;
  onCheckedChange?: (checked: boolean) => void;
}

const Switch = forwardRef<HTMLInputElement, SwitchProps>(
  (
    {
      className,
      label,
      description,
      error,
      disabled,
      checked,
      onCheckedChange,
      ...props
    },
    ref
  ) => {
    return (
      <div className="flex items-center space-x-3">
        <label className="relative inline-flex items-center cursor-pointer">
          <input
            ref={ref}
            type="checkbox"
            className="sr-only peer"
            checked={checked}
            disabled={disabled}
            onChange={(e) => {
              if (onCheckedChange) {
                onCheckedChange(e.target.checked);
              }
              if (props.onChange) {
                props.onChange(e);
              }
            }}
            {...props}
          />
          <div
            className={cn(
              'relative w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 dark:peer-focus:ring-primary-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[\'\'] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-primary-600',
              disabled && 'cursor-not-allowed opacity-50',
              className
            )}
          />
        </label>
        {(label || description) && (
          <div className="flex-1">
            {label && (
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                {label}
              </label>
            )}
            {description && (
              <p className="text-sm text-gray-500 dark:text-gray-400">
                {description}
              </p>
            )}
          </div>
        )}
        {error && (
          <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
        )}
      </div>
    );
  }
);

Switch.displayName = 'Switch';

export default Switch;