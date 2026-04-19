'use client';
import { useState } from 'react';
import { useParams } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { formatCurrency } from '@/lib/utils';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';

interface DesglosePorUbicacion {
  indice: number;
  calculada: boolean;
  total?: number;
  desglose?: Record<string, number>;
  alertas?: Array<{ codigo: string; mensaje: string }>;
}

interface CalculoResult {
  primaNeta: number;
  primaComercial: number;
  factorComercial: number;
  primasPorUbicacion?: DesglosePorUbicacion[];
}

export default function CalculoPage() {
  const { folio } = useParams<{ folio: string }>();
  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<CalculoResult | null>(null);
  const [error, setError] = useState<string>('');

  async function ejecutarCalculo() {
    setLoading(true);
    setError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      const res = await quotesApi.calcular(folio, estado.version);
      setResult(res as CalculoResult);
    } catch (err: unknown) {
      const e = err as { detail?: string; status?: number };
      setError(e.detail || `Error ${e.status ?? ''}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      {/* ── Page header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title">Cálculo de prima</h1>
        <p className="page-subtitle">
          Ejecuta el cálculo para obtener la prima neta y comercial.
        </p>
      </div>

      {!result ? (
        <div style={{ maxWidth: '480px' }}>
          <Card style={{ padding: '32px', marginBottom: '24px' }}>
            <p className="metric-label">Resumen</p>
            <p style={{ fontSize: '0.875rem', lineHeight: 1.7, color: 'var(--muted)' }}>
              El cálculo evaluará cada ubicación contra las coberturas configuradas
              y los catálogos de tarifas vigentes.
            </p>
          </Card>

          {error && (
            <div className="error-banner" style={{ marginBottom: '24px' }}>
              {error}
            </div>
          )}

          <Button
            onClick={ejecutarCalculo}
            loading={loading}
            full
            size="lg"
            id="btn-ejecutar-calculo"
          >
            {loading ? 'Calculando…' : 'Ejecutar cálculo →'}
          </Button>
        </div>
      ) : (
        <ResultadoCalculo
          result={result}
          onRecalcular={() => setResult(null)}
          folio={folio}
        />
      )}
    </div>
  );
}

function ResultadoCalculo({
  result,
  onRecalcular,
  folio,
}: Readonly<{ result: CalculoResult; onRecalcular: () => void; folio: string }>) {
  const { primaNeta, primaComercial, factorComercial, primasPorUbicacion } = result;

  return (
    <div>
      {/* ── Summary metrics ── */}
      <div
        className="grid grid-cols-1 md:grid-cols-3"
        style={{ gap: '16px', marginBottom: '40px' }}
      >
        <Card variant="accent" style={{ padding: '24px' }}>
          <p className="metric-label" style={{ color: 'var(--accent)' }}>Prima neta</p>
          <p className="metric-value metric-value-accent mono-display">
            {formatCurrency(primaNeta)}
          </p>
        </Card>
        <Card style={{ padding: '24px' }}>
          <p className="metric-label">Prima comercial</p>
          <p className="metric-value mono-display">{formatCurrency(primaComercial)}</p>
        </Card>
        <Card style={{ padding: '24px' }}>
          <p className="metric-label">Factor comercial</p>
          <p className="metric-value mono-display">{factorComercial}×</p>
        </Card>
      </div>

      {/* ── Desglose por ubicación ── */}
      {primasPorUbicacion && primasPorUbicacion.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3 className="metric-label">Desglose por ubicación</h3>
          {primasPorUbicacion.map(pu => (
            <div
              key={pu.indice}
              style={{
                border: `1px solid ${pu.calculada ? 'var(--border)' : 'rgba(240, 68, 71, 0.25)'}`,
                backgroundColor: 'var(--surface)',
              }}
            >
              {/* Row header */}
              <div
                className="flex items-center justify-between"
                style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)' }}
              >
                <div className="flex items-center" style={{ gap: '12px' }}>
                  <span
                    className="mono-display"
                    style={{ fontSize: '0.6875rem', color: 'var(--muted)', width: '24px' }}
                  >
                    {pu.indice}
                  </span>
                  <span
                    className="badge"
                    style={
                      pu.calculada
                        ? { color: 'var(--accent)', borderColor: 'rgba(0,200,150,0.3)', background: 'var(--accent-bg)' }
                        : { color: 'var(--danger)', borderColor: 'rgba(240,68,71,0.3)', background: 'var(--danger-bg)' }
                    }
                  >
                    {pu.calculada ? 'CALCULADA' : 'INCALCULABLE'}
                  </span>
                </div>
                {pu.calculada && pu.total !== undefined && (
                  <span
                    className="mono-display"
                    style={{ fontSize: '1.0625rem', color: 'var(--cream)' }}
                  >
                    {formatCurrency(pu.total)}
                  </span>
                )}
              </div>

              {/* Desglose */}
              {pu.calculada && pu.desglose && (
                <div
                  className="grid grid-cols-2 md:grid-cols-4"
                  style={{ padding: '16px 20px', gap: '16px' }}
                >
                  {Object.entries(pu.desglose)
                    .filter(([k]) => k !== 'total')
                    .map(([k, v]) => (
                      <div key={k} className="mono-display">
                        <p style={{ fontSize: '0.6875rem', color: 'var(--muted)', marginBottom: '4px' }}>
                          {k}
                        </p>
                        <p
                          style={{
                            fontSize: '0.8125rem',
                            color: Number(v) > 0 ? 'var(--cream)' : 'var(--border-2)',
                          }}
                        >
                          {formatCurrency(Number(v))}
                        </p>
                      </div>
                    ))}
                </div>
              )}

              {/* Alertas de error */}
              {!pu.calculada && pu.alertas && pu.alertas.length > 0 && (
                <div style={{ padding: '16px 20px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {pu.alertas.map(a => (
                    <p
                      key={a.codigo}
                      className="mono-display"
                      style={{ fontSize: '0.75rem', color: 'var(--danger)' }}
                    >
                      ! {a.codigo}: {a.mensaje}
                    </p>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* ── Actions ── */}
      <div className="flex gap-4" style={{ marginTop: '32px' }}>
        <Button
          variant="ghost"
          onClick={onRecalcular}
          id="btn-recalcular"
        >
          Recalcular
        </Button>
        <Button
          onClick={() => { window.location.href = '/'; }}
          id="btn-nueva-cotizacion-calculo"
        >
          Nueva cotización
        </Button>
      </div>
    </div>
  );
}
