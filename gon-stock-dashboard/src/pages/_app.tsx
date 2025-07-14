import type { AppProps } from 'next/app';
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { ThemeProvider } from 'next-themes';
import '@/styles/globals.css';

export default function App({ Component, pageProps }: AppProps) {
  const router = useRouter();

  // Handle route changes and page load events
  useEffect(() => {
    const handleRouteChange = () => {
      // Add any route change analytics or logging here
    };

    const handleRouteChangeComplete = () => {
      // Add any completion analytics or logging here
    };

    router.events.on('routeChangeStart', handleRouteChange);
    router.events.on('routeChangeComplete', handleRouteChangeComplete);

    return () => {
      router.events.off('routeChangeStart', handleRouteChange);
      router.events.off('routeChangeComplete', handleRouteChangeComplete);
    };
  }, [router]);

  return (
    <ThemeProvider
      attribute="class"
      defaultTheme="system"
      storageKey="gon-stock-theme"
      themes={['light', 'dark', 'system']}
      enableSystem={true}
      disableTransitionOnChange={false}
    >
      <Component {...pageProps} />
    </ThemeProvider>
  );
}