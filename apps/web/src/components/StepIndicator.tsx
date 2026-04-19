type Step = { id: number; label: string; sublabel: string };

const STEPS: Step[] = [
  { id: 1, label: 'Folio',       sublabel: 'Crear cotización' },
  { id: 2, label: 'Datos',       sublabel: 'Info del tomador' },
  { id: 3, label: 'Layout',      sublabel: 'N° ubicaciones' },
  { id: 4, label: 'Ubicaciones', sublabel: 'Propiedades' },
  { id: 5, label: 'Coberturas',  sublabel: 'Riesgos' },
  { id: 6, label: 'Cálculo',     sublabel: 'Prima neta' },
];

export function StepIndicator({ current }: Readonly<{ current: number }>) {
  return (
    <div className="flex items-start gap-0">
      {STEPS.map((step, i) => {
        const done = current > step.id;
        const active = current === step.id;
        return (
          <div key={step.id} className="flex items-start flex-1 min-w-0">
            <div className="flex flex-col items-center flex-1">
              <div
                className={[
                  'step-circle',
                  done ? 'is-done' : '',
                  active ? 'is-active' : '',
                ].filter(Boolean).join(' ')}
              >
                {done ? (
                  <svg width="10" height="10" viewBox="0 0 12 12" fill="none">
                    <path d="M2 6l3 3 5-5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                ) : (
                  step.id
                )}
              </div>
              <div className="mt-2 text-center">
                <div
                  style={{
                    fontSize: '0.75rem',
                    fontWeight: 500,
                    color: active ? 'var(--cream)' : done ? 'var(--accent)' : 'var(--muted)',
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
                className={`h-px w-full mt-4 transition-all duration-500 ${done ? '' : ''}`}
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
  );
}
