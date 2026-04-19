'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { StepIndicator } from '@/components/StepIndicator';

const SECCIONES = [
  { key: 'datosTecnicos', label: 'Datos técnicos' },
  { key: 'giroComercial', label: 'Giro comercial' },
  { key: 'garantias',     label: 'Garantías' },
] as const;

type SeccionKey = (typeof SECCIONES)[number]['key'];

interface SeccionesState {
  datosTecnicos: boolean;
  giroComercial: boolean;
  garantias: boolean;
}

export default function LayoutPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [numUbicaciones, setNumUbicaciones] = useState<number>(1);
  const [secciones, setSecciones] = useState<SeccionesState>({
    datosTecnicos: true, giroComercial: true, garantias: true,
  });

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      await foliosApi.putLayout(folio, estado.version, {
        numeroUbicaciones: numUbicaciones,
        seccionesAplican: { direccion: true, ...secciones },
      });
      router.push(`/cotizador/${folio}/ubicaciones`);
    } catch (err: unknown) {
      const e = err as { detail?: string };
      setError(e.detail || 'Error al guardar layout');
    } finally {
      setSaving(false);
    }
  }

  function toggleSeccion(key: SeccionKey) {
    setSecciones(s => ({ ...s, [key]: !s[key] }));
  }

  return (
    <div>
      <StepIndicator current={3} />

      <div className="mt-10 max-w-lg">
        <h1 className="font-serif text-3xl mb-1" style={{ color: '#F5F5F0' }}>
          Layout de ubicaciones
        </h1>
        <p className="text-sm mb-8" style={{ color: '#6B6B7A' }}>
          Folio <span className="font-mono" style={{ color: '#00D9A3' }}>{folio}</span>
        </p>

        <form onSubmit={handleSubmit} className="space-y-8">
          <div>
            <label className="block text-xs uppercase tracking-widest mb-4" style={{ color: '#6B6B7A' }}>
              ¿Cuántas ubicaciones tiene la póliza?
            </label>
            <div className="flex items-center gap-6">
              <button
                type="button"
                onClick={() => setNumUbicaciones(n => Math.max(1, n - 1))}
                className="w-12 h-12 border text-xl transition-colors"
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
                −
              </button>
              <span className="font-mono text-5xl w-16 text-center" style={{ color: '#F5F5F0' }}>
                {numUbicaciones}
              </span>
              <button
                type="button"
                onClick={() => setNumUbicaciones(n => Math.min(20, n + 1))}
                className="w-12 h-12 border text-xl transition-colors"
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
                +
              </button>
            </div>
            <p className="text-xs mt-2" style={{ color: '#6B6B7A' }}>Máximo 20 ubicaciones</p>
          </div>

          <div>
            <label className="block text-xs uppercase tracking-widest mb-4" style={{ color: '#6B6B7A' }}>
              Secciones que aplican
            </label>
            <div className="space-y-3">
              <div
                className="flex items-center gap-3 py-3 border-b"
                style={{ borderColor: 'rgba(30,30,42,0.5)' }}
              >
                <div
                  className="w-4 h-4 flex items-center justify-center"
                  style={{ backgroundColor: '#00D9A3' }}
                >
                  <span className="text-xs" style={{ color: '#0A0A0F' }}>✓</span>
                </div>
                <span className="text-sm" style={{ color: '#F5F5F0' }}>Dirección</span>
                <span className="ml-auto text-xs" style={{ color: '#6B6B7A' }}>Obligatorio</span>
              </div>

              {SECCIONES.map(({ key, label }) => (
                <label
                  key={key}
                  className="flex items-center gap-3 py-3 border-b cursor-pointer"
                  style={{ borderColor: 'rgba(30,30,42,0.5)' }}
                >
                  <div
                    className="w-4 h-4 border flex items-center justify-center transition-colors"
                    style={{
                      backgroundColor: secciones[key] ? '#00D9A3' : 'transparent',
                      borderColor: secciones[key] ? '#00D9A3' : '#1E1E2A',
                    }}
                    onClick={() => toggleSeccion(key)}
                  >
                    {secciones[key] && (
                      <span className="text-xs" style={{ color: '#0A0A0F' }}>✓</span>
                    )}
                  </div>
                  <span className="text-sm" style={{ color: '#F5F5F0' }}>{label}</span>
                </label>
              ))}
            </div>
          </div>

          {error && (
            <div
              className="border text-sm px-4 py-3"
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
            type="submit"
            disabled={saving}
            className="w-full font-medium py-4 text-sm transition-colors disabled:opacity-50"
            style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
            onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
            onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
          >
            {saving
              ? 'Guardando...'
              : `Configurar ${numUbicaciones} ubicación${numUbicaciones > 1 ? 'es' : ''} →`}
          </button>
        </form>
      </div>
    </div>
  );
}
