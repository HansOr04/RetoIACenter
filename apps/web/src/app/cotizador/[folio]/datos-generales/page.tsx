'use client';
import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi } from '@/lib/api';
import { FieldError } from '@/components/FieldError';
import { Button } from '@/components/ui/Button';
import { Field, Input, Select, Textarea } from '@/components/ui/Input';
import { SectionLabel } from '@/components/ui/SectionLabel';

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
        <h1 className="page-title">Datos generales</h1>
        <p className="page-subtitle">Información del tomador y el bien asegurado.</p>
      </div>

      <form onSubmit={handleSubmit}>
        <div
          className="grid grid-cols-1 md:grid-cols-2"
          style={{ gap: '32px', marginBottom: '32px' }}
        >
          {/* ── Tomador del seguro ── */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <SectionLabel>Tomador del seguro</SectionLabel>

            <Field id="nombreTomador" label="Nombre / Razón social" required error={errors.nombreTomador}>
              <Input
                placeholder="Empresa Ejemplo S.A."
                error={!!errors.nombreTomador}
                {...fieldProps('nombreTomador')}
              />
              <FieldError msg={errors.nombreTomador} />
            </Field>

            <Field id="rucCedula" label="RUC / Cédula" required error={errors.rucCedula}>
              <Input
                placeholder="1792345678001"
                error={!!errors.rucCedula}
                {...fieldProps('rucCedula')}
              />
              <FieldError msg={errors.rucCedula} />
            </Field>

            <Field id="correoElectronico" label="Correo electrónico" required error={errors.correoElectronico}>
              <Input
                type="email"
                placeholder="contacto@empresa.com"
                error={!!errors.correoElectronico}
                {...fieldProps('correoElectronico')}
              />
              <FieldError msg={errors.correoElectronico} />
            </Field>

            <Field id="telefonoContacto" label="Teléfono">
              <Input
                placeholder="0991234567"
                {...fieldProps('telefonoContacto')}
              />
            </Field>
          </div>

          {/* ── Bien asegurado ── */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <SectionLabel>Bien asegurado</SectionLabel>

            <Field id="tipoInmueble" label="Tipo de inmueble" required>
              <Select {...fieldProps('tipoInmueble')}>
                {TIPOS_INMUEBLE.map(t => (
                  <option key={t} value={t}>{t.replaceAll('_', ' ')}</option>
                ))}
              </Select>
            </Field>

            <Field id="usoPrincipal" label="Uso principal" required>
              <Select {...fieldProps('usoPrincipal')}>
                {USOS.map(u => <option key={u} value={u}>{u}</option>)}
              </Select>
            </Field>

            <div className="grid grid-cols-2" style={{ gap: '16px' }}>
              <Field id="anoConstruccion" label="Año construcción">
                <Input
                  type="number"
                  placeholder="2010"
                  {...fieldProps('anoConstruccion')}
                />
              </Field>
              <Field id="numeroPisos" label="N° pisos">
                <Input
                  type="number"
                  placeholder="3"
                  {...fieldProps('numeroPisos')}
                />
              </Field>
            </div>

            <Field id="descripcion" label="Descripción">
              <Textarea
                placeholder="Detalles del bien…"
                style={{ resize: 'none', height: '80px' }}
                {...fieldProps('descripcion')}
              />
            </Field>
          </div>
        </div>

        {apiError && (
          <div className="error-banner" style={{ marginBottom: '24px' }}>
            {apiError}
          </div>
        )}

        <div className="flex gap-3">
          <Button type="submit" loading={saving} id="btn-guardar-datos-generales">
            {saving ? 'Guardando…' : 'Guardar y continuar →'}
          </Button>
          <Button
            type="button"
            variant="ghost"
            onClick={() => router.push(`/cotizador/${folio}/estado`)}
            id="btn-ver-estado"
          >
            Ver estado
          </Button>
        </div>
      </form>
    </div>
  );
}
