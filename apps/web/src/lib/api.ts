const BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ title: 'Error desconocido' }));
    throw { status: res.status, ...err };
  }
  return res.json();
}

export const foliosApi = {
  crear: (idempotencyKey: string, body?: { tipoNegocio?: string; codigoAgente?: string }) =>
    request('/api/v1/folios', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Idempotency-Key': idempotencyKey,
      },
      body: JSON.stringify(body || {}),
    }),

  getEstado: (folio: string) =>
    request(`/api/v1/folios/${folio}/estado`),

  putDatosGenerales: (folio: string, version: number, data: object) =>
    request(`/api/v1/folios/${folio}/datos-generales`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'If-Match': String(version) },
      body: JSON.stringify(data),
    }),

  putLayout: (folio: string, version: number, data: object) =>
    request(`/api/v1/folios/${folio}/ubicaciones/layout`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'If-Match': String(version) },
      body: JSON.stringify(data),
    }),
};

export const quotesApi = {
  putUbicacion: (folio: string, version: number, data: object) =>
    request(`/api/v1/quotes/${folio}/locations`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'If-Match': String(version) },
      body: JSON.stringify(data),
    }),

  getUbicaciones: (folio: string) =>
    request(`/api/v1/quotes/${folio}/locations`),

  getSummary: (folio: string) =>
    request(`/api/v1/quotes/${folio}/locations/summary`),

  patchUbicacion: (folio: string, indice: number, version: number, data: object) =>
    request(`/api/v1/quotes/${folio}/locations/${indice}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json', 'If-Match': String(version) },
      body: JSON.stringify(data),
    }),

  putCoberturas: (folio: string, version: number, data: object) =>
    request(`/api/v1/quotes/${folio}/coverage-options`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'If-Match': String(version) },
      body: JSON.stringify(data),
    }),

  calcular: (folio: string, version: number) =>
    request(`/api/v1/quotes/${folio}/calculate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'If-Match': String(version) },
      body: JSON.stringify({}),
    }),
};
