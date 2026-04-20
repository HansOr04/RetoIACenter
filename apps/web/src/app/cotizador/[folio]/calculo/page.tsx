'use client';
import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { formatCurrency } from '@/lib/utils';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';

function IconAlert() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>;
}

function IconCheck() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"/></svg>;
}

interface Alerta {
  codigo: string;
  mensaje: string;
  campoAfectado?: string;
}

interface DetalleIncompleta {
  indice: number;
  nombreUbicacion: string;
  alertas: Alerta[];
}

interface Resumen {
  total: number;
  completas: number;
  incompletas: number;
  calculables: number;
  indicesIncompletos: number[];
  detalleIncompletas: DetalleIncompleta[];
}

interface DesglosePorUbicacion {
  indice: number;
  calculada: boolean;
  total?: number;
  desglose?: Record<string, number>;
  alertas?: Alerta[];
}

interface CalculoResult {
  primaNeta: number;
  primaComercial: number;
  factorComercial: number;
  primasPorUbicacion?: DesglosePorUbicacion[];
}

export default function CalculoPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  
  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<CalculoResult | null>(null);
  const [error, setError] = useState<string>('');
  
  // Pre-flight validation state
  const [summary, setSummary] = useState<Resumen | null>(null);
  const [coberturas, setCoberturas] = useState<Record<string, any> | null>(null);
  const [fetchingPreflight, setFetchingPreflight] = useState(true);

  useEffect(() => {
    Promise.all([
      quotesApi.getSummary(folio).catch(()=>null),
      quotesApi.getCoberturas(folio).catch(()=>null)
    ]).then(([sumRes, cobRes]) => {
      if (sumRes) setSummary(sumRes as Resumen);
      if (cobRes) setCoberturas(cobRes as Record<string, any>);
      setFetchingPreflight(false);
    });
  }, [folio]);

  async function ejecutarCalculo() {
    setLoading(true);
    setError('');
    try {
      const { version } = await quotesApi.getCoberturas(folio) as { version: number };
      const res = await quotesApi.calcular(folio, version);
      setResult(res as CalculoResult);
    } catch (err: unknown) {
      const e = err as { detail?: string; status?: number; primasPorUbicacion?: any };
      setError(e.detail || `Error ${e.status ?? ''}`);
      
      // If the backend returned detailed exceptions, refresh pre-flight state to show them
      if (e.primasPorUbicacion) {
         const sumRes = await quotesApi.getSummary(folio).catch(()=>null);
         if (sumRes) setSummary(sumRes as Resumen);
      }
    } finally {
      setLoading(false);
    }
  }
  
  const hasErrors = summary ? summary.calculables === 0 : false;

  return (
    <div>
      {/* ── Page header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title" style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--cream)', letterSpacing: '-0.03em', marginBottom: '8px' }}>
          Cálculo de Prima
        </h1>
        <p className="page-subtitle" style={{ fontSize: '0.9375rem', color: 'var(--muted)' }}>
          Resumen de ubicaciones y evaluación contra el catálogo de tarifas vigentes.
        </p>
      </div>

      {!result ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
          
          {/* ────── PRE-FLIGHT EVALUATION ────── */}
          {fetchingPreflight ? (
            <p style={{ color: 'var(--muted-2)' }}>Analizando ubicaciones...</p>
          ) : (
            <>
              {summary && (
                <div style={{
                  backgroundColor: 'var(--surface)',
                  border: '1px solid var(--border)',
                  borderRadius: '12px',
                  boxShadow: '0 4px 24px -12px rgba(15,21,32,0.06)'
                }}>
                  <div style={{ padding: '24px 32px', borderBottom: '1px solid var(--border)', backgroundColor: 'var(--surface-2)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h2 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--cream)', margin: 0 }}>
                      Análisis de Ubicaciones
                    </h2>
                    <div style={{
                      backgroundColor: hasErrors ? 'rgba(240,68,71,0.1)' : 'rgba(0,200,150,0.1)',
                      color: hasErrors ? 'var(--danger)' : 'var(--accent)',
                      padding: '6px 12px', borderRadius: '20px', fontSize: '0.75rem', fontWeight: 700
                    }}>
                      {summary.calculables} / {summary.total} Calculables
                    </div>
                  </div>
                  
                  <div style={{ padding: '32px' }}>
                    {summary.detalleIncompletas.length > 0 ? (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                        <p style={{ fontSize: '0.875rem', color: 'var(--muted)', marginBottom: '8px' }}>
                          Las siguientes ubicaciones tienen errores estructurales y previenen el cálculo:
                        </p>
                        {summary.detalleIncompletas.map(d => (
                          <div key={d.indice} style={{
                            border: '1px solid rgba(240,68,71,0.3)',
                            backgroundColor: 'rgba(240,68,71,0.04)',
                            borderRadius: '8px',
                            padding: '16px 20px'
                          }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px', color: 'var(--cream)', fontWeight: 600 }}>
                              <span style={{ color: 'var(--danger)' }}><IconAlert /></span>
                              UB-{String(d.indice).padStart(2, '0')}: {d.nombreUbicacion}
                            </div>
                            <ul style={{ listStyleType: 'disc', paddingLeft: '32px', color: 'var(--danger)', fontSize: '0.875rem', display: 'flex', flexDirection: 'column', gap: '4px' }}>
                              {d.alertas.map((a, i) => (
                                <li key={i}>
                                  <strong>{a.codigo}</strong>: {a.mensaje} 
                                  {a.campoAfectado && <span style={{ opacity: 0.8 }}> (Campo: {a.campoAfectado})</span>}
                                </li>
                              ))}
                            </ul>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', color: 'var(--accent)', fontWeight: 600 }}>
                        <IconCheck /> Todas las ubicaciones están listas para ser evaluadas.
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* ────── SELECTED COVERAGES ────── */}
              {coberturas && (
                <div style={{
                  backgroundColor: 'var(--surface)',
                  border: '1px solid var(--border)',
                  borderRadius: '12px',
                  boxShadow: '0 4px 24px -12px rgba(15,21,32,0.06)'
                }}>
                  <div style={{ padding: '24px 32px', borderBottom: '1px solid var(--border)', backgroundColor: 'var(--surface-2)' }}>
                    <h2 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--cream)', margin: 0 }}>
                      Coberturas a evaluar
                    </h2>
                  </div>
                  <div style={{ padding: '32px' }}>
                    <div className="grid grid-cols-2 md:grid-cols-4" style={{ gap: '16px' }}>
                      {Object.keys(coberturas).filter(k => coberturas[k] === true && typeof coberturas[k] === 'boolean').map(key => (
                         <div key={key} style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.875rem', color: 'var(--muted)' }}>
                           <span style={{ color: 'var(--accent)' }}><IconCheck /></span>
                           <span style={{ textTransform: 'capitalize' }}>{key.replace(/([A-Z])/g, ' $1').trim()}</span>
                         </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </>
          )}

          {error && (
            <div className="error-banner" style={{
              backgroundColor: 'rgba(229,62,62,0.08)',
              border: '1px solid rgba(229,62,62,0.3)',
              borderRadius: '8px',
              padding: '16px',
              color: 'var(--danger)',
              display: 'flex',
              alignItems: 'center',
              gap: '12px'
            }}>
              <IconAlert />
              <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>{error}</span>
            </div>
          )}

          <div className="floating-footer">
            <Button
              onClick={ejecutarCalculo}
              loading={loading}
              disabled={loading || hasErrors}
              size="lg"
              id="btn-ejecutar-calculo"
              style={{ padding: '12px 32px', fontSize: '0.9375rem' }}
            >
              {loading ? 'Calculando…' : 'Ejecutar cálculo exacto →'}
            </Button>
            
            {hasErrors && (
              <Button
                variant="ghost"
                onClick={() => router.push(`/cotizador/${folio}/ubicaciones`)}
                style={{ color: 'var(--cream)', padding: '12px 24px' }}
              >
                Volver a corregir Ubicaciones
              </Button>
            )}
            {!hasErrors && (
              <Button
                variant="ghost"
                onClick={() => router.push(`/cotizador/${folio}/coberturas`)}
                style={{ padding: '12px 24px' }}
              >
                Modificar riesgos
              </Button>
            )}
          </div>
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
}: Readonly<{ result: CalculoResult; onRecalcular: () => void; folio: string }>) {
  const { primaNeta, primaComercial, factorComercial, primasPorUbicacion } = result;

  return (
    <div>
      <div className="grid grid-cols-1 md:grid-cols-3" style={{ gap: '16px', marginBottom: '40px' }}>
        <Card variant="accent" style={{ padding: '24px' }}>
          <p className="metric-label" style={{ color: 'var(--accent)' }}>Prima neta</p>
          <p className="metric-value metric-value-accent mono-display">{formatCurrency(primaNeta)}</p>
          <p style={{ fontSize: '0.75rem', color: 'var(--accent-dim)', marginTop: '8px', lineHeight: 1.4 }}>
            Suma exacta de las tasas de riesgo multiplicadas por los valores asegurados. Excluye impuestos y gastos operativos.
          </p>
        </Card>
        <Card style={{ padding: '24px' }}>
          <p className="metric-label">Prima comercial</p>
          <p className="metric-value mono-display">{formatCurrency(primaComercial)}</p>
          <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: '8px', lineHeight: 1.4 }}>
            Prima final al cliente. Resulta de multiplicar la Prima Neta por el Factor de Gastos de la aseguradora.
          </p>
        </Card>
        <Card style={{ padding: '24px' }}>
          <p className="metric-label">Factor de Gastos</p>
          <p className="metric-value mono-display">{factorComercial}×</p>
        </Card>
      </div>

      {primasPorUbicacion && primasPorUbicacion.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3 className="metric-label" style={{ marginBottom: '8px' }}>Desglose paramétrico por ubicación</h3>
          {primasPorUbicacion.map(pu => (
            <div key={pu.indice} style={{
              border: `1px solid ${pu.calculada ? 'var(--border)' : 'rgba(240, 68, 71, 0.25)'}`,
              backgroundColor: 'var(--surface)',
              borderRadius: '8px',
              overflow: 'hidden'
            }}>
              <div className="flex items-center justify-between" style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', backgroundColor: 'var(--surface-2)' }}>
                <div className="flex items-center" style={{ gap: '12px' }}>
                  <span className="mono-display" style={{ fontSize: '0.875rem', color: 'var(--cream)' }}>
                    UB-{String(pu.indice).padStart(2, '0')}
                  </span>
                  <span className="badge" style={pu.calculada ? { color: 'var(--accent)', borderColor: 'rgba(0,200,150,0.3)', background: 'rgba(0,200,150,0.1)' } : { color: 'var(--danger)', borderColor: 'rgba(240,68,71,0.3)', background: 'rgba(240,68,71,0.1)' }}>
                    {pu.calculada ? 'CALCULADA' : 'INCALCULABLE'}
                  </span>
                </div>
                {pu.calculada && pu.total !== undefined && (
                  <span className="mono-display" style={{ fontSize: '1.125rem', color: 'var(--cream)', fontWeight: 700 }}>
                    {formatCurrency(pu.total)}
                  </span>
                )}
              </div>

              {pu.calculada && pu.desglose && (
                <div className="grid grid-cols-2 md:grid-cols-4" style={{ padding: '24px 20px', gap: '20px' }}>
                  {Object.entries(pu.desglose).filter(([k]) => k !== 'total' && Number(pu.desglose![k]) > 0).map(([k, v]) => (
                    <div key={k} className="mono-display">
                      <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginBottom: '4px', textTransform: 'capitalize' }}>
                        {k.replace(/([A-Z])/g, ' $1').trim()}
                      </p>
                      <p style={{ fontSize: '0.9375rem', color: 'var(--cream)', fontWeight: 600 }}>
                        {formatCurrency(Number(v))}
                      </p>
                    </div>
                  ))}
                </div>
              )}

              {!pu.calculada && pu.alertas && pu.alertas.length > 0 && (
                <div style={{ padding: '16px 20px', display: 'flex', flexDirection: 'column', gap: '8px', backgroundColor: 'rgba(240,68,71,0.04)' }}>
                  {pu.alertas.map(a => (
                    <div key={a.codigo} style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                       <span style={{ color: 'var(--danger)' }}><IconAlert /></span>
                       <p className="mono-display" style={{ fontSize: '0.75rem', color: 'var(--danger)' }}>
                         <strong>{a.codigo}</strong>: {a.mensaje} {a.campoAfectado && `(${a.campoAfectado})`}
                       </p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      <div className="floating-footer">
        <Button variant="ghost" onClick={onRecalcular} id="btn-recalcular" style={{ padding: '12px 24px', color: 'var(--cream)' }}>
           Volver atrás
        </Button>
        <Button onClick={() => { window.location.href = '/'; }} id="btn-nueva-cotizacion-calculo" style={{ padding: '12px 24px' }}>
          Nueva cotización completada
        </Button>
      </div>
    </div>
  );
}
