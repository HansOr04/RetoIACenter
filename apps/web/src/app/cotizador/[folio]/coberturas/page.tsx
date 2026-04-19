'use client';
import { useState, useEffect, FormEvent } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';

interface CoverageOptions {
  incendioEdificios: boolean;
  incendioContenidos: boolean;
  extensionCobertura: boolean;
  catTev: boolean;
  catFhm: boolean;
  remocionEscombros: boolean;
  gastosExtraordinarios: boolean;
  perdidaRentas: boolean;
  bi: boolean;
  equipoElectronico: boolean;
  robo: boolean;
  dineroValores: boolean;
  vidrios: boolean;
  anunciosLuminosos: boolean;
}

const COVERAGE_LABELS: { key: keyof CoverageOptions; label: string; desc: string }[] = [
  { key: 'incendioEdificios',     label: 'Incendio Edificios',       desc: 'Daños al edificio por incendio, rayo o explosión' },
  { key: 'incendioContenidos',    label: 'Incendio Contenidos',      desc: 'Mercancía, mobiliario y equipo' },
  { key: 'extensionCobertura',    label: 'Extensión de Cobertura',   desc: 'Coberturas adicionales sobre incendio básico' },
  { key: 'catTev',                label: 'Catástrofe TEV',           desc: 'Terrorismo, explosión y vandalismo' },
  { key: 'catFhm',                label: 'Catástrofe FHM',           desc: 'Huracán, inundación y fenómenos hidrometeorológicos' },
  { key: 'remocionEscombros',     label: 'Remoción de Escombros',    desc: 'Gastos de demolición y remoción post-siniestro' },
  { key: 'gastosExtraordinarios', label: 'Gastos Extraordinarios',   desc: 'Gastos para restablecer operaciones' },
  { key: 'perdidaRentas',         label: 'Pérdida de Rentas',        desc: 'Lucro cesante por renta de inmueble' },
  { key: 'bi',                    label: 'Business Interruption',    desc: 'Pérdida de utilidades por interrupción del negocio' },
  { key: 'equipoElectronico',     label: 'Equipo Electrónico',       desc: 'Equipos de cómputo y telecomunicaciones' },
  { key: 'robo',                  label: 'Robo y Asalto',            desc: 'Hurto calificado y robo con violencia' },
  { key: 'dineroValores',         label: 'Dinero y Valores',         desc: 'Pérdida de efectivo y títulos valor' },
  { key: 'vidrios',               label: 'Vidrios',                  desc: 'Rotura accidental de cristales y vidrios' },
  { key: 'anunciosLuminosos',     label: 'Anuncios Luminosos',       desc: 'Letreros y anuncios luminosos exteriores' },
];

const DEFAULTS: CoverageOptions = {
  incendioEdificios: true, incendioContenidos: false, extensionCobertura: false,
  catTev: false, catFhm: false, remocionEscombros: false, gastosExtraordinarios: false,
  perdidaRentas: false, bi: false, equipoElectronico: false, robo: false,
  dineroValores: false, vidrios: false, anunciosLuminosos: false,
};

export default function CoberturasPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [version, setVersion] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [options, setOptions] = useState<CoverageOptions>({ ...DEFAULTS });

  useEffect(() => {
    foliosApi.getEstado(folio)
      .then((e: unknown) => setVersion((e as { version: number }).version))
      .catch(() => setError('No se pudo cargar el folio'));
  }, [folio]);

  const selectedCount = Object.values(options).filter(Boolean).length;

  function toggle(key: keyof CoverageOptions) {
    setOptions(prev => ({ ...prev, [key]: !prev[key] }));
    setError('');
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (selectedCount === 0) { setError('Selecciona al menos una cobertura'); return; }
    if (version === null) { setError('Cargando folio, intenta de nuevo'); return; }
    setSaving(true);
    setError('');
    try {
      await quotesApi.putCoberturas(folio, version, options);
      router.push(`/cotizador/${folio}/calculo`);
    } catch (err: unknown) {
      const e = err as { detail?: string; title?: string };
      setError(e.detail ?? e.title ?? 'Error al guardar coberturas');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-semibold mb-1" style={{ color: '#F5F5F0' }}>Coberturas</h1>
        <p className="text-sm" style={{ color: '#6B6B7A' }}>
          Selecciona las coberturas que aplican para este folio.{' '}
          {selectedCount > 0 && (
            <span style={{ color: '#00D9A3' }}>{selectedCount} seleccionada{selectedCount !== 1 ? 's' : ''}</span>
          )}
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-8">
          {COVERAGE_LABELS.map(({ key, label, desc }) => {
            const on = options[key];
            return (
              <button
                key={key}
                type="button"
                onClick={() => toggle(key)}
                className="flex items-center gap-3 px-4 py-3.5 border text-left w-full transition-all"
                style={{
                  borderColor: on ? '#00D9A3' : '#1E1E2A',
                  backgroundColor: on ? 'rgba(0,217,163,0.07)' : '#0D0D13',
                }}
              >
                {/* Checkbox indicator */}
                <span
                  className="flex-shrink-0 w-5 h-5 rounded-sm border flex items-center justify-center transition-all"
                  style={{
                    borderColor: on ? '#00D9A3' : '#2A2A3A',
                    backgroundColor: on ? '#00D9A3' : 'transparent',
                  }}
                >
                  {on && (
                    <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
                      <path d="M1 4l3 3 5-6" stroke="#0A0A0F" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                  )}
                </span>
                <span>
                  <span className="block text-sm font-medium" style={{ color: on ? '#F5F5F0' : '#9B9BAA' }}>{label}</span>
                  <span className="block text-xs mt-0.5" style={{ color: '#4A4A5A' }}>{desc}</span>
                </span>
              </button>
            );
          })}
        </div>

        {error && (
          <div className="border-l-2 px-4 py-3 text-sm mb-6" style={{ borderColor: '#FF4D4D', backgroundColor: 'rgba(255,77,77,0.06)', color: '#FF4D4D' }}>
            {error}
          </div>
        )}

        <button
          type="submit"
          disabled={saving || selectedCount === 0 || version === null}
          className="px-8 py-3 text-sm font-semibold transition-colors disabled:opacity-40 hover:opacity-90"
          style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
        >
          {saving ? 'Guardando...' : 'Continuar al cálculo →'}
        </button>
      </form>
    </div>
  );
}
