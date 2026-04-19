'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { Button } from '@/components/ui/Button';

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
      {/* ── Page header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title">Layout de ubicaciones</h1>
        <p className="page-subtitle">
          Configura cuántas ubicaciones tendrá la póliza y qué secciones aplican.
        </p>
      </div>

      <div style={{ maxWidth: '480px' }}>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
          {/* ── N° ubicaciones ── */}
          <div>
            <label
              className="field-label"
              style={{ textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '16px', display: 'block' }}
            >
              ¿Cuántas ubicaciones tiene la póliza?
            </label>
            <div className="flex items-center" style={{ gap: '24px' }}>
              <button
                type="button"
                id="btn-decremento-ubicaciones"
                onClick={() => setNumUbicaciones(n => Math.max(1, n - 1))}
                className="btn btn-ghost"
                style={{ width: '48px', height: '48px', padding: 0, fontSize: '1.25rem' }}
              >
                −
              </button>
              <span
                className="mono-display"
                style={{ fontSize: '3rem', width: '64px', textAlign: 'center', color: 'var(--cream)' }}
              >
                {numUbicaciones}
              </span>
              <button
                type="button"
                id="btn-incremento-ubicaciones"
                onClick={() => setNumUbicaciones(n => Math.min(20, n + 1))}
                className="btn btn-ghost"
                style={{ width: '48px', height: '48px', padding: 0, fontSize: '1.25rem' }}
              >
                +
              </button>
            </div>
            <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: '8px' }}>
              Máximo 20 ubicaciones
            </p>
          </div>

          {/* ── Secciones ── */}
          <div>
            <label
              className="field-label"
              style={{ textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '16px', display: 'block' }}
            >
              Secciones que aplican
            </label>
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              {/* Dirección — siempre activa */}
              <div
                className="flex items-center"
                style={{ gap: '12px', padding: '12px 0', borderBottom: '1px solid var(--border)' }}
              >
                <div
                  style={{
                    width: '16px',
                    height: '16px',
                    backgroundColor: 'var(--accent)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexShrink: 0,
                  }}
                >
                  <svg width="9" height="7" viewBox="0 0 9 7" fill="none">
                    <path d="M1 3.5l2.5 2.5 4.5-5" stroke="var(--bg)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </div>
                <span style={{ fontSize: '0.875rem', color: 'var(--cream)' }}>Dirección</span>
                <span style={{ marginLeft: 'auto', fontSize: '0.6875rem', color: 'var(--muted)' }}>
                  Obligatorio
                </span>
              </div>

              {SECCIONES.map(({ key, label }) => (
                <label
                  key={key}
                  className="flex items-center"
                  style={{ gap: '12px', padding: '12px 0', borderBottom: '1px solid var(--border)', cursor: 'pointer' }}
                >
                  <div
                    onClick={() => toggleSeccion(key)}
                    style={{
                      width: '16px',
                      height: '16px',
                      border: `1px solid ${secciones[key] ? 'var(--accent)' : 'var(--border-2)'}`,
                      backgroundColor: secciones[key] ? 'var(--accent)' : 'transparent',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      transition: 'background-color 150ms ease, border-color 150ms ease',
                      cursor: 'pointer',
                    }}
                  >
                    {secciones[key] && (
                      <svg width="9" height="7" viewBox="0 0 9 7" fill="none">
                        <path d="M1 3.5l2.5 2.5 4.5-5" stroke="var(--bg)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                      </svg>
                    )}
                  </div>
                  <span style={{ fontSize: '0.875rem', color: 'var(--cream)' }}>{label}</span>
                </label>
              ))}
            </div>
          </div>

          {error && (
            <div className="error-banner">{error}</div>
          )}

          <Button
            type="submit"
            full
            loading={saving}
            size="lg"
            id="btn-configurar-ubicaciones"
          >
            {saving
              ? 'Guardando…'
              : `Configurar ${numUbicaciones} ubicación${numUbicaciones > 1 ? 'es' : ''} →`}
          </Button>
        </form>
      </div>
    </div>
  );
}
