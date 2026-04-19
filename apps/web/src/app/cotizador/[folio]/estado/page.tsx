'use client';
import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { FolioHeader } from '@/components/FolioHeader';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';

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
      <div style={{ marginTop: '8px' }}>
        <FolioHeader
          numeroFolio={numeroFolio}
          estado={estadoCotizacion}
          version={version}
          fechaActualizacion={fechaUltimaActualizacion}
        />

        {/* ── Metrics ── */}
        <div
          className="grid grid-cols-1 md:grid-cols-3"
          style={{ gap: '16px', marginTop: '32px' }}
        >
          {/* Progreso */}
          <Card style={{ padding: '24px' }}>
            <p className="metric-label">Progreso</p>
            <div className="metric-value">
              {porcentajeProgreso}
              <span className="metric-value-muted" style={{ fontSize: '1.5rem' }}>%</span>
            </div>
            <div className="progress-track">
              <div
                className="progress-fill"
                style={{ width: `${porcentajeProgreso}%` }}
              />
            </div>
          </Card>

          {/* Calculable */}
          <Card style={{ padding: '24px' }}>
            <p className="metric-label">Calculable</p>
            <div
              className="metric-value"
              style={{ color: esCalculable ? 'var(--accent)' : 'var(--muted)' }}
            >
              {esCalculable ? 'Sí' : 'No'}
            </div>
            <p
              style={{
                fontSize: '0.75rem',
                color: 'var(--muted)',
                marginTop: '16px',
                lineHeight: 1.5,
              }}
            >
              {esCalculable
                ? 'Listo para calcular prima'
                : 'Completa las secciones pendientes'}
            </p>
          </Card>

          {/* Secciones */}
          <Card style={{ padding: '24px' }}>
            <p className="metric-label">Secciones</p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '8px' }}>
              {Object.entries(seccionesCompletadas).map(([k, v]) => (
                <div key={k} className="flex justify-between items-center">
                  <span
                    className="mono-display"
                    style={{ fontSize: '0.75rem', color: 'var(--muted)', letterSpacing: '0.02em' }}
                  >
                    {k}
                  </span>
                  <span
                    className="mono-display"
                    style={{
                      fontSize: '0.75rem',
                      color: v ? 'var(--accent)' : 'var(--border-2)',
                    }}
                  >
                    {v ? '✓' : '○'}
                  </span>
                </div>
              ))}
            </div>
          </Card>
        </div>

        {/* ── Alertas ── */}
        {alertas && alertas.length > 0 && (
          <Card variant="warning" style={{ marginTop: '24px', padding: '20px' }}>
            <p className="metric-label" style={{ color: 'var(--warning)', marginBottom: '12px' }}>
              Pendiente
            </p>
            <ul style={{ display: 'flex', flexDirection: 'column', gap: '8px', listStyle: 'none', padding: 0, margin: 0 }}>
              {alertas.map((a) => (
                <li key={a.mensaje} className="flex gap-3" style={{ fontSize: '0.8125rem', color: 'var(--cream)' }}>
                  <span style={{ color: 'var(--warning)', flexShrink: 0 }}>!</span>
                  <span>{a.mensaje}</span>
                </li>
              ))}
            </ul>
          </Card>
        )}

        {/* ── Actions ── */}
        <div className="flex gap-4 flex-wrap" style={{ marginTop: '32px' }}>
          <Button
            onClick={() => router.push(`/cotizador/${folio}/datos-generales`)}
            id="btn-continuar-cotizacion"
          >
            Continuar cotización →
          </Button>
          {esCalculable && (
            <Button
              variant="outline"
              onClick={() => router.push(`/cotizador/${folio}/calculo`)}
              id="btn-ir-calculo"
            >
              Ir al cálculo
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}

function LoadingState() {
  return (
    <div
      className="flex items-center gap-3"
      style={{ padding: '80px 0', fontFamily: 'var(--font-mono)', fontSize: '0.8125rem', color: 'var(--muted)' }}
    >
      <span style={{ animation: 'pulse 1.5s cubic-bezier(0.4,0,0.6,1) infinite' }}>▊</span>
      Cargando folio…
    </div>
  );
}

function ErrorState({ msg }: Readonly<{ msg: string }>) {
  return (
    <Card variant="danger" style={{ padding: '24px', marginTop: '40px' }}>
      <p className="mono-display" style={{ fontSize: '0.8125rem', color: 'var(--danger)' }}>
        {msg}
      </p>
      <a
        href="/"
        className="nav-link"
        style={{ display: 'block', marginTop: '12px', fontSize: '0.75rem' }}
        id="link-error-volver"
      >
        ← Volver al inicio
      </a>
    </Card>
  );
}
