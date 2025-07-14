# Gon Stock Dashboard

A modern, real-time stock market dashboard built with React, Next.js, and TypeScript. This application provides comprehensive stock market data and investment insights for 20 top NASDAQ technology stocks.

## 🚀 Features

### Dashboard
- **Real-time Stock Data**: Live price updates via Server-Sent Events (SSE)
- **Market Overview**: Comprehensive market statistics and trends
- **Stock Recommendations**: AI-powered investment recommendations with scoring
- **Interactive Charts**: Candlestick and line charts with technical indicators
- **Responsive Design**: Optimized for desktop, tablet, and mobile devices

### Stock Analysis
- **Detailed Company Information**: Comprehensive company profiles and metrics
- **Financial Data**: Income statements, balance sheets, and cash flow statements
- **News Integration**: Real-time news with sentiment analysis
- **Technical Indicators**: Moving averages, P/E ratios, and more
- **Watchlist**: Personal stock tracking and favorites

### User Experience
- **Dark/Light Mode**: Automatic and manual theme switching
- **Real-time Notifications**: System alerts and updates
- **Advanced Search**: Quick stock symbol and company name search
- **Sorting & Filtering**: Multiple criteria for data organization

## 🛠️ Tech Stack

### Frontend
- **Framework**: Next.js 14 with TypeScript
- **State Management**: Zustand for global state
- **Styling**: Tailwind CSS with dark mode support
- **Charts**: Recharts for data visualization
- **Icons**: Lucide React icons
- **Animations**: Framer Motion for smooth transitions

### Backend Integration
- **API Client**: Axios with interceptors
- **Real-time Data**: Server-Sent Events (SSE)
- **Data Types**: Comprehensive TypeScript definitions
- **Error Handling**: Graceful error boundaries and retry logic

### Development Tools
- **Language**: TypeScript for type safety
- **Linting**: ESLint with Next.js configuration
- **Formatting**: Prettier for code consistency
- **Build**: Next.js build system with SWC

## 🎨 Design System

