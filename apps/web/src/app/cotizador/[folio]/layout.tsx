'use client';
import { usePathname } from 'next/navigation';

const STEPS = [
  { id: 1, label: 'Folio',       sub: 'Crear cotización', path: '' },
  { id: 2, label: 'Datos',       sub: 'Info del tomador', path: 'datos-generales' },
  { id: 3, label: 'Layout',      sub: 'N° ubicaciones',   path: 'layout' },
  { id: 4, label: 'Ubicaciones', sub: 'Propiedades',      path: 'ubicaciones' },
  { id: 5, label: 'Coberturas',  sub: 'Riesgos',          path: 'coberturas' },
  { id: 6, label: 'Cálculo',     sub: 'Prima neta',       path: 'calculo' },
];

function resolveCurrentStep(pathname: string): number {
  const segments = pathname.split('/').filter(Boolean);
  const last = segments[segments.length - 1] ?? '';
  // If last segment looks like a folio (not a known step slug), we are at step 1
  const stepSlugs = STEPS.map(s => s.path).filter(Boolean);
  if (!stepSlugs.includes(last)) return 1;
  const match = STEPS.find(s => s.path === last);
  return match ? match.id : 1;
}

function extractFolio(pathname: string): string {
  // pathname: /cotizador/<folio>/...
  const parts = pathname.split('/').filter(Boolean);
  const idx = parts.indexOf('cotizador');
  return idx !== -1 && parts[idx + 1] ? parts[idx + 1] : '';
}

export default function CotizadorLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const pathname = usePathname();
  const currentStep = resolveCurrentStep(pathname);
  const folio = extractFolio(pathname);

  return (
    <div className="flex min-h-screen" style={{ backgroundColor: '#0A0A0F' }}>
      {/* Sidebar */}
      <aside
        className="hidden md:flex flex-col w-56 flex-shrink-0 fixed top-0 left-0 h-full border-r"
        style={{ backgroundColor: '#0D0D13', borderColor: '#1E1E2A' }}
      >
        {/* Header */}
        <div className="px-5 py-6 border-b" style={{ borderColor: '#1E1E2A' }}>
          <p
            className="text-xs font-semibold uppercase tracking-widest mb-1"
            style={{ color: '#6B6B7A' }}
          >
            Cotizador
          </p>
          {folio && (
            <p className="font-mono text-sm" style={{ color: '#00D9A3' }}>
              {folio}
            </p>
          )}
        </div>

        {/* Steps */}
        <nav className="flex-1 px-4 py-6 overflow-y-auto">
          <ol className="relative">
            {STEPS.map((step, i) => {
              const done = currentStep > step.id;
              const active = currentStep === step.id;
              const isLast = i === STEPS.length - 1;

              return (
                <li key={step.id} className="relative flex gap-3 pb-0">
                  {/* Connector line */}
                  {!isLast && (
                    <div
                      className="absolute left-3.5 top-8 w-px"
                      style={{
                        height: 'calc(100% - 8px)',
                        backgroundColor: done ? '#00D9A3' : '#1E1E2A',
                      }}
                    />
                  )}

                  {/* Circle */}
                  <div className="flex-shrink-0 relative z-10 mt-1">
                    <div
                      className="w-7 h-7 rounded-full border flex items-center justify-center text-xs font-semibold transition-all"
                      style={
                        done
                          ? { backgroundColor: '#00D9A3', borderColor: '#00D9A3', color: '#0A0A0F' }
                          : active
                          ? { backgroundColor: 'transparent', borderColor: '#00D9A3', color: '#00D9A3' }
                          : { backgroundColor: 'transparent', borderColor: '#2A2A3A', color: '#6B6B7A' }
                      }
                    >
                      {done ? (
                        <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                          <path d="M2 6l3 3 5-5" stroke="#0A0A0F" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
                        </svg>
                      ) : (
                        step.id
                      )}
                    </div>
                  </div>

                  {/* Labels */}
                  <div className={`pb-7 ${isLast ? 'pb-0' : ''}`}>
                    <p
                      className="text-sm font-medium leading-tight"
                      style={{ color: active ? '#F5F5F0' : done ? '#00D9A3' : '#6B6B7A' }}
                    >
                      {step.label}
                    </p>
                    <p className="text-xs mt-0.5" style={{ color: '#6B6B7A' }}>
                      {step.sub}
                    </p>
                  </div>
                </li>
              );
            })}
          </ol>
        </nav>

        {/* Footer */}
        <div className="px-5 py-5 border-t" style={{ borderColor: '#1E1E2A' }}>
          <a
            href="/"
            className="text-xs transition-colors"
            style={{ color: '#6B6B7A' }}
            onMouseEnter={e => (e.currentTarget.style.color = '#F5F5F0')}
            onMouseLeave={e => (e.currentTarget.style.color = '#6B6B7A')}
          >
            ← Volver al inicio
          </a>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 md:ml-56 overflow-y-auto">
        <div className="px-10 py-10 max-w-3xl">
          {children}
        </div>
      </main>
    </div>
  );
}
