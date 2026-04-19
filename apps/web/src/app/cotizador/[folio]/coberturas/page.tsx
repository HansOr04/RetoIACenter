'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { StepIndicator } from '@/components/StepIndicator';

const COBERTURAS_DISPONIBLES = [
  { id: 'INCENDIO',      label: 'Incendio y Líneas Aliadas',  descripcion: 'Cubre daños por incendio, rayo y explosión' },
  { id: 'ROBO',          label: 'Robo y Asalto',              descripcion: 'Cubre hurto calificado y robo con violencia' },
  { id: 'RC',            label: 'Responsabilidad Civil',      descripcion: 'Daños a terceros por negligencia' },
  { id: 'EQUIPO_ELECT',  label: 'Equipo Electrónico',         descripcion: 'Daños a equipos de cómputo y telecomunicaciones' },
  { id: 'LUCRO_CESANTE', label: 'Lucro Cesante',              descripcion: 'Pérdida de ingresos por siniestro cubierto' },
  { id: 'TODO_RIESGO',   label: 'Todo Riesgo Accidental',     descripcion: 'Cobertura amplia contra daños accidentales' },
] as const;

export default function CoberturasPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [selected, setSelected] = useState<Set<string>>(new Set(['INCENDIO']));
  const [factorComercial, setFactorComercial] = useState<string>('1.0');

  function toggle(id: string) {
    setSelected(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (selected.size === 0) { setError('Selecciona al menos una cobertura'); return; }
    setSaving(true);
    setError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      await quotesApi.putCoberturas(folio, estado.version, {
        coberturas: Array.from(selected),
        factorComercial: parseFloat(factorComercial) || 1.0,
      });
      router.push(`/cotizador/${folio}/calculo`);
    } catch (err: unknown) {
      const e = err as { detail?: string };
      setError(e.detail || 'Error al guardar coberturas');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <StepIndicator current={5} />
      <div className="mt-10">
        <h1 className="font-serif text-3xl mb-1" style={{ color: '#F5F5F0' }}>Coberturas</h1>
        <p className="text-sm mb-8" style={{ color: '#6B6B7A' }}>
          Folio <span className="font-mono" style={{ color: '#00D9A3' }}>{folio}</span>
        </p>

        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
            {COBERTURAS_DISPONIBLES.map(cob => {
              const isSelected = selected.has(cob.id);
              return (
                <label
                  key={cob.id}
                  className="flex items-start gap-4 p-5 border cursor-pointer transition-colors"
                  style={{
                    borderColor: isSelected ? '#00D9A3' : '#1E1E2A',
                    backgroundColor: isSelected ? 'rgba(0,217,163,0.05)' : '#111118',
                  }}
                >
                  <div
                    className="mt-0.5 w-4 h-4 border flex-shrink-0 flex items-center justify-center transition-colors"
                    style={{
                      backgroundColor: isSelected ? '#00D9A3' : 'transparent',
                      borderColor: isSelected ? '#00D9A3' : '#1E1E2A',
                    }}
                    onClick={() => toggle(cob.id)}
                  >
                    {isSelected && <span className="text-xs" style={{ color: '#0A0A0F' }}>✓</span>}
                  </div>
                  <div>
                    <div className="text-sm font-medium" style={{ color: '#F5F5F0' }}>{cob.label}</div>
                    <div className="text-xs mt-1" style={{ color: '#6B6B7A' }}>{cob.descripcion}</div>
                  </div>
                </label>
              );
            })}
          </div>

          <div className="max-w-xs mb-8">
            <label className="block text-xs uppercase tracking-widest mb-2" style={{ color: '#6B6B7A' }}>
              Factor comercial
            </label>
            <input
              type="number"
              step="0.01"
              min="0.5"
              max="3"
              value={factorComercial}
              onChange={e => setFactorComercial(e.target.value)}
              className="w-full border font-mono text-lg px-4 py-3 focus:outline-none transition-colors"
              style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
              onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
              onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
            />
            <p className="text-xs mt-1" style={{ color: '#6B6B7A' }}>
              Prima comercial = prima neta × factor
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

          <div className="flex gap-4">
            <button
              type="submit"
              disabled={saving}
              className="font-medium px-8 py-3 text-sm transition-colors disabled:opacity-50"
              style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
            >
              {saving ? 'Guardando...' : 'Continuar al cálculo →'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
