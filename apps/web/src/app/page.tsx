'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { generateIdempotencyKey } from '@/lib/utils';

/* ─── SVG Icon components ─── */
function IconClipboard() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
      <rect x="9" y="3" width="6" height="4" rx="1"/>
      <line x1="9" y1="12" x2="15" y2="12"/>
      <line x1="9" y1="16" x2="13" y2="16"/>
    </svg>
  );
}

function IconLayout() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="18" height="18" rx="2"/>
      <line x1="3" y1="9" x2="21" y2="9"/>
      <line x1="9" y1="9" x2="9" y2="21"/>
    </svg>
  );
}

function IconMapPin() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
      <circle cx="12" cy="9" r="2.5"/>
    </svg>
  );
}

function IconShield() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
      <polyline points="9 12 11 14 15 10"/>
    </svg>
  );
}

function IconCalculator() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <rect x="4" y="2" width="16" height="20" rx="2"/>
      <line x1="8" y1="6" x2="16" y2="6"/>
      <line x1="8" y1="10" x2="8" y2="10" strokeWidth="2.5"/>
      <line x1="12" y1="10" x2="12" y2="10" strokeWidth="2.5"/>
      <line x1="16" y1="10" x2="16" y2="10" strokeWidth="2.5"/>
      <line x1="8" y1="14" x2="8" y2="14" strokeWidth="2.5"/>
      <line x1="12" y1="14" x2="12" y2="14" strokeWidth="2.5"/>
      <line x1="16" y1="14" x2="16" y2="18"/>
      <line x1="8" y1="18" x2="8" y2="18" strokeWidth="2.5"/>
      <line x1="12" y1="18" x2="12" y2="18" strokeWidth="2.5"/>
    </svg>
  );
}

/* Brand logo mark — shield with chart line */
function LogoMark({ size = 32 }: { size?: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 32 32" fill="none">
      <rect width="32" height="32" fill="#00C896" />
      {/* Shield outline */}
      <path
        d="M16 5L7 8.5V15c0 5.2 3.9 9.5 9 10.5 5.1-1 9-5.3 9-10.5V8.5L16 5z"
        fill="rgba(255,255,255,0.15)"
        stroke="rgba(255,255,255,0.4)"
        strokeWidth="1"
      />
      {/* Trend line inside */}
      <polyline
        points="10,18 13,13 16,15.5 19,10 22,12"
        stroke="white"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
        fill="none"
      />
      {/* Dot on last point */}
      <circle cx="22" cy="12" r="1.5" fill="white" />
    </svg>
  );
}

/* Arrow right icon */
function IconArrow() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M2 7h10M8 3l4 4-4 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

/* Check circle icon */
function IconCheck() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <circle cx="8" cy="8" r="8" fill="#00C896" />
      <path d="M5 8l2.5 2.5L11 6" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

/* Spinner icon */
function IconSpinner() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" style={{ animation: 'spin 0.9s linear infinite' }}>
      <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"/>
    </svg>
  );
}

/* Plus icon */
function IconPlus() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <path d="M8 3v10M3 8h10" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"/>
    </svg>
  );
}

/* ─── Step data ─── */
const STEPS = [
  { num: '01', Icon: IconClipboard, title: 'Datos generales',   desc: 'Información del tomador del seguro y el tipo de bien a asegurar.' },
  { num: '02', Icon: IconLayout,    title: 'Layout de póliza',  desc: 'Cuántas ubicaciones tiene la póliza y qué secciones aplican.' },
  { num: '03', Icon: IconMapPin,    title: 'Ubicaciones',       desc: 'Dirección, tipo constructivo y valores de cada propiedad.' },
  { num: '04', Icon: IconShield,    title: 'Coberturas',        desc: 'Riesgos cubiertos: incendio, catástrofe, robo y más.' },
  { num: '05', Icon: IconCalculator,title: 'Cálculo de prima',  desc: 'Prima neta y comercial con desglose completo por ubicación.' },
];

