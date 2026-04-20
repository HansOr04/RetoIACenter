import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

// ── Métricas personalizadas ───────────────────────────────────────────────────
const primaResponseTime = new Trend('prima_response_time');
const calculosExitosos  = new Counter('calculos_exitosos');
const calculosFallidos  = new Counter('calculos_fallidos');

// ── Opciones del test ─────────────────────────────────────────────────────────
export const options = {
  stages: [
    { duration: '10s', target: 10 },   // Warm-up
    { duration: '30s', target: 50 },   // Ramp up
    { duration: '60s', target: 50 },   // Carga sostenida
    { duration: '10s', target: 0  },   // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],   // p95 debe estar bajo 500ms
    http_req_failed:   ['rate<0.01'],   // Menos de 1% de errores
  },
};

const API = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

// ── Flujo completo por VU ─────────────────────────────────────────────────────
function crearFolioConUbicacion() {
  const key = `perf-${__VU}-${__ITER}-${Date.now()}`;

  // 1. Crear folio
  let r = http.post(
    `${API}/folios`,
    JSON.stringify({ tipoNegocio: 'COMERCIAL' }),
    { headers: { 'Content-Type': 'application/json', 'X-Idempotency-Key': key } }
  );
  if (r.status !== 201 && r.status !== 200) return null;
  const folio = r.json('numeroFolio');

  // 2. Datos generales
  http.put(
    `${API}/quotes/${folio}/general-info`,
    JSON.stringify({
      nombreTomador: 'Perf Test S.A.',
      rucCedula: '1791876029001',
      correoElectronico: 'perf@test.com',
      telefonoContacto: '0991234567',
      tipoInmueble: 'EDIFICIO',
      usoPrincipal: 'COMERCIO',
      anoConstruccion: 2010,
      numeroPisos: 3,
      descripcion: 'Prueba de carga'
    }),
    { headers: { 'Content-Type': 'application/json', 'If-Match': '1' } }
  );

  // 3. Layout
  http.put(
    `${API}/quotes/${folio}/locations/layout`,
    JSON.stringify({
      numeroUbicaciones: 1,
      seccionesAplican: { direccion: true, datosTecnicos: true, giroComercial: true, garantias: true }
    }),
    { headers: { 'Content-Type': 'application/json', 'If-Match': '2' } }
  );

  // 4. Ubicación
  http.put(
    `${API}/quotes/${folio}/locations`,
    JSON.stringify({
      nombreUbicacion: 'Oficina Central Perf',
      direccion: 'Av. Amazonas 123',
      codigoPostal: '170110',
      estado: 'Pichincha',
      municipio: 'Quito',
      colonia: 'Centro',
      ciudad: 'Quito',
      tipoConstructivo: 'CONCRETO_ARMADO',
      nivel: 3,
      anioConstruccion: 2010,
      giro: { codigo: 'G-4521', descripcion: 'Oficinas', claveIncendio: 'B1' },
      garantias: ['EXTINTORES', 'DETECTORES_HUMO']
    }),
    { headers: { 'Content-Type': 'application/json', 'If-Match': '3' } }
  );

  // 5. Cobertura
  http.put(
    `${API}/quotes/${folio}/coverage-options`,
    JSON.stringify({ incendioEdificios: true, incendioContenidos: true, catTev: true }),
    { headers: { 'Content-Type': 'application/json', 'If-Match': '4' } }
  );

  return folio;
}

// ── Escenario principal ───────────────────────────────────────────────────────
export default function () {
  const folio = crearFolioConUbicacion();
  if (!folio) {
    calculosFallidos.add(1);
    return;
  }

  // Endpoint objetivo bajo carga
  const start = Date.now();
  const r = http.post(
    `${API}/quotes/${folio}/calculate`,
    '',
    { headers: { 'Content-Type': 'application/json', 'If-Match': '5' } }
  );
  primaResponseTime.add(Date.now() - start);

  const ok = check(r, {
    'status es 200': (res) => res.status === 200,
    'primaNeta > 0':  (res) => {
      try { return res.json('primaNeta') > 0; } catch { return false; }
    },
    'response time < 500ms': (res) => res.timings.duration < 500,
  });

  if (ok) calculosExitosos.add(1);
  else     calculosFallidos.add(1);

  sleep(1);
}

// ── Reporte HTML ──────────────────────────────────────────────────────────────
import { htmlReport }   from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary }  from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export function handleSummary(data) {
  return {
    'tests/reports/k6-summary.html': htmlReport(data),
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
  };
}
