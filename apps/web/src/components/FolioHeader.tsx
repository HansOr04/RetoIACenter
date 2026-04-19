import { StatusBadge } from './StatusBadge';
import { formatDate } from '@/lib/utils';

interface Props {
  numeroFolio: string;
  estado: string;
  version: number;
  fechaActualizacion: string;
}

export function FolioHeader({ numeroFolio, estado, version, fechaActualizacion }: Readonly<Props>) {
  return (
    <div className="flex items-start justify-between py-4 border-b" style={{ borderColor: 'var(--border)' }}>
      <div>
        <p className="metric-label" style={{ marginBottom: 6 }}>Folio activo</p>
        <h2
          className="mono-display"
          style={{ fontSize: '1.375rem', color: 'var(--accent)', letterSpacing: '-0.02em' }}
        >
          {numeroFolio}
        </h2>
        <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: 4 }}>
          Actualizado: {formatDate(fechaActualizacion)}
        </p>
      </div>
      <div className="flex flex-col items-end gap-2">
        <StatusBadge estado={estado} />
        <span className="mono-display" style={{ fontSize: '0.75rem', color: 'var(--muted)' }}>
          v{version}
        </span>
      </div>
    </div>
  );
}
