'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { FieldError } from '@/components/FieldError';
import { Button } from '@/components/ui/Button';
import { Field, Input, Select, Textarea } from '@/components/ui/Input';

/* ─── Icons ─── */
function IconUser() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>;
}
function IconId() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="16" rx="2" ry="2"/><line x1="7" y1="8" x2="17" y2="8"/><line x1="7" y1="12" x2="17" y2="12"/><line x1="7" y1="16" x2="11" y2="16"/></svg>;
}
function IconMail() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m2 4 10 8 10-8"/></svg>;
}
function IconPhone() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>;
}
function IconBuilding() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><rect x="4" y="2" width="16" height="20" rx="2" ry="2"/><path d="M9 22v-4h6v4"/><path d="M8 6h.01"/><path d="M16 6h.01"/><path d="M12 6h.01"/><path d="M12 10h.01"/><path d="M12 14h.01"/><path d="M16 10h.01"/><path d="M16 14h.01"/><path d="M8 10h.01"/><path d="M8 14h.01"/></svg>;
}
function IconBriefcase() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>;
}
function IconCalendar() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>;
}
function IconLayers() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 12 12 17 22 12"/><polyline points="2 17 12 22 22 17"/></svg>;
}
function IconText() {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><line x1="21" y1="6" x2="3" y2="6"/><line x1="15" y1="12" x2="3" y2="12"/><line x1="17" y1="18" x2="3" y2="18"/></svg>;
}

const TIPOS_INMUEBLE = ['CASA', 'EDIFICIO', 'LOCAL_COMERCIAL', 'BODEGA'] as const;
const USOS = ['HABITACIONAL', 'COMERCIAL', 'MIXTO'] as const;

interface FormState {
  nombreTomador: string;
  rucCedula: string;
  correoElectronico: string;
  telefonoContacto: string;
  tipoInmueble: string;
  usoPrincipal: string;
  anoConstruccion: string;
  numeroPisos: string;
  descripcion: string;
}

