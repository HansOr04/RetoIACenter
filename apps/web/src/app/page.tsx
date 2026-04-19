'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { generateIdempotencyKey } from '@/lib/utils';

export default function LandingPage() {
  const router = useRouter();
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [folio, setFolioInput] = useState<string>('');

  async function crearFolio() {
    setLoading(true);
    setError('');
    try {
      const key = generateIdempotencyKey();
      const res = await foliosApi.crear(key) as { numeroFolio: string };
      router.push(`/cotizador/${res.numeroFolio}/datos-generales`);
    } catch (e: unknown) {
      const err = e as { detail?: string };
      setError(err.detail || 'No se pudo conectar con el servidor');
    } finally {
      setLoading(false);
    }
  }

  async function continuarFolio(e: React.FormEvent) {
    e.preventDefault();
    if (!folio.trim()) return;
    router.push(`/cotizador/${folio.trim()}/estado`);
  }

  return (
    <main className="min-h-screen flex flex-col" style={{ backgroundColor: '#0A0A0F' }}>
      <header
        className="border-b px-8 py-5 flex justify-between items-center"
        style={{ borderColor: '#1E1E2A' }}
      >
        <div>
          <span className="font-serif text-lg" style={{ color: '#F5F5F0' }}>Cotizador</span>
          <span className="font-mono text-xs ml-3" style={{ color: '#6B6B7A' }}>Seguros de Daños</span>
        </div>
        <span
          className="font-mono text-xs border px-3 py-1 rounded"
          style={{ color: '#6B6B7A', borderColor: '#1E1E2A' }}
        >
          Sofka · Reto IA Center
        </span>
      </header>

      <div className="flex-1 flex items-center px-8 md:px-20 py-20">
        <div className="max-w-2xl">
          <div
            className="font-mono text-[120px] leading-none select-none mb-8 -ml-2"
            style={{ color: '#1E1E2A' }}
          >
            §
          </div>
          <h1 className="font-serif text-5xl md:text-6xl leading-tight mb-4" style={{ color: '#F5F5F0' }}>
            Cotización de seguros<br />
            <em className="not-italic" style={{ color: '#00D9A3' }}>comerciales.</em>
          </h1>
          <p className="text-lg mb-12 max-w-md leading-relaxed" style={{ color: '#6B6B7A' }}>
            Prima neta calculada con trazabilidad completa por ubicación,
            cobertura y componente financiero.
          </p>

          <div className="flex flex-col sm:flex-row gap-4">
            <button
              onClick={crearFolio}
              disabled={loading}
              className="font-medium px-8 py-4 text-sm transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
            >
              {loading ? 'Creando folio...' : '+ Nueva cotización'}
            </button>

            <form onSubmit={continuarFolio} className="flex gap-2">
              <input
                value={folio}
                onChange={e => setFolioInput(e.target.value)}
                placeholder="F2026-0042"
                className="border font-mono text-sm px-4 py-4 w-48 focus:outline-none transition-colors"
                style={{
                  backgroundColor: '#111118',
                  borderColor: '#1E1E2A',
                  color: '#F5F5F0',
                }}
                onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
              />
              <button
                type="submit"
                className="border px-6 py-4 text-sm transition-colors"
                style={{ borderColor: '#1E1E2A', color: '#F5F5F0' }}
                onMouseEnter={e => {
                  e.currentTarget.style.borderColor = '#00D9A3';
                  e.currentTarget.style.color = '#00D9A3';
                }}
                onMouseLeave={e => {
                  e.currentTarget.style.borderColor = '#1E1E2A';
                  e.currentTarget.style.color = '#F5F5F0';
                }}
              >
                Continuar
              </button>
            </form>
          </div>

          {error && (
            <div
              className="mt-6 border text-sm px-4 py-3"
              style={{ borderColor: 'rgba(255,77,77,0.3)', backgroundColor: 'rgba(255,77,77,0.05)', color: '#FF4D4D' }}
            >
              {error}
            </div>
          )}
        </div>

        <div className="hidden lg:block ml-auto pl-20">
          <div
            className="border p-8 w-72 font-mono text-xs space-y-3"
            style={{ borderColor: '#1E1E2A', backgroundColor: '#111118', color: '#6B6B7A' }}
          >
            <div className="mb-4" style={{ color: '#00D9A3' }}>{`// proceso`}</div>
            {['01 Datos generales', '02 Layout', '03 Ubicaciones', '04 Coberturas', '05 Cálculo'].map((s) => (
              <div key={s} className="flex gap-3">
                <span style={{ color: '#1E1E2A' }}>→</span>
                <span>{s}</span>
              </div>
            ))}
            <div
              className="pt-4 border-t"
              style={{ borderColor: '#1E1E2A', color: '#00D9A3' }}
            >
              prima_neta × factor = prima_comercial
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
