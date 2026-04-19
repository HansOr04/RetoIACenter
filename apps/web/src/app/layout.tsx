import type { Metadata } from 'next';
import { Providers } from './providers';
import './globals.css';

export const metadata: Metadata = {
  title: 'Cotizador — Seguros de Daños',
  description: 'Sistema de cotización de seguros comerciales',
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="es">
      <body className="min-h-screen antialiased" style={{ backgroundColor: '#0A0A0F', color: '#F5F5F0' }}>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
