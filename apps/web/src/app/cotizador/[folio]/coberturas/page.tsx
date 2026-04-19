'use client';
import { useState, useEffect, FormEvent } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { Button } from '@/components/ui/Button';

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
      {/* ── Page header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title">Coberturas</h1>
        <p className="page-subtitle">
          Selecciona las coberturas que aplican para este folio.{' '}
          {selectedCount > 0 && (
            <span style={{ color: 'var(--accent)' }}>
              {selectedCount} seleccionada{selectedCount !== 1 ? 's' : ''}
            </span>
          )}
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        <div
          className="grid grid-cols-1 sm:grid-cols-2"
          style={{ gap: '8px', marginBottom: '32px' }}
        >
          {COVERAGE_LABELS.map(({ key, label, desc }) => {
            const on = options[key];
            return (
              <button
                key={key}
                type="button"
                onClick={() => toggle(key)}
                className={`coverage-item ${on ? 'is-on' : ''}`}
                id={`coverage-${key}`}
              >
                {/* Custom checkbox */}
                <span className={`coverage-checkbox ${on ? 'is-on' : ''}`}>
                  {on && (
                    <svg width="9" height="7" viewBox="0 0 9 7" fill="none">
                      <path
                        d="M1 3.5l2.5 2.5 4.5-5"
                        stroke="var(--bg)"
                        strokeWidth="1.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                    </svg>
                  )}
                </span>
                <span>
                  <span
                    style={{
                      display: 'block',
                      fontSize: '0.8125rem',
                      fontWeight: 500,
                      color: on ? 'var(--cream)' : 'var(--muted-2)',
                      transition: 'color 150ms ease',
                    }}
                  >
                    {label}
                  </span>
                  <span
                    style={{
                      display: 'block',
                      fontSize: '0.75rem',
                      color: 'var(--muted)',
                      marginTop: '2px',
                    }}
                  >
                    {desc}
                  </span>
                </span>
              </button>
            );
          })}
        </div>

        {error && (
          <div className="error-banner" style={{ marginBottom: '24px' }}>
            {error}
          </div>
        )}

        <Button
          type="submit"
          loading={saving}
          disabled={saving || selectedCount === 0 || version === null}
          size="lg"
          id="btn-continuar-calculo"
        >
          {saving ? 'Guardando…' : 'Continuar al cálculo →'}
        </Button>
      </form>
    </div>
  );
}
