'use client';
import { useState, useEffect, FormEvent } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { quotesApi } from '@/lib/api';
import { Button } from '@/components/ui/Button';

/* ─── SVG Icons ─── */
function IconShieldCheck() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/></svg>;
}
function IconInfo() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>;
}

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
  { key: 'extensionCobertura',    label: 'Extensión general',        desc: 'Coberturas adicionales sobre incendio básico' },
  { key: 'catTev',                label: 'Catástrofe TEV',           desc: 'Terrorismo, explosión y vandalismo' },
  { key: 'catFhm',                label: 'Catástrofe FHM',           desc: 'Huracán, inundación y fenómenos hidrometeorológicos' },
  { key: 'remocionEscombros',     label: 'Remoción Escombros',       desc: 'Gastos de demolición y remoción' },
  { key: 'gastosExtraordinarios', label: 'Gastos Extra',             desc: 'Para restablecer operaciones' },
  { key: 'perdidaRentas',         label: 'Pérdida de Rentas',        desc: 'Lucro cesante por arrendamientos' },
  { key: 'bi',                    label: 'Business Interruption',    desc: 'Pérdida por interrupción del negocio' },
  { key: 'equipoElectronico',     label: 'Equipo Electrónico',       desc: 'Equipos de cómputo y redes' },
  { key: 'robo',                  label: 'Robo y Asalto',            desc: 'Hurto calificado con violencia' },
  { key: 'dineroValores',         label: 'Dinero y Valores',         desc: 'Efectivo en caja y tránsito' },
  { key: 'vidrios',               label: 'Cristales',                desc: 'Rotura accidental de vidrios' },
  { key: 'anunciosLuminosos',     label: 'Anuncios',                 desc: 'Letreros y anuncios exteriores' },
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
    // Fetch from the quotes API which checks the Cotizacion entity version, avoiding pessimistic locks.
    quotesApi.getCoberturas(folio)
      .then((res: unknown) => {
        const data = res as CoverageOptions & { version: number };
        setVersion(data.version);
        // Exclude version/warnings from the options state
        const loaded: CoverageOptions = { ...DEFAULTS };
        Object.keys(DEFAULTS).forEach(k => {
          const key = k as keyof CoverageOptions;
          if (data[key] !== undefined) loaded[key] = data[key];
        });
        setOptions(loaded);
      })
      .catch((err) => {
        // If 404 (Cotización no existe aún), we rely on default options, 
        // but we can't save without Cotizacion state.
        if (err.status === 404) {
          setError('La cotización principal no existe. Asegúrate de registrar las ubicaciones primero.');
        } else {
          setError('No se pudo cargar la configuración de coberturas.');
        }
      });
  }, [folio]);

  const selectedCount = Object.values(options).filter(Boolean).length;

  function toggle(key: keyof CoverageOptions) {
    if (!version) return; // Disables toggle if unloaded
    setOptions(prev => ({ ...prev, [key]: !prev[key] }));
    setError('');
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (selectedCount === 0) { setError('Selecciona al menos una cobertura'); return; }
    if (version === null) { setError('Cargando cotización, intenta de nuevo'); return; }
    
    setSaving(true);
    setError('');
    
    try {
      await quotesApi.putCoberturas(folio, version, options);
      router.push(`/cotizador/${folio}/calculo`);
    } catch (err: unknown) {
      const ex = err as { detail?: string; title?: string };
      setError(ex.detail ?? ex.title ?? 'Error al guardar coberturas');
      
      // If version conflict, auto-recover or prompt reload
      if (ex.detail?.includes('Conflicto de versión')) {
         quotesApi.getCoberturas(folio).then((res: any) => setVersion(res.version)).catch(()=>{});
         setError('Detectamos un cambio desde otra ventana. Versión actualizada; intenta guardar nuevamente.');
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      {/* ── Header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title" style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--cream)', letterSpacing: '-0.03em', marginBottom: '8px' }}>
          Coberturas
        </h1>
        <p className="page-subtitle" style={{ fontSize: '0.9375rem', color: 'var(--muted)' }}>
          Configura los riesgos que se ampararán en esta póliza.
        </p>
      </div>

      <div style={{
        backgroundColor: 'rgba(0, 200, 150, 0.08)',
        border: '1px solid rgba(0, 200, 150, 0.3)',
        borderRadius: '8px', padding: '16px', marginBottom: '24px',
        color: 'var(--cream)', fontSize: '0.875rem'
      }}>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '8px', color: 'var(--accent)', fontWeight: 600 }}>
          <IconShieldCheck /> Entiende las Coberturas
        </div>
        Aquí decides qué riesgos asume la aseguradora. <strong>¡Atención!</strong> Si activas catástrofes como <em>Catástrofe TEV</em> o <em>Catástrofe FHM</em>, el motor multiplicará matemáticamente la exposición basándose en el <strong>Código Postal</strong> de las ubicaciones que registraste antes, encareciendo sensiblemente la prima según el nivel de peligrosidad geográfica (zonas).
      </div>

      <form onSubmit={handleSubmit}>

        <div style={{
          backgroundColor: 'var(--surface)',
          border: '1px solid var(--border)',
          borderRadius: '12px',
          overflow: 'hidden',
          boxShadow: '0 4px 24px -12px rgba(15,21,32,0.06)'
        }}>
          {/* Section Head */}
          <div style={{
            padding: '24px 32px',
            borderBottom: '1px solid var(--border)',
            backgroundColor: 'var(--surface-2)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: '12px'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <div style={{
                width: '32px', height: '32px',
                backgroundColor: 'rgba(0,200,150,0.12)',
                color: 'var(--accent)',
                borderRadius: '8px',
                display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>
                <IconShieldCheck />
              </div>
              <h2 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--cream)', margin: 0 }}>
                Opciones disponibles
              </h2>
            </div>
            
            <div style={{
              backgroundColor: selectedCount > 0 ? 'var(--accent)' : 'var(--border-2)',
              color: selectedCount > 0 ? '#08120E' : 'var(--muted-2)',
              padding: '6px 12px', borderRadius: '20px',
              fontSize: '0.75rem', fontWeight: 700, transition: 'all 200ms'
            }}>
              {selectedCount} seleccionadas
            </div>
          </div>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3" style={{ padding: '32px', gap: '16px' }}>
            {COVERAGE_LABELS.map(({ key, label, desc }) => {
              const on = options[key];
              return (
                <button
                  key={key}
                  type="button"
                  disabled={!version}
                  onClick={() => toggle(key)}
                  style={{
                    position: 'relative',
                    textAlign: 'left',
                    padding: '20px',
                    borderRadius: '8px',
                    backgroundColor: on ? 'rgba(0,200,150,0.04)' : 'var(--surface-2)',
                    border: `1px solid ${on ? 'var(--accent-dim)' : 'var(--border)'}`,
                    transition: 'all 200ms ease',
                    cursor: !version ? 'not-allowed' : 'pointer',
                    opacity: !version ? 0.6 : 1,
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '12px',
                  }}
                  onMouseEnter={e => {
                    if (!on && version) e.currentTarget.style.borderColor = 'var(--border-2)';
                  }}
                  onMouseLeave={e => {
                    if (!on && version) e.currentTarget.style.borderColor = 'var(--border)';
                  }}
                >
                  {/* Fake Checkbox */}
                  <div style={{
                    width: '20px', height: '20px',
                    borderRadius: '4px',
                    border: `1.5px solid ${on ? 'var(--accent)' : 'var(--border-2)'}`,
                    backgroundColor: on ? 'var(--accent)' : 'transparent',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    transition: 'all 150ms'
                  }}>
                    {on && (
                      <svg width="12" height="10" viewBox="0 0 12 12" fill="none">
                        <path d="M2.5 6L5 8.5L9.5 3.5" stroke="#FFFFFF" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    )}
                  </div>
                  
                  <div>
                    <span style={{
                      display: 'block',
                      fontSize: '0.875rem',
                      fontWeight: 600,
                      color: on ? 'var(--cream)' : 'var(--muted)',
                      marginBottom: '4px',
                      transition: 'color 150ms ease',
                    }}>
                      {label}
                    </span>
                    <span style={{ display: 'block', fontSize: '0.75rem', color: 'var(--muted-2)', lineHeight: 1.4 }}>
                      {desc}
                    </span>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {error && (
          <div className="error-banner" style={{
            marginTop: '24px',
            backgroundColor: 'rgba(229,62,62,0.08)',
            border: '1px solid rgba(229,62,62,0.3)',
            borderRadius: '8px',
            padding: '16px',
            color: 'var(--danger)',
            display: 'flex',
            alignItems: 'center',
            gap: '12px'
          }}>
            <IconInfo />
            <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>{error}</span>
          </div>
        )}

        {/* ════ STICKY FOOTER ACTIONS ════ */}
        <div className="floating-footer">
          <Button 
            type="submit" 
            loading={saving} 
            disabled={saving || !version} 
            id="btn-continuar-calculo" 
            style={{ padding: '12px 24px', fontSize: '0.9375rem', display: 'flex', alignItems: 'center', gap: '8px' }}
          >
            {saving ? 'Guardando…' : <>Continuar al Paso 5</>}
            {!saving && (
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                <path d="M2 7h10M8 3l4 4-4 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            )}
          </Button>
          
          <Button
            type="button"
            variant="ghost"
            onClick={() => router.push(`/cotizador/${folio}/ubicaciones`)}
            style={{ padding: '12px 24px' }}
          >
            Atrás: Ubicaciones
          </Button>
        </div>
      </form>
    </div>
  );
}