/* ─── Page ─────────────────────────────────────────────── */
export default function LandingPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [folio, setFolioInput] = useState('');

  async function crearFolio() {
    setLoading(true);
    setError('');
    try {
      const key = generateIdempotencyKey();
      const res = await foliosApi.crear(key) as { numeroFolio: string };
      router.push(`/cotizador/${res.numeroFolio}/datos-generales`);
    } catch (e: unknown) {
      const err = e as { detail?: string };
      setError(err.detail || 'No se pudo conectar con el servidor. Verifica que el servicio esté activo.');
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
    <main style={{ minHeight: '100vh', backgroundColor: '#F4F6FB', color: '#0F1520', fontFamily: 'var(--font-sans)' }}>

      {/* ═══════ NAVBAR ═══════ */}
      <nav style={{
        backgroundColor: '#ffffff',
        borderBottom: '1px solid #E2E6EF',
        padding: '0 40px',
        height: '64px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        position: 'sticky',
        top: 0,
        zIndex: 50,
        boxShadow: '0 1px 3px rgba(15,21,32,0.06)',
      }}>
        {/* Brand */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <LogoMark size={34} />
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1px' }}>
            <span style={{ fontWeight: 700, fontSize: '0.9375rem', color: '#0F1520', letterSpacing: '-0.02em', lineHeight: 1.1 }}>
              Cotizador
            </span>
            <span style={{ fontSize: '0.6875rem', color: '#8A94A8', letterSpacing: '0.01em', lineHeight: 1 }}>
              Seguros de Daños Comerciales
            </span>
          </div>
        </div>

        {/* Badge */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          backgroundColor: '#F0F2F8',
          padding: '6px 14px',
          border: '1px solid #E2E6EF',
        }}>
          <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
            <circle cx="6" cy="6" r="5.5" stroke="#00C896" strokeWidth="1"/>
            <circle cx="6" cy="6" r="2.5" fill="#00C896"/>
          </svg>
          <span style={{ fontSize: '0.6875rem', fontWeight: 600, color: '#6B7B94', letterSpacing: '0.06em', textTransform: 'uppercase' }}>
            Sofka · Reto IA Center
          </span>
        </div>
      </nav>

      {/* ═══════ HERO ═══════ */}
      <section style={{
        background: 'linear-gradient(140deg, #0B1222 0%, #152040 55%, #0F2E30 100%)',
        padding: '80px 40px',
        color: '#fff',
        overflow: 'hidden',
        position: 'relative',
      }}>
        {/* Radial glow */}
        <div aria-hidden="true" style={{
          position: 'absolute', inset: 0,
          backgroundImage: 'radial-gradient(ellipse at 65% 40%, rgba(0,200,150,0.14) 0%, transparent 65%)',
          pointerEvents: 'none',
        }} />
        {/* Grid overlay */}
        <div aria-hidden="true" style={{
          position: 'absolute', inset: 0,
          backgroundImage:
            'linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px)',
          backgroundSize: '48px 48px',
          pointerEvents: 'none',
        }} />

        <div style={{ maxWidth: '1120px', margin: '0 auto', position: 'relative' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '64px', alignItems: 'center' }}>

            {/* LEFT — Copy */}
            <div>
              <div style={{
                display: 'inline-flex', alignItems: 'center', gap: '8px',
                backgroundColor: 'rgba(0,200,150,0.12)',
                border: '1px solid rgba(0,200,150,0.3)',
                color: '#00C896',
                padding: '5px 14px',
                fontSize: '0.6875rem',
                fontWeight: 700,
                letterSpacing: '0.1em',
                textTransform: 'uppercase',
                marginBottom: '28px',
              }}>
                <svg width="8" height="8" viewBox="0 0 8 8" fill="none">
                  <circle cx="4" cy="4" r="4" fill="#00C896"/>
                </svg>
                Sistema de cotización profesional
              </div>

              <h1 style={{
                fontSize: 'clamp(2rem, 4vw, 3rem)',
                fontWeight: 800,
                lineHeight: 1.12,
                letterSpacing: '-0.035em',
                color: '#FFFFFF',
                marginBottom: '20px',
              }}>
                Calcula primas de&nbsp;seguros
                <br />
                <span style={{ color: '#00C896' }}>comerciales en minutos</span>
              </h1>

              <p style={{
                fontSize: '1rem',
                lineHeight: 1.75,
                color: 'rgba(255,255,255,0.6)',
                maxWidth: '440px',
                marginBottom: '40px',
              }}>
                Ingresa los datos del tomador, las ubicaciones y coberturas.
                Nuestro motor calcula la prima neta y comercial con desglose
                completo y trazabilidad total.
              </p>

              {/* Primary CTA */}
              <button
                id="btn-nueva-cotizacion"
                onClick={crearFolio}
                disabled={loading}
                style={{
                  backgroundColor: '#00C896',
                  color: '#08120E',
                  border: 'none',
                  padding: '14px 28px',
                  fontSize: '0.9375rem',
                  fontWeight: 700,
                  cursor: loading ? 'not-allowed' : 'pointer',
                  opacity: loading ? 0.75 : 1,
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '10px',
                  letterSpacing: '-0.01em',
                  transition: 'opacity 150ms, background-color 150ms',
                }}
                onMouseEnter={e => { if (!loading) e.currentTarget.style.backgroundColor = '#00B085'; }}
                onMouseLeave={e => { e.currentTarget.style.backgroundColor = '#00C896'; }}
              >
                {loading ? <><IconSpinner /> Creando folio…</> : <><IconPlus /> Iniciar nueva cotización</>}
              </button>

              {error && (
                <div style={{
                  marginTop: '20px',
                  backgroundColor: 'rgba(240,68,71,0.1)',
                  border: '1px solid rgba(240,68,71,0.3)',
                  borderLeft: '3px solid #F04447',
                  color: '#FF8080',
                  padding: '10px 16px',
                  fontSize: '0.8125rem',
                  maxWidth: '420px',
                  lineHeight: 1.6,
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: '8px',
                }}>
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none" style={{ flexShrink: 0, marginTop: '1px' }}>
                    <circle cx="7" cy="7" r="6.5" stroke="#F04447"/>
                    <path d="M7 4v3.5M7 10h.01" stroke="#F04447" strokeWidth="1.5" strokeLinecap="round"/>
                  </svg>
                  {error}
                </div>
              )}
            </div>

            {/* RIGHT — Stat cards */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '2px' }}>
              {[
                {
                  label: 'Proceso guiado en',
                  value: '5 pasos',
                  sub: 'De datos hasta el cálculo final',
                  icon: (
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="rgba(0,200,150,0.6)" strokeWidth="1.5" strokeLinecap="round">
                      <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                    </svg>
                  ),
                },
                {
                  label: 'Coberturas disponibles',
                  value: '14+',
                  sub: 'Incendio, catástrofe, robo y más',
                  icon: (
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="rgba(0,200,150,0.6)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                    </svg>
                  ),
                },
                {
                  label: 'Resultado final',
                  value: 'Prima neta + comercial',
                  sub: 'Con desglose completo por ubicación',
                  icon: (
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="rgba(0,200,150,0.6)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                      <rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>
                    </svg>
                  ),
                },
              ].map(({ label, value, sub, icon }) => (
                <div key={label} style={{
                  backgroundColor: 'rgba(255,255,255,0.05)',
                  border: '1px solid rgba(255,255,255,0.08)',
                  padding: '20px 24px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '20px',
                  transition: 'background-color 200ms',
                }}>
                  <div style={{
                    width: '44px', height: '44px',
                    backgroundColor: 'rgba(0,200,150,0.08)',
                    border: '1px solid rgba(0,200,150,0.15)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    flexShrink: 0,
                  }}>
                    {icon}
                  </div>
                  <div>
                    <p style={{ fontSize: '0.6875rem', color: 'rgba(255,255,255,0.38)', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: '3px' }}>
                      {label}
                    </p>
                    <p style={{ fontSize: '1.0625rem', fontWeight: 700, color: '#00C896', letterSpacing: '-0.02em', marginBottom: '3px', lineHeight: 1 }}>
                      {value}
                    </p>
                    <p style={{ fontSize: '0.75rem', color: 'rgba(255,255,255,0.38)' }}>{sub}</p>
                  </div>
                </div>
              ))}
            </div>

          </div>
        </div>
      </section>

      {/* ═══════ PROCESO ═══════ */}
      <section style={{ padding: '80px 40px', backgroundColor: '#F4F6FB' }}>
        <div style={{ maxWidth: '1120px', margin: '0 auto' }}>

          <div style={{ textAlign: 'center', marginBottom: '56px' }}>
            <p style={{ fontSize: '0.6875rem', fontWeight: 700, letterSpacing: '0.14em', textTransform: 'uppercase', color: '#00C896', marginBottom: '12px' }}>
              Flujo de trabajo
            </p>
            <h2 style={{ fontSize: 'clamp(1.5rem, 3vw, 2.25rem)', fontWeight: 800, letterSpacing: '-0.03em', color: '#0F1520', marginBottom: '14px' }}>
              ¿Cómo funciona?
            </h2>
            <p style={{ fontSize: '1rem', color: '#6B7B94', maxWidth: '500px', margin: '0 auto', lineHeight: 1.7 }}>
              El sistema te guía paso a paso desde los datos del cliente hasta el resultado final de la prima.
            </p>
          </div>

          {/* ── Steps ── */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(5, 1fr)',
            gap: '0',
            position: 'relative',
          }}>
            {/* Connector line behind cards */}
            <div aria-hidden="true" style={{
              position: 'absolute',
              top: '36px',
              left: '10%',
              right: '10%',
              height: '2px',
              backgroundColor: '#DDE2EF',
              zIndex: 0,
            }} />

            {STEPS.map(({ num, Icon, title, desc }) => (
              <div key={num} style={{ padding: '0 8px', position: 'relative', zIndex: 1 }}>
                <div style={{
                  backgroundColor: '#ffffff',
                  border: '1px solid #E2E6EF',
                  padding: '24px 16px 20px',
                  textAlign: 'center',
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                }}>
                  {/* Number bubble */}
                  <div style={{
                    width: '40px', height: '40px',
                    backgroundColor: '#00C896',
                    borderRadius: '50%',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    marginBottom: '16px',
                    border: '3px solid #F4F6FB',
                    outline: '1px solid rgba(0,200,150,0.35)',
                    fontFamily: 'var(--font-mono)',
                    fontSize: '0.6875rem',
                    fontWeight: 700,
                    color: '#08120E',
                    flexShrink: 0,
                  }}>
                    {num}
                  </div>

                  {/* Icon */}
                  <div style={{
                    color: '#00A87E',
                    marginBottom: '12px',
                    width: '44px', height: '44px',
                    backgroundColor: '#E8FAF5',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    <Icon />
                  </div>

                  <h3 style={{
                    fontSize: '0.875rem',
                    fontWeight: 700,
                    color: '#0F1520',
                    marginBottom: '8px',
                    letterSpacing: '-0.01em',
                    lineHeight: 1.3,
                  }}>
                    {title}
                  </h3>
                  <p style={{ fontSize: '0.75rem', color: '#8A94A8', lineHeight: 1.6 }}>
                    {desc}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ═══════ ACCIONES ═══════ */}
      <section style={{ padding: '0 40px 80px' }}>
        <div style={{ maxWidth: '1120px', margin: '0 auto', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>

          {/* ── Card Nueva cotización ── */}
          <div style={{
            backgroundColor: '#ffffff',
            border: '2px solid #00C896',
            padding: '40px',
            display: 'flex',
            flexDirection: 'column',
            gap: '24px',
            position: 'relative',
            overflow: 'hidden',
          }}>
            {/* Top accent stripe */}
            <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: '4px', backgroundColor: '#00C896' }} />

            <div>
              <div style={{
                display: 'inline-flex', alignItems: 'center', gap: '6px',
                backgroundColor: '#E8FAF6',
                color: '#00916A',
                fontSize: '0.6875rem',
                fontWeight: 700,
                padding: '4px 12px',
                letterSpacing: '0.08em',
                textTransform: 'uppercase',
                marginBottom: '16px',
              }}>
                <svg width="10" height="10" viewBox="0 0 10 10" fill="none">
                  <circle cx="5" cy="5" r="5" fill="#00C896"/>
                  <path d="M5 2v6M2 5h6" stroke="white" strokeWidth="1.5" strokeLinecap="round"/>
                </svg>
                Nuevo proceso
              </div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0F1520', letterSpacing: '-0.025em', marginBottom: '12px' }}>
                Iniciar una nueva cotización
              </h3>
              <p style={{ fontSize: '0.875rem', color: '#6B7B94', lineHeight: 1.65 }}>
                Crea un nuevo folio de cotización. El sistema generará un código único
                y te guiará por los 5 pasos del proceso hasta el cálculo de la prima.
              </p>
            </div>

            <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {[
                'Folio único generado automáticamente',
                'Proceso guiado paso a paso',
                'Cálculo de prima en tiempo real',
              ].map(f => (
                <li key={f} style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '0.8125rem', color: '#374151' }}>
                  <IconCheck />
                  {f}
                </li>
              ))}
            </ul>

            <button
              id="btn-nueva-cotizacion-card"
              onClick={crearFolio}
              disabled={loading}
              style={{
                backgroundColor: '#00C896',
                color: '#08120E',
                border: 'none',
                padding: '14px 24px',
                fontSize: '0.9375rem',
                fontWeight: 700,
                cursor: loading ? 'not-allowed' : 'pointer',
                opacity: loading ? 0.7 : 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
                width: '100%',
                transition: 'background-color 150ms, opacity 150ms',
                letterSpacing: '-0.01em',
              }}
              onMouseEnter={e => { if (!loading) e.currentTarget.style.backgroundColor = '#00B085'; }}
              onMouseLeave={e => { e.currentTarget.style.backgroundColor = '#00C896'; }}
            >
              {loading ? <><IconSpinner /> Creando folio…</> : <>Iniciar cotización <IconArrow /></>}
            </button>
          </div>

          {/* ── Card Continuar folio ── */}
          <div style={{
            backgroundColor: '#ffffff',
            border: '1px solid #E2E6EF',
            padding: '40px',
            display: 'flex',
            flexDirection: 'column',
            gap: '24px',
            position: 'relative',
            overflow: 'hidden',
          }}>
            <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: '4px', backgroundColor: '#E2E6EF' }} />

            <div>
              <div style={{
                display: 'inline-flex', alignItems: 'center', gap: '6px',
                backgroundColor: '#F0F2F8',
                color: '#6B7B94',
                fontSize: '0.6875rem',
                fontWeight: 700,
                padding: '4px 12px',
                letterSpacing: '0.08em',
                textTransform: 'uppercase',
                marginBottom: '16px',
              }}>
                <svg width="10" height="10" viewBox="0 0 10 10" fill="none">
                  <circle cx="5" cy="5" r="4.5" stroke="#8A94A8"/>
                  <path d="M3 5h4M6 3l2 2-2 2" stroke="#8A94A8" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                Retomar proceso
              </div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0F1520', letterSpacing: '-0.025em', marginBottom: '12px' }}>
                Continuar una cotización existente
              </h3>
              <p style={{ fontSize: '0.875rem', color: '#6B7B94', lineHeight: 1.65 }}>
                ¿Ya tienes un folio? Ingresa el código y retoma desde donde lo dejaste.
                Puedes completar secciones pendientes o ejecutar el cálculo.
              </p>
            </div>

            <form onSubmit={continuarFolio} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label
                  htmlFor="input-folio"
                  style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: '#374151', marginBottom: '8px', letterSpacing: '0.01em' }}
                >
                  Número de folio
                </label>
                <div style={{ position: 'relative' }}>
                  <div style={{
                    position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)',
                    color: '#A0ADC0',
                    pointerEvents: 'none',
                  }}>
                    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                      <rect x="2" y="1" width="10" height="12" rx="1" stroke="#A0ADC0" strokeWidth="1.2"/>
                      <line x1="4.5" y1="5" x2="9.5" y2="5" stroke="#A0ADC0" strokeWidth="1.2" strokeLinecap="round"/>
                      <line x1="4.5" y1="7.5" x2="9.5" y2="7.5" stroke="#A0ADC0" strokeWidth="1.2" strokeLinecap="round"/>
                      <line x1="4.5" y1="10" x2="7" y2="10" stroke="#A0ADC0" strokeWidth="1.2" strokeLinecap="round"/>
                    </svg>
                  </div>
                  <input
                    id="input-folio"
                    value={folio}
                    onChange={e => setFolioInput(e.target.value)}
                    placeholder="Ej: F2026-0042"
                    style={{
                      width: '100%',
                      border: '1px solid #D1D9E8',
                      backgroundColor: '#F8FAFF',
                      color: '#0F1520',
                      padding: '12px 16px 12px 36px',
                      fontSize: '0.9375rem',
                      fontFamily: 'var(--font-mono)',
                      letterSpacing: '0.04em',
                      outline: 'none',
                      boxSizing: 'border-box',
                      transition: 'border-color 150ms, box-shadow 150ms',
                    }}
                    onFocus={e => {
                      e.currentTarget.style.borderColor = '#00C896';
                      e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,200,150,0.1)';
                    }}
                    onBlur={e => {
                      e.currentTarget.style.borderColor = '#D1D9E8';
                      e.currentTarget.style.boxShadow = 'none';
                    }}
                    aria-label="Número de folio existente"
                  />
                </div>
                <p style={{ fontSize: '0.6875rem', color: '#A0ADC0', marginTop: '6px', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <svg width="10" height="10" viewBox="0 0 10 10" fill="none">
                    <circle cx="5" cy="5" r="4.5" stroke="#A0ADC0" strokeWidth="1"/>
                    <path d="M5 4v2.5M5 7.5h.01" stroke="#A0ADC0" strokeWidth="1.1" strokeLinecap="round"/>
                  </svg>
                  El folio fue generado al crear la cotización (ej: F2026-0001)
                </p>
              </div>

              <button
                type="submit"
                id="btn-continuar-folio"
                disabled={!folio.trim()}
                style={{
                  backgroundColor: 'transparent',
                  color: folio.trim() ? '#0F1520' : '#A0ADC0',
                  border: `2px solid ${folio.trim() ? '#C5CDD E' : '#E2E6EF'}`,
                  borderColor: folio.trim() ? '#0F1520' : '#D1D9E8',
                  padding: '12px 24px',
                  fontSize: '0.9375rem',
                  fontWeight: 600,
                  cursor: folio.trim() ? 'pointer' : 'not-allowed',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px',
                  width: '100%',
                  transition: 'border-color 150ms, color 150ms',
                  letterSpacing: '-0.01em',
                }}
              >
                Ver estado del folio <IconArrow />
              </button>
            </form>
          </div>

        </div>

        {error && (
          <div style={{
            maxWidth: '1120px',
            margin: '16px auto 0',
            backgroundColor: '#FFF5F5',
            border: '1px solid rgba(240,68,71,0.25)',
            borderLeft: '3px solid #F04447',
            color: '#C53030',
            padding: '12px 16px',
            fontSize: '0.8125rem',
            lineHeight: 1.5,
          }}>
            {error}
          </div>
        )}
      </section>

      {/* ═══════ FOOTER ═══════ */}
      <footer style={{
        backgroundColor: '#0B1222',
        borderTop: '1px solid #1A2540',
        padding: '24px 40px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <LogoMark size={22} />
          <span style={{ fontSize: '0.8125rem', color: 'rgba(255,255,255,0.4)', letterSpacing: '-0.01em' }}>
            Cotizador de Seguros de Daños
          </span>
        </div>
        <span style={{ fontSize: '0.6875rem', color: 'rgba(255,255,255,0.25)', fontFamily: 'var(--font-mono)', letterSpacing: '0.06em' }}>
          SOFKA · RETO IA CENTER
        </span>
      </footer>

      <style>{`
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
      `}</style>
    </main>
  );
}