### Color Palette
- **Primary**: Blue (#3b82f6) - Main brand color
- **Bull**: Red (#ff6b6b) - Stock price increases
- **Bear**: Blue (#4ecdc4) - Stock price decreases
- **Neutral**: Gray (#95a5a6) - Unchanged values
- **Accent**: Blue (#3498db) - Highlights and CTAs

### Typography
- **Primary Font**: Inter (Google Fonts)
- **Monospace**: JetBrains Mono for numbers and code

### Components
- **Cards**: Flexible container components
- **Tables**: Responsive data tables with sorting
- **Buttons**: Multiple variants and sizes
- **Forms**: Accessible form inputs and controls
- **Loading States**: Skeleton loaders and spinners

## 📊 Supported Stocks

The dashboard tracks 20 major NASDAQ technology stocks:
- AAPL (Apple Inc.)
- MSFT (Microsoft Corporation)
- GOOGL (Alphabet Inc.)
- AMZN (Amazon.com Inc.)
- META (Meta Platforms Inc.)
- NVDA (NVIDIA Corporation)
- TSLA (Tesla Inc.)
- AVGO (Broadcom Inc.)
- CRM (Salesforce Inc.)
- ORCL (Oracle Corporation)
- NFLX (Netflix Inc.)
- ADBE (Adobe Inc.)
- AMD (Advanced Micro Devices)
- INTC (Intel Corporation)
- PYPL (PayPal Holdings)
- CSCO (Cisco Systems)
- QCOM (QUALCOMM Inc.)
- TXN (Texas Instruments)
- AMAT (Applied Materials)
- PLTR (Palantir Technologies)

## 🚦 Getting Started

### Prerequisites
- Node.js 18+ (LTS recommended, tested up to v23.x)
- npm 9+ (required for Next.js 15+, recommended npm 10+)
- Git for version control

### Version Compatibility
- **Node.js**: 18.x, 20.x, 22.x, 23.x ✅
- **npm**: 9.x, 10.x ✅ (npm 8.x may have dependency resolution issues)
- **Yarn**: 1.22+ or 4.x ✅
- **pnpm**: 8.x+ ✅

### Installation

1. **Check your environment**
   ```bash
   node --version  # Should be 18.x or higher
   npm --version   # Should be 9.x or higher
   ```

2. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd gon-stock-dashboard
   ```

3. **Install dependencies**
   ```bash
   npm install
   # or for yarn users
   yarn install
   # or for pnpm users
   pnpm install
   ```

3. **Set up environment variables**
   ```bash
   cp .env.local.example .env.local
   ```
   
   Edit `.env.local` with your configuration:
   ```
   NEXT_PUBLIC_API_BASE_URL=https://api.hwangonjang.com
   NEXT_PUBLIC_APP_NAME=Gon Stock Dashboard
   NEXT_PUBLIC_APP_VERSION=1.0.0
   ```

4. **Run the development server**
   ```bash
   npm run dev
   # or
   yarn dev
   ```

5. **Open your browser**
   Navigate to [http://localhost:3000](http://localhost:3000)

## 🏗️ Project Structure

```
gon-stock-dashboard/
├── public/                 # Static assets
│   ├── images/            # Image assets
│   └── icons/             # App icons
├── src/
│   ├── components/        # React components
│   │   ├── ui/           # Base UI components
│   │   ├── layout/       # Layout components
│   │   ├── dashboard/    # Dashboard-specific components
│   │   └── charts/       # Chart components
│   ├── hooks/            # Custom React hooks
│   ├── lib/              # Utilities and configurations
│   │   ├── api/          # API client and services
│   │   ├── constants/    # App constants
│   │   └── utils.ts      # Utility functions
│   ├── pages/            # Next.js pages
│   ├── store/            # Zustand store definitions
│   ├── styles/           # Global styles
│   └── types/            # TypeScript type definitions
├── .env.local            # Environment variables
├── next.config.js        # Next.js configuration
├── tailwind.config.js    # Tailwind CSS configuration
└── tsconfig.json         # TypeScript configuration
```

## 📱 Features by Page

### Dashboard (`/`)
- Market overview with key statistics
- Top stock recommendations
- Complete stock table with real-time prices
- Search and filtering capabilities
- Responsive design for all devices

### Stock Detail (`/stocks/[symbol]`)
- Real-time price updates
- Interactive price charts
- Company information and metrics
- Financial statements and ratios
- Related news with sentiment analysis
- Investment recommendations

## 🔧 Configuration

### API Integration
The application connects to the Gon Stock API for:
- Stock basic information
- Company overviews and financials
- Real-time price data via SSE
- News and sentiment analysis
- Investment recommendations

### Environment Variables
- `NEXT_PUBLIC_API_BASE_URL`: Backend API URL
- `NEXT_PUBLIC_APP_NAME`: Application name
- `NEXT_PUBLIC_APP_VERSION`: Version number

### Theme Configuration
- Supports automatic dark/light mode detection
- Manual theme switching
- Persistent theme preferences
- Custom color schemes

## 🎯 Performance Optimizations

### Code Splitting
- Dynamic imports for charts and heavy components
- Route-based code splitting with Next.js
- Lazy loading for images and non-critical content

### Data Management
- Efficient state management with Zustand
- Local storage for user preferences
- Debounced search and filtering
- Optimistic updates for better UX

### Real-time Updates
- SSE connections with automatic reconnection
- Throttled updates to prevent excessive re-renders
- Connection status indicators

## 🔒 Security

- Input validation and sanitization
- XSS protection with Content Security Policy
- HTTPS enforcement
- Rate limiting for API calls
- Error boundary protection

## 🧪 Testing

### Development Testing
```bash
npm run lint          # ESLint checks
npm run lint:fix      # Fix ESLint issues
npm run type-check    # TypeScript validation
npm run format        # Format code with Prettier
npm run format:check  # Check code formatting
npm run build         # Production build test
npm run analyze       # Bundle size analysis
```

### Browser Testing
- Chrome/Chromium (recommended)
- Firefox
- Safari
- Edge

### Device Testing
- Desktop (1920x1080+)
- Tablet (768px - 1024px)
- Mobile (320px - 768px)

## 📦 Build and Deployment

### Production Build
```bash
npm run build
npm run start
```

### Docker Deployment
```bash
# Build Docker image
docker build -t gon-stock-dashboard .

# Run container
docker run -p 3000:3000 gon-stock-dashboard
```

### Static Export
```bash
npm run build
npm run export
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow TypeScript best practices
- Use Tailwind CSS for styling
- Write responsive, accessible components
- Include proper error handling
- Test on multiple devices and browsers

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Toss Securities** - Design inspiration
- **Finnhub API** - Stock market data
- **Recharts** - Chart library
- **Tailwind CSS** - Utility-first CSS framework
- **Next.js** - React framework
- **Vercel** - Deployment platform

## 📞 Support

For support, email support@gonstockdashboard.com or open an issue on GitHub.

## 🗺️ Roadmap

### Phase 1 (Current)
- ✅ Basic dashboard functionality
- ✅ Real-time price updates
- ✅ Stock detail pages
- ✅ Responsive design

### Phase 2 (Next)
- [ ] Advanced charting features
- [ ] Portfolio tracking
- [ ] Price alerts
- [ ] Export functionality

### Phase 3 (Future)
- [ ] Social features
- [ ] Advanced analytics
- [ ] Mobile app
- [ ] API for third-party integrations

---

Built with ❤️ by the Gon Stock Dashboard team