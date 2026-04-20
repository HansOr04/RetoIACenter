'use client';
import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { foliosApi, quotesApi } from '@/lib/api';
import { Button } from '@/components/ui/Button';
import { Field, Input, Select } from '@/components/ui/Input';
import { SectionLabel } from '@/components/ui/SectionLabel';
import { Card } from '@/components/ui/Card';

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
  garantias: string;
}

const emptyUbicacion = (): UbicacionForm => ({
  nombreUbicacion: '',
  direccion: '',
  codigoPostal: '',
  ciudad: '',
  estado: '',
  municipio: '',
  tipoConstructivo: 'MAMPOSTERIA',
  valorEdificacion: '100000',
  valorContenidos: '50000',
  giro: { codigo: 'B1', descripcion: 'Oficinas', claveIncendio: 'B1' },
  garantias: 'INCENDIO, TERREMOTO',
});

const TIPOS_CONSTRUCTIVOS = ['MAMPOSTERIA', 'CONCRETO', 'ACERO', 'MADERA', 'MIXTO'];

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
          garantias: u.garantias ? u.garantias.split(',').map(g => g.trim()).filter(Boolean) : [],
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
      {/* ── Page header ── */}
      <div style={{ marginBottom: '32px' }}>
        <h1 className="page-title">Ubicaciones</h1>
        <p className="page-subtitle">Completa los datos de cada ubicación de la póliza.</p>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
        {ubicaciones.map((u, i) => {
          const p = `ub${i}`;
          return (
            <Card key={p} style={{ padding: '24px' }}>
              {/* UB header */}
              <div className="flex items-center" style={{ gap: '12px', marginBottom: '20px' }}>
                <span
                  className="mono-display badge badge-muted"
                  style={{ fontSize: '0.6875rem', letterSpacing: '0.08em' }}
                >
                  UB-{String(i + 1).padStart(2, '0')}
                </span>
                <span style={{ fontSize: '0.875rem', color: 'var(--muted)' }}>
                  Ubicación {i + 1}
                </span>
              </div>

              {/* Sección: Dirección */}
              <SectionLabel style={{ marginBottom: '16px' }}>Dirección</SectionLabel>
              <div className="grid grid-cols-1 md:grid-cols-2" style={{ gap: '16px', marginBottom: '24px' }}>
                <Field id={`${p}-nombre`} label="Nombre ubicación" required>
                  <Input
                    id={`${p}-nombre`}
                    required
                    placeholder="Bodega Central"
                    value={u.nombreUbicacion}
                    onChange={ev => updateField(i, 'nombreUbicacion', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-dir`} label="Dirección" required>
                  <Input
                    id={`${p}-dir`}
                    required
                    placeholder="Av. Principal 123"
                    value={u.direccion}
                    onChange={ev => updateField(i, 'direccion', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-cp`} label="Código postal" required>
                  <Input
                    id={`${p}-cp`}
                    required
                    placeholder="090001"
                    value={u.codigoPostal}
                    onChange={ev => updateField(i, 'codigoPostal', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-ciudad`} label="Ciudad">
                  <Input
                    id={`${p}-ciudad`}
                    placeholder="Guayaquil"
                    value={u.ciudad}
                    onChange={ev => updateField(i, 'ciudad', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-estado`} label="Estado / Provincia">
                  <Input
                    id={`${p}-estado`}
                    placeholder="Guayas"
                    value={u.estado}
                    onChange={ev => updateField(i, 'estado', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-mun`} label="Municipio">
                  <Input
                    id={`${p}-mun`}
                    placeholder="Guayaquil"
                    value={u.municipio}
                    onChange={ev => updateField(i, 'municipio', ev.target.value)}
                  />
                </Field>
              </div>

              {/* Sección: Datos técnicos */}
              <SectionLabel style={{ marginBottom: '16px' }}>Datos técnicos</SectionLabel>
              <div className="grid grid-cols-1 md:grid-cols-3" style={{ gap: '16px', marginBottom: '24px' }}>
                <Field id={`${p}-tc`} label="Tipo constructivo" required>
                  <Select
                    id={`${p}-tc`}
                    required
                    value={u.tipoConstructivo}
                    onChange={ev => updateField(i, 'tipoConstructivo', ev.target.value)}
                  >
                    {TIPOS_CONSTRUCTIVOS.map(t => (
                      <option key={t} value={t}>{t}</option>
                    ))}
                  </Select>
                </Field>
                <Field id={`${p}-vedif`} label="Valor edificación (USD)">
                  <Input
                    id={`${p}-vedif`}
                    type="number"
                    placeholder="150000"
                    value={u.valorEdificacion}
                    onChange={ev => updateField(i, 'valorEdificacion', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-vcont`} label="Valor contenidos (USD)">
                  <Input
                    id={`${p}-vcont`}
                    type="number"
                    placeholder="50000"
                    value={u.valorContenidos}
                    onChange={ev => updateField(i, 'valorContenidos', ev.target.value)}
                  />
                </Field>
              </div>

              {/* Sección: Giro comercial */}
              <SectionLabel style={{ marginBottom: '16px' }}>Giro comercial</SectionLabel>
              <div className="grid grid-cols-1 md:grid-cols-3" style={{ gap: '16px' }}>
                <Field id={`${p}-gcod`} label="Código" required>
                  <Input
                    id={`${p}-gcod`}
                    required
                    placeholder="G001"
                    value={u.giro.codigo}
                    onChange={ev => updateGiro(i, 'codigo', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-gdesc`} label="Descripción" required>
                  <Input
                    id={`${p}-gdesc`}
                    required
                    placeholder="Comercio General"
                    value={u.giro.descripcion}
                    onChange={ev => updateGiro(i, 'descripcion', ev.target.value)}
                  />
                </Field>
                <Field id={`${p}-ginc`} label="Clave incendio" required>
                  <Input
                    id={`${p}-ginc`}
                    required
                    placeholder="INC-01"
                    value={u.giro.claveIncendio}
                    onChange={ev => updateGiro(i, 'claveIncendio', ev.target.value)}
                  />
                </Field>
              </div>
              {/* Sección: Otras garantías */}
              <SectionLabel style={{ marginBottom: '16px', marginTop: '24px' }}>Otras Garantías</SectionLabel>
              <div className="grid grid-cols-1" style={{ gap: '16px' }}>
                <Field id={`${p}-garantias`} label="Garantías Tarifables (separadas por coma)" required>
                  <Input
                    id={`${p}-garantias`}
                    required
                    placeholder="INCENDIO, TERREMOTO"
                    value={u.garantias}
                    onChange={ev => updateField(i, 'garantias', ev.target.value)}
                  />
                  <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginTop: '8px' }}>Ingresa al menos una garantía para que la ubicación sea tarifable.</p>
                </Field>
              </div>
            </Card>
          );
        })}

        {error && (
          <div className="error-banner">{error}</div>
        )}

        <div className="flex gap-4">
          <Button
            type="submit"
            loading={saving}
            id="btn-guardar-ubicaciones"
          >
            {saving ? 'Guardando…' : 'Continuar a coberturas →'}
          </Button>
          <Button
            type="button"
            variant="ghost"
            onClick={() => router.push(`/cotizador/${folio}/calculo`)}
            id="btn-ir-calculo-desde-ub"
          >
            Ir al cálculo
          </Button>
        </div>
      </form>
    </div>
  );
}
