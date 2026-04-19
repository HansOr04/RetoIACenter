type Estado = 'BORRADOR' | 'CALCULADO' | 'EMITIDO' | 'CANCELADO';

const config: Record<Estado, { label: string; mod: string }> = {
  BORRADOR:  { label: 'Borrador',  mod: 'badge-warning' },
  CALCULADO: { label: 'Calculado', mod: 'badge-accent' },
  EMITIDO:   { label: 'Emitido',   mod: 'badge-cream' },
  CANCELADO: { label: 'Cancelado', mod: 'badge-danger' },
};

export function StatusBadge({ estado }: Readonly<{ estado: string }>) {
  const c = config[estado as Estado] ?? { label: estado, mod: 'badge-muted' };
  return (
    <span className={`badge ${c.mod}`}>
      <span className="badge-dot" />
      {c.label}
    </span>
  );
}
