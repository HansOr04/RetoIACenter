'use client';
import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { StepIndicator } from '@/components/StepIndicator';

interface UbicacionForm {
  calle: string;
  numero: string;
  ciudad: string;
  provincia: string;
  valorEdificacion: string;
  valorContenidos: string;
}

const emptyUbicacion = (): UbicacionForm => ({
  calle: '', numero: '', ciudad: '', provincia: '',
  valorEdificacion: '', valorContenidos: '',
});

const inputCls =
  'w-full border text-sm px-3 py-2.5 focus:outline-none transition-colors';

export default function UbicacionesPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [ubicaciones, setUbicaciones] = useState<UbicacionForm[]>([emptyUbicacion()]);

  useEffect(() => {
    foliosApi.getEstado(folio)
      .then((estado: unknown) => {
        const s = estado as { ubicacionesLayout?: { numeroUbicaciones?: number } };
        const n = s.ubicacionesLayout?.numeroUbicaciones ?? 1;
        setUbicaciones(Array.from({ length: n }, () => emptyUbicacion()));
      })
      .catch(() => {});
  }, [folio]);

  function updateUbicacion(i: number, field: keyof UbicacionForm, value: string) {
    setUbicaciones(prev =>
      prev.map((u, idx) => (idx === i ? { ...u, [field]: value } : u)),
    );
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      await quotesApi.putUbicacion(folio, estado.version, {
        ubicaciones: ubicaciones.map((u, i) => ({
          indice: i + 1,
          direccion: {
            calle: u.calle, numero: u.numero,
            ciudad: u.ciudad, provincia: u.provincia,
          },
          datosTecnicos: {
            valorEdificacion: u.valorEdificacion ? parseFloat(u.valorEdificacion) : null,
            valorContenidos: u.valorContenidos ? parseFloat(u.valorContenidos) : null,
          },
        })),
      });
      router.push(`/cotizador/${folio}/coberturas`);
    } catch (err: unknown) {
      const e = err as { detail?: string };
      setError(e.detail || 'Error al guardar ubicaciones');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <StepIndicator current={4} />
      <div className="mt-10">
        <h1 className="font-serif text-3xl mb-1" style={{ color: '#F5F5F0' }}>Ubicaciones</h1>
        <p className="text-sm mb-8" style={{ color: '#6B6B7A' }}>
          Folio <span className="font-mono" style={{ color: '#00D9A3' }}>{folio}</span>
        </p>

        <form onSubmit={handleSubmit} className="space-y-8">
          {ubicaciones.map((u, i) => (
            <div key={`ub-${i + 1}`} className="border p-6" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
              <div className="flex items-center gap-3 mb-5">
                <span
                  className="font-mono text-xs border px-2 py-1"
                  style={{ borderColor: '#1E1E2A', color: '#6B6B7A' }}
                >
                  UB-{String(i + 1).padStart(2, '0')}
                </span>
                <span className="text-sm" style={{ color: '#6B6B7A' }}>Ubicación {i + 1}</span>
              </div>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                <div className="col-span-2">
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Calle</label>
                  <input
                    className={inputCls}
                    placeholder="Av. Principal"
                    value={u.calle}
                    onChange={ev => updateUbicacion(i, 'calle', ev.target.value)}
                    style={{ backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  />
                </div>
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Número</label>
                  <input
                    className={inputCls}
                    placeholder="123"
                    value={u.numero}
                    onChange={ev => updateUbicacion(i, 'numero', ev.target.value)}
                    style={{ backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  />
                </div>
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Ciudad</label>
                  <input
                    className={inputCls}
                    placeholder="Quito"
                    value={u.ciudad}
                    onChange={ev => updateUbicacion(i, 'ciudad', ev.target.value)}
                    style={{ backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  />
                </div>
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Provincia</label>
                  <input
                    className={inputCls}
                    placeholder="Pichincha"
                    value={u.provincia}
                    onChange={ev => updateUbicacion(i, 'provincia', ev.target.value)}
                    style={{ backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  />
                </div>
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Valor edificación (USD)</label>
                  <input
                    type="number"
                    className={inputCls}
                    placeholder="150000"
                    value={u.valorEdificacion}
                    onChange={ev => updateUbicacion(i, 'valorEdificacion', ev.target.value)}
                    style={{ backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  />
                </div>
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Valor contenidos (USD)</label>
                  <input
                    type="number"
                    className={inputCls}
                    placeholder="50000"
                    value={u.valorContenidos}
                    onChange={ev => updateUbicacion(i, 'valorContenidos', ev.target.value)}
                    style={{ backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  />
                </div>
              </div>
            </div>
          ))}

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

          <div className="flex gap-4">
            <button
              type="submit"
              disabled={saving}
              className="font-medium px-8 py-3 text-sm transition-colors disabled:opacity-50"
              style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
            >
              {saving ? 'Guardando...' : 'Continuar a coberturas →'}
            </button>
            <button
              type="button"
              onClick={() => router.push(`/cotizador/${folio}/calculo`)}
              className="border px-6 py-3 text-sm transition-colors"
              style={{ borderColor: '#1E1E2A', color: '#6B6B7A' }}
              onMouseEnter={e => {
                e.currentTarget.style.borderColor = '#00D9A3';
                e.currentTarget.style.color = '#00D9A3';
              }}
              onMouseLeave={e => {
                e.currentTarget.style.borderColor = '#1E1E2A';
                e.currentTarget.style.color = '#6B6B7A';
              }}
            >
              Ir al cálculo
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
