import { test, expect, request } from '@playwright/test';

/**
 * FLUJO 3 · VERSIONADO OPTIMISTA EN EDICIÓN
 *
 * JUSTIFICACIÓN (criterio 7):
 * El reto exige explícitamente "manejo versionado optimista en
 * operaciones de edición" como regla de negocio obligatoria.
 *
 * En un dominio de cotización de seguros, dos operadores pueden
 * editar simultáneamente la misma cotización. Sin detección de
 * conflictos de versión se pierden actualizaciones silenciosamente,
 * con impacto financiero real.
 *
 * Este flujo prueba directamente la API porque es donde vive el
 * contrato del versionado optimista (header If-Match + ETag).
 * Simula dos clientes que editan con versiones desfasadas.
 */

const API_BASE = 'http://localhost:8080/api/v1';

test.describe('Flujo 3 · Versionado optimista en edición', () => {
  test('segundo cliente con version obsoleta recibe 409 Conflict sin modificar datos', async ({ request }) => {
    // Crear folio
    const crear = await request.post(`${API_BASE}/folios`, {
      headers: {
        'X-Idempotency-Key': `test-versionado-${Date.now()}`,
        'Content-Type': 'application/json',
      },
      data: { tipoNegocio: 'COMERCIAL', codigoAgente: 'AG-001' },
    });
    expect(crear.ok()).toBeTruthy();
    const folio = await crear.json();
    const numeroFolio = folio.numeroFolio;
    expect(folio.version).toBe(1);

    // Cliente A actualiza datos generales con version 1 → version queda en 2
    const actA = await request.put(`${API_BASE}/folios/${numeroFolio}/datos-generales`, {
      headers: {
        'If-Match': '1',
        'Content-Type': 'application/json',
      },
      data: {
        nombreTomador: 'Cliente A',
        rucCedula: '1792146739001',
        correoElectronico: 'clienteA@test.com',
        tipoInmueble: 'EDIFICIO',
        usoPrincipal: 'COMERCIAL',
      },
    });
    expect(actA.status()).toBe(200);
    const bodyA = await actA.json();
    expect(bodyA.version).toBe(2);

    // Cliente B intenta actualizar con version 1 (desactualizada) → debe recibir 409
    const actB = await request.put(`${API_BASE}/folios/${numeroFolio}/datos-generales`, {
      headers: {
        'If-Match': '1', // version obsoleta
        'Content-Type': 'application/json',
      },
      data: {
        nombreTomador: 'Cliente B (debería fallar)',
        rucCedula: '9999999999999',
        correoElectronico: 'clienteB@test.com',
        tipoInmueble: 'EDIFICIO',
        usoPrincipal: 'COMERCIAL',
      },
    });
    expect(actB.status()).toBe(409);

    const errorBody = await actB.json();
    expect(errorBody.type).toContain('version-conflict');
    expect(errorBody.status).toBe(409);
    expect(errorBody.currentVersion).toBe(2);
    expect(errorBody.receivedVersion).toBe(1);

    // Verificar que los datos del Cliente A siguen intactos
    const leer = await request.get(`${API_BASE}/folios/${numeroFolio}/datos-generales`);
    const datos = await leer.json();
    expect(datos.nombreTomador).toBe('Cliente A');
    expect(datos.rucCedula).toBe('1792146739001');
  });

  test('cliente sin header If-Match en PUT recibe 428 Precondition Required', async ({ request }) => {
    const crear = await request.post(`${API_BASE}/folios`, {
      headers: {
        'X-Idempotency-Key': `test-ifmatch-${Date.now()}`,
        'Content-Type': 'application/json',
      },
      data: { tipoNegocio: 'COMERCIAL' },
    });
    const folio = await crear.json();

    // Sin If-Match
    const sinIfMatch = await request.put(`${API_BASE}/folios/${folio.numeroFolio}/datos-generales`, {
      headers: { 'Content-Type': 'application/json' },
      data: { nombreTomador: 'Test' },
    });
    expect(sinIfMatch.status()).toBe(428);
  });
});
