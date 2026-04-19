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
    <div className="flex items-start justify-between py-4 border-b border-border">
      <div>
        <p className="text-xs text-muted uppercase tracking-widest mb-1">Folio activo</p>
        <h2 className="font-mono text-2xl text-accent font-medium">{numeroFolio}</h2>
        <p className="text-xs text-muted mt-1">Actualizado: {formatDate(fechaActualizacion)}</p>
      </div>
      <div className="flex flex-col items-end gap-2">
        <StatusBadge estado={estado} />
        <span className="text-xs font-mono text-muted">v{version}</span>
      </div>
    </div>
  );
}
