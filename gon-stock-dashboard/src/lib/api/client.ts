import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ApiResponse, ErrorResponse } from '@/types/api';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'https://api.hwangonjang.com',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        // Add timestamp to prevent caching
        if (config.params) {
          config.params._t = Date.now();
        } else {
          config.params = { _t: Date.now() };
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response: AxiosResponse<ApiResponse<any>>) => {
        return response;
      },
      (error) => {
        if (error.response?.data) {
          const errorData = error.response.data as ApiResponse<ErrorResponse>;
          console.error('API Error:', errorData);
          return Promise.reject(new Error(errorData.body?.message || 'An error occurred'));
        }
        return Promise.reject(error);
      }
    );
  }

  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.get<ApiResponse<T>>(url, config);
    return response.data.body;
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.post<ApiResponse<T>>(url, data, config);
    return response.data.body;
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.put<ApiResponse<T>>(url, data, config);
    return response.data.body;
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<ApiResponse<T>>(url, config);
    return response.data.body;
  }

  // Server-Sent Events method
  createSSE(url: string, params?: { intervalSeconds?: number; useKoreanTimeSimulation?: boolean }): EventSource {
    const fullUrl = `${this.client.defaults.baseURL}${url}`;
    
    if (params) {
      const searchParams = new URLSearchParams();
      if (params.intervalSeconds) {
        searchParams.append('intervalSeconds', params.intervalSeconds.toString());
      }
      if (params.useKoreanTimeSimulation !== undefined) {
        searchParams.append('useKoreanTimeSimulation', params.useKoreanTimeSimulation.toString());
      }
      
      const urlWithParams = `${fullUrl}?${searchParams.toString()}`;
      return new EventSource(urlWithParams);
    }
    
    return new EventSource(fullUrl);
  }
}

export const apiClient = new ApiClient();