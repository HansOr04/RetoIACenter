export default function CotizadorLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <div className="min-h-screen" style={{ backgroundColor: '#0A0A0F' }}>
      <header className="border-b px-8 py-4" style={{ borderColor: '#1E1E2A' }}>
        <div className="max-w-5xl mx-auto">
          <div className="flex justify-between items-center mb-6">
            <a
              href="/"
              className="font-serif transition-colors"
              style={{ color: '#F5F5F0' }}
              onMouseEnter={e => (e.currentTarget.style.color = '#00D9A3')}
              onMouseLeave={e => (e.currentTarget.style.color = '#F5F5F0')}
            >
              ← Cotizador
            </a>
            <span className="font-mono text-xs" style={{ color: '#6B6B7A' }}>Seguros de Daños</span>
          </div>
        </div>
      </header>
      <main className="max-w-5xl mx-auto px-8 py-10">
        {children}
      </main>
    </div>
  );
}
