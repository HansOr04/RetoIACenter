'use client';
import { usePathname, useRouter } from 'next/navigation';

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
  const stepSlugs = STEPS.map(s => s.path).filter(Boolean);
  if (!stepSlugs.includes(last)) return 1;
  const match = STEPS.find(s => s.path === last);
  return match ? match.id : 1;
}

function extractFolio(pathname: string): string {
  const parts = pathname.split('/').filter(Boolean);
  const idx = parts.indexOf('cotizador');
  return idx !== -1 && parts[idx + 1] ? parts[idx + 1] : '';
}

export default function CotizadorLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const pathname = usePathname();
  const router = useRouter();
  const currentStep = resolveCurrentStep(pathname);
  const folio = extractFolio(pathname);

  const navigateToStep = (step: typeof STEPS[number]) => {
    if (step.id < currentStep && folio) {
      const base = `/cotizador/${folio}`;
      router.push(step.path ? `${base}/${step.path}` : base);
    }
  };

  const progressPct = Math.round(((currentStep - 1) / (STEPS.length - 1)) * 100);

  return (
    <div className="flex min-h-screen">
      {/* ── Sidebar ── */}
      <aside
        className="hidden md:flex flex-col fixed top-0 left-0 h-full"
        style={{
          width: '216px',
          flexShrink: 0,
          backgroundColor: 'var(--surface)',
          borderRight: '1px solid var(--border)',
        }}
      >
        {/* Logo / Folio */}
        <div style={{ padding: '24px 20px', borderBottom: '1px solid var(--border)' }}>
          <p
            style={{
              fontSize: '0.6875rem',
              fontWeight: 600,
              letterSpacing: '0.1em',
              textTransform: 'uppercase',
              color: 'var(--muted)',
              marginBottom: '6px',
            }}
          >
            Cotizador
          </p>
          {folio && (
            <p
              className="mono-display"
              style={{ fontSize: '0.8125rem', color: 'var(--accent)', letterSpacing: '0.02em' }}
            >
              {folio}
            </p>
          )}
        </div>

        {/* Progress bar */}
        <div style={{ padding: '12px 16px 0' }}>
          <div
            role="progressbar"
            aria-valuenow={progressPct}
            aria-valuemin={0}
            aria-valuemax={100}
            aria-label="Progreso de la cotización"
            style={{
              height: '3px',
              borderRadius: '2px',
              backgroundColor: 'var(--border)',
              overflow: 'hidden',
            }}
          >
            <div
              style={{
                height: '100%',
                width: `${progressPct}%`,
                background: 'linear-gradient(90deg, var(--accent), var(--cream))',
                transition: 'width 500ms cubic-bezier(0.4,0,0.2,1)',
                borderRadius: '2px',
              }}
            />
          </div>
          <p style={{ fontSize: '0.625rem', color: 'var(--muted)', marginTop: '4px', textAlign: 'right' }}>
            {progressPct}% completado
          </p>
        </div>

        {/* Steps nav */}
        <nav style={{ flex: 1, padding: '12px 16px', overflowY: 'auto' }}>
          <ol style={{ position: 'relative', listStyle: 'none', padding: 0, margin: 0 }}>
            {STEPS.map((step, i) => {
              const done = currentStep > step.id;
              const active = currentStep === step.id;
              const isLast = i === STEPS.length - 1;
              const clickable = done && folio;

              return (
                <li key={step.id} style={{ position: 'relative', display: 'flex', gap: '12px' }}>
                  {/* Vertical connector */}
                  {!isLast && (
                    <div
                      style={{
                        position: 'absolute',
                        left: '13px',
                        top: '28px',
                        width: '1px',
                        height: 'calc(100% - 8px)',
                        backgroundColor: done ? 'var(--accent)' : 'var(--border)',
                        transition: 'background-color 300ms ease',
                      }}
                    />
                  )}

                  {/* Circle */}
                  <div style={{ flexShrink: 0, position: 'relative', zIndex: 1, marginTop: '2px' }}>
                    <button
                      onClick={() => navigateToStep(step)}
                      disabled={!clickable}
                      title={clickable ? `Volver a: ${step.label}` : step.sub}
                      aria-current={active ? 'step' : undefined}
                      style={{
                        background: 'none',
                        border: 'none',
                        padding: 0,
                        cursor: clickable ? 'pointer' : 'default',
                      }}
                    >
                      <div
                        className={[
                          'step-circle',
                          done ? 'is-done' : '',
                          active ? 'is-active' : '',
                        ].filter(Boolean).join(' ')}
                        style={{
                          transform: done ? 'scale(1.05)' : 'scale(1)',
                          transition: 'transform 200ms ease',
                        }}
                      >
                        {done ? (
                          <svg width="10" height="10" viewBox="0 0 12 12" fill="none">
                            <path
                              d="M2 6l3 3 5-5"
                              stroke="currentColor"
                              strokeWidth="1.8"
                              strokeLinecap="round"
                              strokeLinejoin="round"
                            />
                          </svg>
                        ) : (
                          step.id
                        )}
                      </div>
                    </button>
                  </div>

                  {/* Labels */}
                  <div
                    style={{ paddingBottom: isLast ? 0 : '28px', cursor: clickable ? 'pointer' : 'default' }}
                    onClick={() => navigateToStep(step)}
                  >
                    <p
                      style={{
                        fontSize: '0.8125rem',
                        fontWeight: 500,
                        lineHeight: 1.3,
                        color: active ? 'var(--cream)' : done ? 'var(--accent)' : 'var(--muted)',
                        transition: 'color 200ms ease',
                      }}
                    >
                      {step.label}
                    </p>
                    <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: '2px' }}>
                      {step.sub}
                    </p>
                  </div>
                </li>
              );
            })}
          </ol>
        </nav>

        {/* Footer */}
        <div style={{ padding: '20px', borderTop: '1px solid var(--border)' }}>
          <a href="/" className="nav-link" id="link-volver-inicio">
            ← Volver al inicio
          </a>
        </div>
      </aside>

      {/* ── Main content ── */}
      <main
        className="flex-1 overflow-y-auto"
        style={{ marginLeft: '216px' }}
      >
        <div style={{ padding: '40px 40px', maxWidth: '800px' }}>
          {children}
        </div>
      </main>
    </div>
  );
}
