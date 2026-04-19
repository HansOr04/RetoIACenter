'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { FieldError } from '@/components/FieldError';

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

const inputCls = 'w-full border text-sm px-4 py-3 focus:outline-none transition-colors';
const inputStyle = { backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' };
const onFocus = (e: { currentTarget: HTMLElement }) => (e.currentTarget.style.borderColor = '#00D9A3');
const onBlur  = (e: { currentTarget: HTMLElement }) => (e.currentTarget.style.borderColor = '#1E1E2A');

export default function DatosGeneralesPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState(false);
  const [apiError, setApiError] = useState('');
  const [form, setForm] = useState<FormState>({
    nombreTomador: '', rucCedula: '', correoElectronico: '',
    telefonoContacto: '', tipoInmueble: 'LOCAL_COMERCIAL',
    usoPrincipal: 'COMERCIAL', anoConstruccion: '', numeroPisos: '',
    descripcion: '',
  });
  const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});

  function validate() {
    const e: Partial<Record<keyof FormState, string>> = {};
    if (!form.nombreTomador.trim()) e.nombreTomador = 'Campo requerido';
    if (!form.rucCedula.trim()) e.rucCedula = 'Campo requerido';
    if (!form.correoElectronico.trim()) e.correoElectronico = 'Campo requerido';
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

  function field(key: keyof FormState) {
    return {
      id: key,
      value: form[key],
      onChange: (e: { target: { value: string } }) => setForm(f => ({ ...f, [key]: e.target.value })),
    };
  }

  const bc = (k: keyof FormState) => errors[k] ? '#FF4D4D' : '#1E1E2A';

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-semibold mb-1" style={{ color: '#F5F5F0' }}>Datos generales</h1>
        <p className="text-sm" style={{ color: '#6B6B7A' }}>
          Información del tomador y el bien asegurado.
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
          {/* Tomador */}
          <div className="space-y-5">
            <p className="text-[11px] font-semibold uppercase tracking-widest border-b pb-2"
              style={{ color: '#6B6B7A', borderColor: '#1E1E2A' }}>
              Tomador del seguro
            </p>

            <div>
              <label htmlFor="nombreTomador" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                Nombre / Razón social *
              </label>
              <input className={inputCls} placeholder="Empresa Ejemplo S.A."
                style={{ ...inputStyle, borderColor: bc('nombreTomador') }}
                onFocus={onFocus} onBlur={e => (e.currentTarget.style.borderColor = bc('nombreTomador'))}
                {...field('nombreTomador')} />
              <FieldError msg={errors.nombreTomador} />
            </div>

            <div>
              <label htmlFor="rucCedula" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                RUC / Cédula *
              </label>
              <input className={inputCls} placeholder="1792345678001"
                style={{ ...inputStyle, borderColor: bc('rucCedula') }}
                onFocus={onFocus} onBlur={e => (e.currentTarget.style.borderColor = bc('rucCedula'))}
                {...field('rucCedula')} />
              <FieldError msg={errors.rucCedula} />
            </div>

            <div>
              <label htmlFor="correoElectronico" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                Correo electrónico *
              </label>
              <input type="email" className={inputCls} placeholder="contacto@empresa.com"
                style={{ ...inputStyle, borderColor: bc('correoElectronico') }}
                onFocus={onFocus} onBlur={e => (e.currentTarget.style.borderColor = bc('correoElectronico'))}
                {...field('correoElectronico')} />
              <FieldError msg={errors.correoElectronico} />
            </div>

            <div>
              <label htmlFor="telefonoContacto" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                Teléfono
              </label>
              <input className={inputCls} placeholder="0991234567"
                style={inputStyle} onFocus={onFocus} onBlur={onBlur}
                {...field('telefonoContacto')} />
            </div>
          </div>

          {/* Bien asegurado */}
          <div className="space-y-5">
            <p className="text-[11px] font-semibold uppercase tracking-widest border-b pb-2"
              style={{ color: '#6B6B7A', borderColor: '#1E1E2A' }}>
              Bien asegurado
            </p>

            <div>
              <label htmlFor="tipoInmueble" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                Tipo de inmueble *
              </label>
              <select className={inputCls} style={inputStyle} onFocus={onFocus} onBlur={onBlur}
                {...field('tipoInmueble')}>
                {TIPOS_INMUEBLE.map(t => <option key={t} value={t}>{t.replaceAll('_', ' ')}</option>)}
              </select>
            </div>

            <div>
              <label htmlFor="usoPrincipal" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                Uso principal *
              </label>
              <select className={inputCls} style={inputStyle} onFocus={onFocus} onBlur={onBlur}
                {...field('usoPrincipal')}>
                {USOS.map(u => <option key={u} value={u}>{u}</option>)}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="anoConstruccion" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                  Año construcción
                </label>
                <input type="number" className={inputCls} placeholder="2010"
                  style={inputStyle} onFocus={onFocus} onBlur={onBlur}
                  {...field('anoConstruccion')} />
              </div>
              <div>
                <label htmlFor="numeroPisos" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                  N° pisos
                </label>
                <input type="number" className={inputCls} placeholder="3"
                  style={inputStyle} onFocus={onFocus} onBlur={onBlur}
                  {...field('numeroPisos')} />
              </div>
            </div>

            <div>
              <label htmlFor="descripcion" className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                Descripción
              </label>
              <textarea className={`${inputCls} resize-none h-20`} placeholder="Detalles del bien..."
                style={inputStyle} onFocus={onFocus} onBlur={onBlur}
                {...field('descripcion')} />
            </div>
          </div>
        </div>

        {apiError && (
          <div className="border-l-2 px-4 py-3 text-sm mb-6"
            style={{ borderColor: '#FF4D4D', backgroundColor: 'rgba(255,77,77,0.06)', color: '#FF4D4D' }}>
            {apiError}
          </div>
        )}

        <div className="flex gap-3">
          <button type="submit" disabled={saving}
            className="font-semibold px-8 py-3 text-sm transition-colors disabled:opacity-50 hover:opacity-90"
            style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}>
            {saving ? 'Guardando...' : 'Guardar y continuar →'}
          </button>
          <button type="button" onClick={() => router.push(`/cotizador/${folio}/estado`)}
            className="border px-6 py-3 text-sm transition-colors hover:opacity-80"
            style={{ borderColor: '#1E1E2A', color: '#6B6B7A' }}>
            Ver estado
          </button>
        </div>
      </form>
    </div>
  );
}