export default function DatosGeneralesPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState(false);
  const [apiError, setApiError] = useState('');
  const [form, setForm] = useState<FormState>({
    nombreTomador: '', rucCedula: '', correoElectronico: '',
    telefonoContacto: '', tipoInmueble: '',
    usoPrincipal: '', anoConstruccion: '', numeroPisos: '',
    descripcion: '',
  });
  const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});

  function validate() {
    const e: Partial<Record<keyof FormState, string>> = {};
    if (!form.nombreTomador.trim()) e.nombreTomador = 'Campo requerido';
    if (!form.rucCedula.trim()) e.rucCedula = 'Campo requerido';
    if (!form.correoElectronico.trim()) e.correoElectronico = 'Campo requerido';
    if (!form.tipoInmueble) e.tipoInmueble = 'Requerido';
    if (!form.usoPrincipal) e.usoPrincipal = 'Requerido';
    if (form.correoElectronico && !/\S+@\S+\.\S+/.test(form.correoElectronico))
      e.correoElectronico = 'Correo inválido';
    return e;
  }

  async function handleSubmit(e: { preventDefault(): void }) {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    setApiError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      await foliosApi.putDatosGenerales(folio, estado.version, {
        ...form,
        anoConstruccion: form.anoConstruccion ? Number.parseInt(form.anoConstruccion) : null,
        numeroPisos: form.numeroPisos ? Number.parseInt(form.numeroPisos) : null,
      });
      router.push(`/cotizador/${folio}/layout`);
    } catch (err: unknown) {
      const ex = err as { detail?: string };
      setApiError(ex.detail ?? 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  function fieldProps(key: keyof FormState) {
    return {
      id: key,
      value: form[key],
      onChange: (e: { target: { value: string } }) => setForm(f => ({ ...f, [key]: e.target.value })),
    };
  }

  return (
    <div>
      {/* ── Page header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title" style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--cream)', letterSpacing: '-0.03em', marginBottom: '8px' }}>
          Datos Generales
        </h1>
        <p className="page-subtitle" style={{ fontSize: '0.9375rem', color: 'var(--muted)' }}>
          Ingresa la información base del tomador del seguro y la caracterización principal del bien.
        </p>
      </div>

      <div style={{
        backgroundColor: 'rgba(0, 200, 150, 0.08)',
        border: '1px solid rgba(0, 200, 150, 0.3)',
        borderRadius: '8px', padding: '16px', marginBottom: '24px',
        color: 'var(--cream)', fontSize: '0.875rem'
      }}>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '8px', color: 'var(--accent)', fontWeight: 600 }}>
          <IconId /> ¿Por qué pedimos esto?
        </div>
        Los Datos Generales establecen el contrato base (Tomador) y validan el perfil de riesgo primario. Información falsa en el RUC/Cédula o Tipo de Inmueble impedirá la posterior emisión oficial de la póliza en el Core de la aseguradora.
      </div>

      <form onSubmit={handleSubmit}>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
          {/* ════ CARD 1: TOMADOR DEL SEGURO ════ */}
          <div style={{
            backgroundColor: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: '12px',
            overflow: 'hidden',
            boxShadow: '0 4px 24px -12px rgba(15,21,32,0.06)'
          }}>
            <div style={{
              padding: '24px 32px',
              borderBottom: '1px solid var(--border)',
              backgroundColor: 'var(--surface-2)',
              display: 'flex',
              alignItems: 'center',
              gap: '12px'
            }}>
              <div style={{
                width: '32px', height: '32px',
                backgroundColor: 'rgba(0,200,150,0.12)',
                color: 'var(--accent)',
                borderRadius: '8px',
                display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>
                <IconUser />
              </div>
              <h2 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--cream)', margin: 0 }}>
                1. Información del tomador
              </h2>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2" style={{ padding: '32px', gap: '24px' }}>
              <div className="col-span-full">
                <Field id="nombreTomador" label="Nombre o Razón social" required error={errors.nombreTomador}>
                  <Input
                    icon={<IconUser />}
                    placeholder="Empresa Ejemplo S.A."
                    error={!!errors.nombreTomador}
                    {...fieldProps('nombreTomador')}
                  />
                </Field>
              </div>

              <Field id="rucCedula" label="RUC / Cédula" required error={errors.rucCedula}>
                <Input
                  icon={<IconId />}
                  placeholder="1792345678001"
                  error={!!errors.rucCedula}
                  {...fieldProps('rucCedula')}
                />
              </Field>

              <Field id="correoElectronico" label="Correo electrónico" required error={errors.correoElectronico}>
                <Input
                  icon={<IconMail />}
                  type="email"
                  placeholder="contacto@empresa.com"
                  error={!!errors.correoElectronico}
                  {...fieldProps('correoElectronico')}
                />
              </Field>

              <div className="md:col-span-1">
                <Field id="telefonoContacto" label="Teléfono de contacto">
                  <Input
                    icon={<IconPhone />}
                    placeholder="0991234567"
                    {...fieldProps('telefonoContacto')}
                  />
                </Field>
              </div>
            </div>
          </div>

          {/* ════ CARD 2: BIEN ASEGURADO ════ */}
          <div style={{
            backgroundColor: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: '12px',
            overflow: 'hidden',
            boxShadow: '0 4px 24px -12px rgba(15,21,32,0.06)'
          }}>
            <div style={{
              padding: '24px 32px',
              borderBottom: '1px solid var(--border)',
              backgroundColor: 'var(--surface-2)',
              display: 'flex',
              alignItems: 'center',
              gap: '12px'
            }}>
              <div style={{
                width: '32px', height: '32px',
                backgroundColor: 'rgba(0,200,150,0.12)',
                color: 'var(--accent)',
                borderRadius: '8px',
                display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>
                <IconBuilding />
              </div>
              <h2 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--cream)', margin: 0 }}>
                2. Características del bien
              </h2>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2" style={{ padding: '32px', gap: '24px' }}>
              <Field id="tipoInmueble" label="Tipo de inmueble" required error={errors.tipoInmueble}>
                <Select icon={<IconBuilding />} error={!!errors.tipoInmueble} {...fieldProps('tipoInmueble')}>
                  <option value="" disabled>Seleccione...</option>
                  {TIPOS_INMUEBLE.map(t => (
                    <option key={t} value={t}>{t.replaceAll('_', ' ')}</option>
                  ))}
                </Select>
              </Field>

              <Field id="usoPrincipal" label="Uso principal" required error={errors.usoPrincipal}>
                <Select icon={<IconBriefcase />} error={!!errors.usoPrincipal} {...fieldProps('usoPrincipal')}>
                  <option value="" disabled>Seleccione...</option>
                  {USOS.map(u => <option key={u} value={u}>{u}</option>)}
                </Select>
              </Field>

              <Field id="anoConstruccion" label="Año de construcción">
                <Input
                  icon={<IconCalendar />}
                  type="number"
                  placeholder="Ej: 2010"
                  {...fieldProps('anoConstruccion')}
                />
              </Field>

              <Field id="numeroPisos" label="Número de pisos">
                <Input
                  icon={<IconLayers />}
                  type="number"
                  placeholder="Ej: 3"
                  {...fieldProps('numeroPisos')}
                />
              </Field>

              <div className="col-span-full">
                <Field id="descripcion" label="Descripción general (Opcional)">
                  <Textarea
                    icon={<IconText />}
                    placeholder="Detalles adicionales sobre el estado o características del bien..."
                    style={{ resize: 'none', height: '80px' }}
                    {...fieldProps('descripcion')}
                  />
                </Field>
              </div>
            </div>
          </div>
        </div>

        {apiError && (
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
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="2"/>
              <path d="M10 6v4M10 14h.01" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"/>
            </svg>
            <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>{apiError}</span>
          </div>
        )}

        {/* ════ STICKY FOOTER ACTIONS ════ */}
        <div className="floating-footer">
          <Button type="submit" loading={saving} id="btn-guardar-datos-generales" style={{ padding: '12px 24px', fontSize: '0.9375rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            {saving ? 'Guardando…' : <>Continuar al Paso 2</>}
            {!saving && (
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                <path d="M2 7h10M8 3l4 4-4 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            )}
          </Button>
          
          <Button
            type="button"
            variant="ghost"
            onClick={() => router.push(`/cotizador/${folio}/estado`)}
            id="btn-ver-estado"
            style={{ padding: '12px 24px' }}
          >
            Guardar como borrador
          </Button>
        </div>
      </form>
    </div>
  );
}
