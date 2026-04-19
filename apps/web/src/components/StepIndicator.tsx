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
                className={`
                  w-8 h-8 rounded-full border flex items-center justify-center
                  font-mono text-xs font-medium transition-all duration-300
                  ${done   ? 'bg-accent border-accent text-bg' : ''}
                  ${active ? 'bg-surface border-accent text-accent' : ''}
                  ${!done && !active ? 'bg-surface border-border text-muted' : ''}
                `}
              >
                {done ? '✓' : step.id}
              </div>
              <div className="mt-2 text-center">
                <div
                  className={`text-xs font-medium ${active ? 'text-cream' : done ? 'text-accent' : 'text-muted'}`}
                >
                  {step.label}
                </div>
                <div className="text-xs text-muted hidden sm:block">{step.sublabel}</div>
              </div>
            </div>
            {i < STEPS.length - 1 && (
              <div
                className={`h-px w-full mt-4 transition-all duration-500 ${done ? 'bg-accent' : 'bg-border'}`}
              />
            )}
          </div>
        );
      })}
    </div>
  );
}
