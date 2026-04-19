type Estado = 'BORRADOR' | 'CALCULADO' | 'EMITIDO' | 'CANCELADO';

const config: Record<Estado, { label: string; color: string; dot: string }> = {
  BORRADOR:  { label: 'Borrador',  color: 'text-warning border-warning/30 bg-warning/5',  dot: 'bg-warning' },
  CALCULADO: { label: 'Calculado', color: 'text-accent border-accent/30 bg-accent/5',     dot: 'bg-accent animate-pulse-slow' },
  EMITIDO:   { label: 'Emitido',   color: 'text-cream border-cream/30 bg-cream/5',        dot: 'bg-cream' },
  CANCELADO: { label: 'Cancelado', color: 'text-danger border-danger/30 bg-danger/5',     dot: 'bg-danger' },
};

export function StatusBadge({ estado }: Readonly<{ estado: string }>) {
  const c = config[estado as Estado] ?? {
    label: estado,
    color: 'text-muted border-border bg-surface',
    dot: 'bg-muted',
  };
  return (
    <span
      className={`inline-flex items-center gap-1.5 px-2.5 py-1 text-xs font-mono border ${c.color} rounded`}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${c.dot}`} />
      {c.label}
    </span>
  );
}
