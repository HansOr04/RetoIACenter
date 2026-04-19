'use client';
import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { FolioHeader } from '@/components/FolioHeader';

interface EstadoData {
  numeroFolio: string;
  estadoCotizacion: string;
  version: number;
  fechaUltimaActualizacion: string;
  porcentajeProgreso: number;
  esCalculable: boolean;
  alertas?: Array<{ mensaje: string }>;
  seccionesCompletadas: Record<string, boolean>;
}

export default function EstadoPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [data, setData] = useState<EstadoData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    foliosApi.getEstado(folio)
      .then(d => setData(d as EstadoData))
      .catch((e: { detail?: string }) => setError(e.detail || 'Folio no encontrado'))
      .finally(() => setLoading(false));
  }, [folio]);

  if (loading) return <LoadingState />;
  if (error || !data) return <ErrorState msg={error} />;

  const {
    numeroFolio, estadoCotizacion, version, fechaUltimaActualizacion,
    porcentajeProgreso, esCalculable, alertas, seccionesCompletadas,
  } = data;

  return (
    <div>
      <div className="mt-2">
        <FolioHeader
          numeroFolio={numeroFolio}
          estado={estadoCotizacion}
          version={version}
          fechaActualizacion={fechaUltimaActualizacion}
        />

        <div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="border p-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
            <p className="text-xs uppercase tracking-widest mb-3" style={{ color: '#6B6B7A' }}>Progreso</p>
            <div className="font-mono text-5xl font-medium" style={{ color: '#F5F5F0' }}>
              {porcentajeProgreso}
              <span className="text-2xl" style={{ color: '#6B6B7A' }}>%</span>
            </div>
            <div className="mt-4 h-1 rounded" style={{ backgroundColor: '#1E1E2A' }}>
              <div
                className="h-1 rounded transition-all duration-700"
                style={{ width: `${porcentajeProgreso}%`, backgroundColor: '#00D9A3' }}
              />
            </div>
          </div>

          <div className="border p-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
            <p className="text-xs uppercase tracking-widest mb-3" style={{ color: '#6B6B7A' }}>Calculable</p>
            <div
              className="font-mono text-5xl font-medium"
              style={{ color: esCalculable ? '#00D9A3' : '#6B6B7A' }}
            >
              {esCalculable ? 'Sí' : 'No'}
            </div>
            <p className="text-xs mt-4" style={{ color: '#6B6B7A' }}>
              {esCalculable ? 'Listo para calcular prima' : 'Completa las secciones pendientes'}
            </p>
          </div>

          <div className="border p-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
            <p className="text-xs uppercase tracking-widest mb-3" style={{ color: '#6B6B7A' }}>Secciones</p>
            <div className="space-y-2 mt-2">
              {Object.entries(seccionesCompletadas).map(([k, v]) => (
                <div key={k} className="flex justify-between items-center">
                  <span className="text-xs font-mono" style={{ color: '#6B6B7A' }}>{k}</span>
                  <span className="text-xs font-mono" style={{ color: v ? '#00D9A3' : '#1E1E2A' }}>
                    {v ? '✓' : '○'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {alertas && alertas.length > 0 && (
          <div
            className="mt-6 border p-5"
            style={{ borderColor: 'rgba(245,166,35,0.2)', backgroundColor: 'rgba(245,166,35,0.05)' }}
          >
            <p className="text-xs uppercase tracking-widest mb-3" style={{ color: '#F5A623' }}>Pendiente</p>
            <ul className="space-y-2">
              {alertas.map((a) => (
                <li key={a.mensaje} className="flex gap-3 text-sm" style={{ color: 'rgba(245,245,240,0.7)' }}>
                  <span style={{ color: '#F5A623' }}>!</span>
                  <span>{a.mensaje}</span>
                </li>
              ))}
            </ul>
          </div>
        )}

        <div className="mt-8 flex gap-4 flex-wrap">
          <button
            onClick={() => router.push(`/cotizador/${folio}/datos-generales`)}
            className="font-medium px-6 py-3 text-sm transition-colors"
            style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
            onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
            onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
          >
            Continuar cotización →
          </button>
          {esCalculable && (
            <button
              onClick={() => router.push(`/cotizador/${folio}/calculo`)}
              className="border px-6 py-3 text-sm transition-colors"
              style={{ borderColor: '#00D9A3', color: '#00D9A3' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = 'rgba(0,217,163,0.05)')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = 'transparent')}
            >
              Ir al cálculo
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

function LoadingState() {
  return (
    <div className="flex items-center gap-3 py-20 font-mono text-sm" style={{ color: '#6B6B7A' }}>
      <span className="animate-pulse">▊</span> Cargando folio...
    </div>
  );
}

function ErrorState({ msg }: Readonly<{ msg: string }>) {
  return (
    <div
      className="border p-6 mt-10"
      style={{ borderColor: 'rgba(255,77,77,0.3)', backgroundColor: 'rgba(255,77,77,0.05)', color: '#FF4D4D' }}
    >
      <p className="font-mono text-sm">{msg}</p>
      <a
        href="/"
        className="text-xs underline mt-3 block"
        style={{ color: '#6B6B7A' }}
        onMouseEnter={e => (e.currentTarget.style.color = '#F5F5F0')}
        onMouseLeave={e => (e.currentTarget.style.color = '#6B6B7A')}
      >
        ← Volver al inicio
      </a>
    </div>
  );
}
