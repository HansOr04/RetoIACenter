'use client';
import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';

interface GiroForm {
  codigo: string;
  descripcion: string;
  claveIncendio: string;
}

interface UbicacionForm {
  nombreUbicacion: string;
  direccion: string;
  codigoPostal: string;
  ciudad: string;
  estado: string;
  municipio: string;
  tipoConstructivo: string;
  valorEdificacion: string;
  valorContenidos: string;
  giro: GiroForm;
}

const emptyUbicacion = (): UbicacionForm => ({
  nombreUbicacion: '',
  direccion: '',
  codigoPostal: '',
  ciudad: '',
  estado: '',
  municipio: '',
  tipoConstructivo: 'MAMPOSTERIA',
  valorEdificacion: '',
  valorContenidos: '',
  giro: { codigo: '', descripcion: '', claveIncendio: '' },
});

const TIPOS_CONSTRUCTIVOS = ['MAMPOSTERIA', 'CONCRETO', 'ACERO', 'MADERA', 'MIXTO'];

const inputCls = 'w-full border text-sm px-3 py-2.5 focus:outline-none transition-colors';
const inputStyle = { backgroundColor: '#0A0A0F', borderColor: '#1E1E2A', color: '#F5F5F0' };
const focusHandler = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) =>
  (e.currentTarget.style.borderColor = '#00D9A3');
const blurHandler = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) =>
  (e.currentTarget.style.borderColor = '#1E1E2A');

function Field({
  id, label, required, children,
}: Readonly<{
  id?: string; label: string; required?: boolean; children: React.ReactNode;
}>) {
  return (
    <div>
      <label htmlFor={id} className="block text-xs mb-1.5" style={{ color: '#6B6B7A' }}>
        {label}{required && ' *'}
      </label>
      {children}
    </div>
  );
}

