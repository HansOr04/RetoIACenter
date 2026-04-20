'use client';

import { useRouter } from 'next/navigation';
import { useParams } from 'next/navigation';

type Step = { id: number; label: string; sublabel: string; path: string };

const STEPS: Step[] = [
  { id: 1, label: 'Folio',       sublabel: 'Crear cotización',  path: '' },
  { id: 2, label: 'Datos',       sublabel: 'Info del tomador',  path: 'datos-generales' },
  { id: 3, label: 'Layout',      sublabel: 'N° ubicaciones',    path: 'layout' },
  { id: 4, label: 'Ubicaciones', sublabel: 'Propiedades',       path: 'ubicaciones' },
  { id: 5, label: 'Coberturas',  sublabel: 'Riesgos',           path: 'coberturas' },
  { id: 6, label: 'Cálculo',     sublabel: 'Prima neta',        path: 'calculo' },
];

const progressPct = (current: number) =>
  Math.round(((current - 1) / (STEPS.length - 1)) * 100);

export function StepIndicator({ current }: Readonly<{ current: number }>) {
  const router = useRouter();
  const params = useParams<{ folio: string }>();
  const folio = params?.folio ?? '';

  const handleStepClick = (step: Step) => {
    if (step.id < current && folio) {
      const base = `/cotizador/${folio}`;
      router.push(step.path ? `${base}/${step.path}` : base);
    }
  };

  return (
    <div>
      {/* ── Animated progress bar ── */}
      <div
        role="progressbar"
        aria-valuenow={progressPct(current)}
        aria-valuemin={0}
        aria-valuemax={100}
        style={{
          height: '3px',
          marginBottom: '12px',
          borderRadius: '2px',
          backgroundColor: 'var(--border)',
          overflow: 'hidden',
        }}
      >
        <div
          style={{
            height: '100%',
            width: `${progressPct(current)}%`,
            background: 'linear-gradient(90deg, var(--accent), var(--cream))',
            transition: 'width 500ms cubic-bezier(0.4,0,0.2,1)',
            borderRadius: '2px',
          }}
        />
      </div>

      {/* ── Step circles ── */}
      <div className="flex items-start gap-0">
        {STEPS.map((step, i) => {
          const done   = current > step.id;
          const active = current === step.id;
          const clickable = done && folio;

          return (
            <div key={step.id} className="flex items-start flex-1 min-w-0">
              <div className="flex flex-col items-center flex-1">
                <button
                  onClick={() => handleStepClick(step)}
                  disabled={!clickable}
                  title={step.sublabel}
                  aria-current={active ? 'step' : undefined}
                  aria-label={`Paso ${step.id}: ${step.label} — ${step.sublabel}`}
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
                      done   ? 'is-done'   : '',
                      active ? 'is-active' : '',
                    ].filter(Boolean).join(' ')}
                    style={{
                      transform: done ? 'scale(1.1)' : 'scale(1)',
                      transition: 'transform 200ms ease',
                    }}
                  >
                    {done ? (
                      <svg width="10" height="10" viewBox="0 0 12 12" fill="none">
                        <path d="M2 6l3 3 5-5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
                      </svg>
                    ) : (
                      step.id
                    )}
                  </div>
                </button>
                <div className="mt-2 text-center">
                  <div
                    style={{
                      fontSize: '0.75rem',
                      fontWeight: 500,
                      color: active ? 'var(--cream)' : done ? 'var(--accent)' : 'var(--muted)',
                      transition: 'color 200ms ease',
                    }}
                  >
                    {step.label}
                  </div>
                  <div
                    className="hidden sm:block"
                    style={{ fontSize: '0.6875rem', color: 'var(--muted)', marginTop: 2 }}
                  >
                    {step.sublabel}
                  </div>
                </div>
              </div>
              {i < STEPS.length - 1 && (
                <div
                  className="h-px w-full mt-4"
                  style={{
                    backgroundColor: done ? 'var(--accent)' : 'var(--border)',
                    transition: 'background-color 500ms ease',
                  }}
                />
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}


