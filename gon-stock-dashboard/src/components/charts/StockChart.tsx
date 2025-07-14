import { useState, useMemo } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, ComposedChart } from 'recharts';
import { format } from 'date-fns';
import { TrendingUp, BarChart3, LineChart as LineChartIcon } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent, Button, Select } from '@/components/ui';
import { formatCurrency, formatLargeNumber, cn } from '@/lib/utils';
import { DailyPrice } from '@/types/api';
import { TIME_PERIODS } from '@/lib/constants';

interface StockChartProps {
  data: DailyPrice[];
  symbol: string;
  className?: string;
}

export default function StockChart({ data, symbol, className }: StockChartProps) {
  const [timePeriod, setTimePeriod] = useState('1M');

  const filteredData = useMemo(() => {
    if (!data.length) return [];

    const period = TIME_PERIODS[timePeriod as keyof typeof TIME_PERIODS];
    const now = new Date();
    const startDate = new Date();

    if (period.unit === 'day') {
      startDate.setDate(now.getDate() - period.value);
    } else if (period.unit === 'month') {
      startDate.setMonth(now.getMonth() - period.value);
    } else if (period.unit === 'year') {
      startDate.setFullYear(now.getFullYear() - period.value);
    }

    return data
      .filter(item => new Date(item.date) >= startDate)
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
      .map(item => ({
        ...item,
        formattedDate: format(new Date(item.date), 'MMM dd'),
        change: item.close - item.open,
        changePercent: ((item.close - item.open) / item.open) * 100,
      }));
  }, [data, timePeriod]);

  const chartStats = useMemo(() => {
    if (!filteredData.length) return null;

    const firstPrice = filteredData[0].close;
    const lastPrice = filteredData[filteredData.length - 1].close;
    const change = lastPrice - firstPrice;
    const changePercent = (change / firstPrice) * 100;

    const high = Math.max(...filteredData.map(d => d.high));
    const low = Math.min(...filteredData.map(d => d.low));
    const avgVolume = filteredData.reduce((sum, d) => sum + d.volume, 0) / filteredData.length;

    return {
      change,
      changePercent,
      high,
      low,
      avgVolume,
      firstPrice,
      lastPrice,
    };
  }, [filteredData]);

  const CustomTooltip = ({ active, payload, label }: any) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="bg-white dark:bg-dark-800 p-3 border border-gray-200 dark:border-dark-600 rounded-lg shadow-lg">
          <p className="font-medium text-gray-900 dark:text-white">{label}</p>
          <div className="space-y-1 mt-2">
            <p className="text-sm">
              <span className="text-gray-600 dark:text-gray-400">Open: </span>
              <span className="font-medium">{formatCurrency(data.open)}</span>
            </p>
            <p className="text-sm">
              <span className="text-gray-600 dark:text-gray-400">High: </span>
              <span className="font-medium">{formatCurrency(data.high)}</span>
            </p>
            <p className="text-sm">
              <span className="text-gray-600 dark:text-gray-400">Low: </span>
              <span className="font-medium">{formatCurrency(data.low)}</span>
            </p>
            <p className="text-sm">
              <span className="text-gray-600 dark:text-gray-400">Close: </span>
              <span className="font-medium">{formatCurrency(data.close)}</span>
            </p>
            <p className="text-sm">
              <span className="text-gray-600 dark:text-gray-400">Volume: </span>
              <span className="font-medium">{formatLargeNumber(data.volume)}</span>
            </p>
          </div>
        </div>
      );
    }
    return null;
  };

  if (!filteredData.length) {
    return (
      <Card className={className}>
        <CardContent className="p-6">
          <div className="text-center py-8 text-gray-500 dark:text-gray-400">
            <BarChart3 className="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p>No chart data available for {symbol}</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <CardTitle className="flex items-center space-x-2">
            <TrendingUp className="h-5 w-5 text-primary-600" />
            <span>{symbol} Price Chart</span>
          </CardTitle>
          
          <div className="flex items-center space-x-2">
            <Select
              value={timePeriod}
              onValueChange={setTimePeriod}
              options={Object.entries(TIME_PERIODS).map(([key, value]) => ({
                value: key,
                label: value.label,
              }))}
            />
          </div>
        </div>
        
        {chartStats && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4">
            <div className="text-center">
              <p className="text-sm text-gray-500 dark:text-gray-400">Change</p>
              <p className={cn(
                'font-semibold',
                chartStats.change >= 0 ? 'text-green-600' : 'text-red-600'
              )}>
                {chartStats.change >= 0 ? '+' : ''}{formatCurrency(chartStats.change)}
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-500 dark:text-gray-400">Change %</p>
              <p className={cn(
                'font-semibold',
                chartStats.changePercent >= 0 ? 'text-green-600' : 'text-red-600'
              )}>
                {chartStats.changePercent >= 0 ? '+' : ''}{chartStats.changePercent.toFixed(2)}%
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-500 dark:text-gray-400">High</p>
              <p className="font-semibold">{formatCurrency(chartStats.high)}</p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-500 dark:text-gray-400">Low</p>
              <p className="font-semibold">{formatCurrency(chartStats.low)}</p>
            </div>
          </div>
        )}
      </CardHeader>
      
      <CardContent>
        {/* Price Chart */}
        <div className="mb-6">
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Price</h4>
          <div className="h-60">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={filteredData}>
                <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                <XAxis 
                  dataKey="formattedDate" 
                  axisLine={false}
                  tickLine={false}
                  className="text-xs"
                />
                <YAxis 
                  axisLine={false}
                  tickLine={false}
                  className="text-xs"
                  tickFormatter={(value) => formatCurrency(value)}
                />
                <Tooltip content={<CustomTooltip />} />
                <Line 
                  type="monotone" 
                  dataKey="close" 
                  stroke="#3b82f6" 
                  strokeWidth={2}
                  dot={false}
                  activeDot={{ r: 4, fill: '#3b82f6' }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Volume Chart */}
        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Volume</h4>
          <div className="h-40">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={filteredData}>
                <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                <XAxis 
                  dataKey="formattedDate" 
                  axisLine={false}
                  tickLine={false}
                  className="text-xs"
                />
                <YAxis 
                  axisLine={false}
                  tickLine={false}
                  className="text-xs"
                  tickFormatter={(value) => formatLargeNumber(value)}
                />
                <Tooltip content={<CustomTooltip />} />
                <Bar 
                  dataKey="volume" 
                  fill="#10b981"
                  opacity={0.8}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}