export default function UbicacionesPage() {
  const { folio } = useParams<{ folio: string }>();
  const router = useRouter();
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
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

  function updateField(i: number, field: keyof UbicacionForm, value: string) {
    setUbicaciones(prev =>
      prev.map((u, idx) => (idx === i ? { ...u, [field]: value } : u)),
    );
  }

  function updateGiro(i: number, field: keyof GiroForm, value: string) {
    setUbicaciones(prev =>
      prev.map((u, idx) =>
        idx === i ? { ...u, giro: { ...u.giro, [field]: value } } : u,
      ),
    );
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const estado = await foliosApi.getEstado(folio) as { version: number };
      let version = estado.version;

      for (const u of ubicaciones) {
        const payload: Record<string, unknown> = {
          nombreUbicacion: u.nombreUbicacion,
          direccion: u.direccion,
          codigoPostal: u.codigoPostal,
          ciudad: u.ciudad,
          estado: u.estado,
          municipio: u.municipio,
          tipoConstructivo: u.tipoConstructivo,
          giro: u.giro,
        };
        if (u.valorEdificacion || u.valorContenidos) {
          payload.datosTecnicos = {
            valorEdificacion: u.valorEdificacion ? Number.parseFloat(u.valorEdificacion) : null,
            valorContenidos: u.valorContenidos ? Number.parseFloat(u.valorContenidos) : null,
          };
        }
        const res = await quotesApi.putUbicacion(folio, version, payload) as { version?: number };
        version = res.version ?? version + 1;
      }

      router.push(`/cotizador/${folio}/coberturas`);
    } catch (err: unknown) {
      const e = err as { detail?: string; title?: string };
      setError(e.detail ?? e.title ?? 'Error al guardar ubicaciones');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-semibold mb-1" style={{ color: '#F5F5F0' }}>Ubicaciones</h1>
        <p className="text-sm" style={{ color: '#6B6B7A' }}>
          Completa los datos de cada ubicación de la póliza.
        </p>
      </div>

        <form onSubmit={handleSubmit} className="space-y-8">
          {ubicaciones.map((u, i) => {
            const p = `ub${i}`;
            return (
              <div key={p} className="border p-6 space-y-5" style={{ borderColor: '#1E1E2A', backgroundColor: '#111118' }}>
                <div className="flex items-center gap-3">
                  <span className="font-mono text-xs border px-2 py-1" style={{ borderColor: '#1E1E2A', color: '#6B6B7A' }}>
                    UB-{String(i + 1).padStart(2, '0')}
                  </span>
                  <span className="text-sm" style={{ color: '#6B6B7A' }}>Ubicación {i + 1}</span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Field id={`${p}-nombre`} label="Nombre ubicación" required>
                    <input
                      id={`${p}-nombre`}
                      required
                      className={inputCls}
                      placeholder="Bodega Central"
                      value={u.nombreUbicacion}
                      onChange={ev => updateField(i, 'nombreUbicacion', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                  <Field id={`${p}-dir`} label="Dirección" required>
                    <input
                      id={`${p}-dir`}
                      required
                      className={inputCls}
                      placeholder="Av. Principal 123"
                      value={u.direccion}
                      onChange={ev => updateField(i, 'direccion', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                  <Field id={`${p}-cp`} label="Código postal" required>
                    <input
                      id={`${p}-cp`}
                      required
                      className={inputCls}
                      placeholder="090001"
                      value={u.codigoPostal}
                      onChange={ev => updateField(i, 'codigoPostal', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                  <Field id={`${p}-ciudad`} label="Ciudad">
                    <input
                      id={`${p}-ciudad`}
                      className={inputCls}
                      placeholder="Guayaquil"
                      value={u.ciudad}
                      onChange={ev => updateField(i, 'ciudad', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                  <Field id={`${p}-estado`} label="Estado / Provincia">
                    <input
                      id={`${p}-estado`}
                      className={inputCls}
                      placeholder="Guayas"
                      value={u.estado}
                      onChange={ev => updateField(i, 'estado', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                  <Field id={`${p}-mun`} label="Municipio">
                    <input
                      id={`${p}-mun`}
                      className={inputCls}
                      placeholder="Guayaquil"
                      value={u.municipio}
                      onChange={ev => updateField(i, 'municipio', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <Field id={`${p}-tc`} label="Tipo constructivo" required>
                    <select
                      id={`${p}-tc`}
                      required
                      className={inputCls}
                      value={u.tipoConstructivo}
                      onChange={ev => updateField(i, 'tipoConstructivo', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    >
                      {TIPOS_CONSTRUCTIVOS.map(t => (
                        <option key={t} value={t}>{t}</option>
                      ))}
                    </select>
                  </Field>
                  <Field id={`${p}-vedif`} label="Valor edificación (USD)">
                    <input
                      id={`${p}-vedif`}
                      type="number"
                      className={inputCls}
                      placeholder="150000"
                      value={u.valorEdificacion}
                      onChange={ev => updateField(i, 'valorEdificacion', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                  <Field id={`${p}-vcont`} label="Valor contenidos (USD)">
                    <input
                      id={`${p}-vcont`}
                      type="number"
                      className={inputCls}
                      placeholder="50000"
                      value={u.valorContenidos}
                      onChange={ev => updateField(i, 'valorContenidos', ev.target.value)}
                      style={inputStyle}
                      onFocus={focusHandler}
                      onBlur={blurHandler}
                    />
                  </Field>
                </div>

                <div>
                  <p className="text-xs mb-3 uppercase tracking-wider" style={{ color: '#6B6B7A' }}>Giro comercial</p>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <Field id={`${p}-gcod`} label="Código" required>
                      <input
                        id={`${p}-gcod`}
                        required
                        className={inputCls}
                        placeholder="G001"
                        value={u.giro.codigo}
                        onChange={ev => updateGiro(i, 'codigo', ev.target.value)}
                        style={inputStyle}
                        onFocus={focusHandler}
                        onBlur={blurHandler}
                      />
                    </Field>
                    <Field id={`${p}-gdesc`} label="Descripción" required>
                      <input
                        id={`${p}-gdesc`}
                        required
                        className={inputCls}
                        placeholder="Comercio General"
                        value={u.giro.descripcion}
                        onChange={ev => updateGiro(i, 'descripcion', ev.target.value)}
                        style={inputStyle}
                        onFocus={focusHandler}
                        onBlur={blurHandler}
                      />
                    </Field>
                    <Field id={`${p}-ginc`} label="Clave incendio" required>
                      <input
                        id={`${p}-ginc`}
                        required
                        className={inputCls}
                        placeholder="INC-01"
                        value={u.giro.claveIncendio}
                        onChange={ev => updateGiro(i, 'claveIncendio', ev.target.value)}
                        style={inputStyle}
                        onFocus={focusHandler}
                        onBlur={blurHandler}
                      />
                    </Field>
                  </div>
                </div>
              </div>
            );
          })}

          {error && (
            <div
              className="border text-sm px-4 py-3"
              style={{ borderColor: 'rgba(255,77,77,0.3)', backgroundColor: 'rgba(255,77,77,0.05)', color: '#FF4D4D' }}
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
              onMouseEnter={e => { e.currentTarget.style.borderColor = '#00D9A3'; e.currentTarget.style.color = '#00D9A3'; }}
              onMouseLeave={e => { e.currentTarget.style.borderColor = '#1E1E2A'; e.currentTarget.style.color = '#6B6B7A'; }}
            >
              Ir al cálculo
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
