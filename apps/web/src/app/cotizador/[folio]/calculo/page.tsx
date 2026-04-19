'use client';
import { useState } from 'react';
import { useParams } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { StepIndicator } from '@/components/StepIndicator';
import { formatCurrency } from '@/lib/utils';

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
      <StepIndicator current={6} />
      <div className="mt-10">
        <h1 className="font-serif text-3xl mb-1" style={{ color: '#F5F5F0' }}>Cálculo de prima</h1>
        <p className="text-sm mb-8" style={{ color: '#6B6B7A' }}>
          Folio <span className="font-mono" style={{ color: '#00D9A3' }}>{folio}</span>
        </p>

        {!result ? (
          <div className="max-w-md">
            <div className="border p-8 mb-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
              <p className="text-xs uppercase tracking-widest mb-4" style={{ color: '#6B6B7A' }}>Resumen</p>
              <p className="text-sm leading-relaxed" style={{ color: 'rgba(245,245,240,0.7)' }}>
                El cálculo evaluará cada ubicación contra las coberturas configuradas
                y los catálogos de tarifas vigentes.
              </p>
            </div>

            {error && (
              <div
                className="border text-sm px-4 py-3 mb-6"
                style={{
                  borderColor: 'rgba(255,77,77,0.3)',
                  backgroundColor: 'rgba(255,77,77,0.05)',
                  color: '#FF4D4D',
                }}
              >
                {error}
              </div>
            )}

            <button
              onClick={ejecutarCalculo}
              disabled={loading}
              className="w-full font-medium py-4 text-sm transition-colors disabled:opacity-50"
              style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
            >
              {loading
                ? <span className="font-mono animate-pulse">Calculando...</span>
                : 'Ejecutar cálculo →'}
            </button>
          </div>
        ) : (
          <ResultadoCalculo
            result={result}
            onRecalcular={() => setResult(null)}
          />
        )}
      </div>
    </div>
  );
}

function ResultadoCalculo({
  result,
  onRecalcular,
}: Readonly<{ result: CalculoResult; onRecalcular: () => void }>) {
  const { primaNeta, primaComercial, factorComercial, primasPorUbicacion } = result;

  return (
    <div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-10">
        <div
          className="border p-6"
          style={{ borderColor: 'rgba(0,217,163,0.3)', backgroundColor: 'rgba(0,217,163,0.05)' }}
        >
          <p className="text-xs uppercase tracking-widest mb-2" style={{ color: '#00D9A3' }}>Prima neta</p>
          <p className="font-mono text-3xl" style={{ color: '#00D9A3' }}>{formatCurrency(primaNeta)}</p>
        </div>
        <div className="border p-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
          <p className="text-xs uppercase tracking-widest mb-2" style={{ color: '#6B6B7A' }}>Prima comercial</p>
          <p className="font-mono text-3xl" style={{ color: '#F5F5F0' }}>{formatCurrency(primaComercial)}</p>
        </div>
        <div className="border p-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
          <p className="text-xs uppercase tracking-widest mb-2" style={{ color: '#6B6B7A' }}>Factor comercial</p>
          <p className="font-mono text-3xl" style={{ color: '#F5F5F0' }}>{factorComercial}×</p>
        </div>
      </div>

      {primasPorUbicacion && primasPorUbicacion.length > 0 && (
        <div className="space-y-4">
          <h3 className="text-xs uppercase tracking-widest" style={{ color: '#6B6B7A' }}>
            Desglose por ubicación
          </h3>
          {primasPorUbicacion.map(pu => (
            <div
              key={pu.indice}
              className="border"
              style={{
                borderColor: pu.calculada ? '#1E1E2A' : 'rgba(255,77,77,0.2)',
                backgroundColor: '#111118',
              }}
            >
              <div
                className="flex items-center justify-between p-5 border-b"
                style={{ borderColor: '#1E1E2A' }}
              >
                <div className="flex items-center gap-3">
                  <span className="font-mono text-xs w-6" style={{ color: '#6B6B7A' }}>{pu.indice}</span>
                  <span
                    className="text-xs font-mono px-2 py-0.5 border"
                    style={{
                      color: pu.calculada ? '#00D9A3' : '#FF4D4D',
                      borderColor: pu.calculada ? 'rgba(0,217,163,0.3)' : 'rgba(255,77,77,0.3)',
                    }}
                  >
                    {pu.calculada ? 'CALCULADA' : 'INCALCULABLE'}
                  </span>
                </div>
                {pu.calculada && pu.total !== undefined && (
                  <span className="font-mono text-lg" style={{ color: '#F5F5F0' }}>
                    {formatCurrency(pu.total)}
                  </span>
                )}
              </div>

              {pu.calculada && pu.desglose && (
                <div className="p-5 grid grid-cols-2 md:grid-cols-4 gap-3">
                  {Object.entries(pu.desglose)
                    .filter(([k]) => k !== 'total')
                    .map(([k, v]) => (
                      <div key={k} className="font-mono">
                        <p className="text-xs mb-1" style={{ color: '#6B6B7A' }}>{k}</p>
                        <p
                          className="text-sm"
                          style={{ color: Number(v) > 0 ? '#F5F5F0' : '#1E1E2A' }}
                        >
                          {formatCurrency(Number(v))}
                        </p>
                      </div>
                    ))}
                </div>
              )}

              {!pu.calculada && pu.alertas && pu.alertas.length > 0 && (
                <div className="p-5 space-y-2">
                  {pu.alertas.map(a => (
                    <p key={a.codigo} className="text-xs font-mono" style={{ color: '#FF4D4D' }}>
                      ! {a.codigo}: {a.mensaje}
                    </p>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      <div className="mt-8 flex gap-4">
        <button
          onClick={onRecalcular}
          className="border px-6 py-3 text-sm transition-colors"
          style={{ borderColor: '#1E1E2A', color: '#6B6B7A' }}
          onMouseEnter={e => {
            e.currentTarget.style.borderColor = '#F5F5F0';
            e.currentTarget.style.color = '#F5F5F0';
          }}
          onMouseLeave={e => {
            e.currentTarget.style.borderColor = '#1E1E2A';
            e.currentTarget.style.color = '#6B6B7A';
          }}
        >
          Recalcular
        </button>
        <button
          onClick={() => { window.location.href = '/'; }}
          className="font-medium px-8 py-3 text-sm transition-colors"
          style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
          onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
          onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
        >
          Nueva cotización
        </button>
      </div>
    </div>
  );
}
