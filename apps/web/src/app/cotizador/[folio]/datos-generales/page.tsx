'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { StepIndicator } from '@/components/StepIndicator';
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

const inputBase =
  'w-full border text-sm px-4 py-3 focus:outline-none transition-colors';

export default function DatosGeneralesPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState<boolean>(false);
  const [apiError, setApiError] = useState<string>('');
  const [form, setForm] = useState<FormState>({
    nombreTomador: '', rucCedula: '', correoElectronico: '',
    telefonoContacto: '', tipoInmueble: 'LOCAL_COMERCIAL',
    usoPrincipal: 'COMERCIAL', anoConstruccion: '', numeroPisos: '',
    descripcion: '',
  });
  const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});

  function validate(): Partial<Record<keyof FormState, string>> {
    const e: Partial<Record<keyof FormState, string>> = {};
    if (!form.nombreTomador.trim()) e.nombreTomador = 'Campo requerido';
    if (!form.rucCedula.trim()) e.rucCedula = 'Campo requerido';
    if (!form.correoElectronico.trim()) e.correoElectronico = 'Campo requerido';
    if (form.correoElectronico && !/\S+@\S+\.\S+/.test(form.correoElectronico))
      e.correoElectronico = 'Correo inválido';
    return e;
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }

    setSaving(true);
    setApiError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      await foliosApi.putDatosGenerales(folio, estado.version, {
        ...form,
        anoConstruccion: form.anoConstruccion ? parseInt(form.anoConstruccion) : null,
        numeroPisos: form.numeroPisos ? parseInt(form.numeroPisos) : null,
      });
      router.push(`/cotizador/${folio}/layout`);
    } catch (err: unknown) {
      const e = err as { detail?: string };
      setApiError(e.detail || 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  function field(key: keyof FormState) {
    return {
      value: form[key],
      onChange: (
        ev: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>,
      ) => setForm(f => ({ ...f, [key]: ev.target.value })),
    };
  }

  const borderColor = (k: keyof FormState) =>
    errors[k] ? '#FF4D4D' : '#1E1E2A';

  return (
    <div>
      <StepIndicator current={2} />

      <div className="mt-10">
        <h1 className="font-serif text-3xl mb-1" style={{ color: '#F5F5F0' }}>Datos generales</h1>
        <p className="text-sm mb-8" style={{ color: '#6B6B7A' }}>
          Folio <span className="font-mono" style={{ color: '#00D9A3' }}>{folio}</span>
        </p>

        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="space-y-5">
              <div
                className="text-xs uppercase tracking-widest border-b pb-2 mb-4"
                style={{ color: '#6B6B7A', borderColor: '#1E1E2A' }}
              >
                Tomador del seguro
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
                  Nombre / Razón social *
                </label>
                <input
                  className={inputBase}
                  placeholder="Empresa Ejemplo S.A."
                  style={{ backgroundColor: '#111118', borderColor: borderColor('nombreTomador'), color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = borderColor('nombreTomador'))}
                  {...field('nombreTomador')}
                />
                <FieldError msg={errors.nombreTomador} />
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>RUC / Cédula *</label>
                <input
                  className={inputBase}
                  placeholder="1792345678001"
                  style={{ backgroundColor: '#111118', borderColor: borderColor('rucCedula'), color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = borderColor('rucCedula'))}
                  {...field('rucCedula')}
                />
                <FieldError msg={errors.rucCedula} />
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Correo electrónico *</label>
                <input
                  type="email"
                  className={inputBase}
                  placeholder="contacto@empresa.com"
                  style={{ backgroundColor: '#111118', borderColor: borderColor('correoElectronico'), color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = borderColor('correoElectronico'))}
                  {...field('correoElectronico')}
                />
                <FieldError msg={errors.correoElectronico} />
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Teléfono</label>
                <input
                  className={inputBase}
                  placeholder="0991234567"
                  style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  {...field('telefonoContacto')}
                />
              </div>
            </div>

            <div className="space-y-5">
              <div
                className="text-xs uppercase tracking-widest border-b pb-2 mb-4"
                style={{ color: '#6B6B7A', borderColor: '#1E1E2A' }}
              >
                Bien asegurado
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Tipo de inmueble *</label>
                <select
                  className={inputBase}
                  style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  {...field('tipoInmueble')}
                >
                  {TIPOS_INMUEBLE.map(t => (
                    <option key={t} value={t}>{t.replace('_', ' ')}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Uso principal *</label>
                <select
                  className={inputBase}
                  style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  {...field('usoPrincipal')}
                >
                  {USOS.map(u => (
                    <option key={u} value={u}>{u}</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Año construcción</label>
                  <input
                    type="number"
                    className={inputBase}
                    placeholder="2010"
                    style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                    {...field('anoConstruccion')}
                  />
                </div>
                <div>
                  <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>N° pisos</label>
                  <input
                    type="number"
                    className={inputBase}
                    placeholder="3"
                    style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                    onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                    onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                    {...field('numeroPisos')}
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>Descripción</label>
                <textarea
                  className={`${inputBase} resize-none h-20`}
                  placeholder="Detalles del bien..."
                  style={{ backgroundColor: '#111118', borderColor: '#1E1E2A', color: '#F5F5F0' }}
                  onFocus={e => (e.currentTarget.style.borderColor = '#00D9A3')}
                  onBlur={e => (e.currentTarget.style.borderColor = '#1E1E2A')}
                  {...field('descripcion')}
                />
              </div>
            </div>
          </div>

          {apiError && (
            <div
              className="mt-6 border text-sm px-4 py-3"
              style={{
                borderColor: 'rgba(255,77,77,0.3)',
                backgroundColor: 'rgba(255,77,77,0.05)',
                color: '#FF4D4D',
              }}
            >
              {apiError}
            </div>
          )}

          <div className="mt-10 flex gap-4">
            <button
              type="submit"
              disabled={saving}
              className="font-medium px-8 py-3 text-sm transition-colors disabled:opacity-50"
              style={{ backgroundColor: '#00D9A3', color: '#0A0A0F' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#00A87E')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = '#00D9A3')}
            >
              {saving ? 'Guardando...' : 'Continuar →'}
            </button>
            <button
              type="button"
              onClick={() => router.push(`/cotizador/${folio}/estado`)}
              className="border px-6 py-3 text-sm transition-colors"
              style={{ borderColor: '#1E1E2A', color: '#6B6B7A' }}
              onMouseEnter={e => {
                e.currentTarget.style.borderColor = '#F5F5F0';
                e.currentTarget.style.color = '#F5F5F0';
              }}
              onMouseLeave={e => {
                e.currentTarget.style.borderColor = '#1E1E2A';
                e.currentTarget.style.color = '#6B6B7A';
              }}
            >
              Ver estado
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
