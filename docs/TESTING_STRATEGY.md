# Estrategia de pruebas

> Cómo se asegura la calidad del cotizador. Qué se prueba, a qué nivel, con qué herramientas, y por qué.

## 1. Pirámide de testing

Seguimos una pirámide clásica adaptada al contexto del reto:

```
          ╱╲
         ╱E2E╲          3 flujos Playwright       (alta confianza,
        ╱─────╲                                     baja velocidad)
       ╱ Integ. ╲       5 features Karate          (media)
      ╱─────────╲
     ╱  Unit     ╲      18+ tests JUnit + Jest      (alta velocidad,
    ╱─────────────╲                                  baja confianza)
```

**Regla general:** si una regla de negocio se puede probar con un unit test, no subir a integration. Si se puede probar con integration, no subir a E2E.

## 2. Tests unitarios

### Backend · JUnit 5 + Mockito

**Alcance:** use cases, servicios de dominio, validadores, mappers.

**Herramientas:** JUnit 5, Mockito, AssertJ, Testcontainers (para tests que necesitan BD real).

**Cobertura mínima:** 80% de líneas (enforced por JaCoCo plugin).

**Ubicación:** `apps/api/src/test/java/com/sofka/cotizador/`

**Ejecutar:** `cd apps/api && ./mvnw test`

**Reporte:** `apps/api/target/site/jacoco/index.html`

### Frontend · Jest + React Testing Library

**Alcance:** componentes UI reutilizables, hooks con lógica, utilidades puras.

**Herramientas:** Jest 29, Testing Library, user-event, SWC transform.

**Cobertura:** al menos 50% global, 70% en componentes testeados.

**Ubicación:** `apps/web/src/**/__tests__/`

**Ejecutar:** `cd apps/web && pnpm test:coverage`

## 3. Tests de integración · Karate DSL

**Alcance:** contratos HTTP del backend. Validan que los endpoints responden correctamente a requests reales sin UI.

**Por qué Karate:** sintaxis Gherkin legible, no requiere Java boilerplate, maneja JSON naturalmente.

**Features implementados:**
- `crear-folio.feature` — HU-001 (3 scenarios)
- `datos-generales.feature` — HU-002 (5 scenarios)
- `ubicaciones.feature` — HU-004 (4 scenarios)
- `cobertura.feature` — HU-006 (3 scenarios)
- `calculo.feature` — HU-007 (3 scenarios)

**Ubicación:** `tests/integration/src/test/java/features/`

**Ejecutar:** `cd tests/integration && mvn test` (requiere el stack docker arriba)

**Reporte:** `tests/integration/target/karate-reports/karate-summary.html`

## 4. Tests E2E · Playwright

**Alcance:** flujos completos de usuario a través de la interfaz web. Validan integración real de las 3 aplicaciones.

### Los 3 flujos obligatorios con justificación

**Flujo 1 · Happy path completo del cotizador**

> Cubre el escenario de aceptación oficial del reto (página 14 del documento funcional). Sin este flujo no se puede afirmar que el sistema cumpla su propósito. Es el único que valida integración REAL entre las 3 aplicaciones y la persistencia.

**Flujo 2 · Ubicación incompleta no bloquea cálculo**

> Valida la regla de negocio más delicada: "si una ubicación está incompleta, esta ubicación genera alerta, pero no debe impedir calcular las demás". Sin este test un refactor del cálculo puede romper silenciosamente el requisito, con impacto de negocio.

**Flujo 3 · Versionado optimista en edición**

> El reto exige "manejo versionado optimista en operaciones de edición". En producción, dos operadores pueden editar simultáneamente con impacto financiero real. Este test valida que el backend detecta y rechaza el conflicto con 409.

**Ubicación:** `tests/e2e/tests/`

**Ejecutar:**
```bash
docker compose up -d
cd tests/e2e
pnpm exec playwright test
```

**Reporte:** `tests/e2e/playwright-report/index.html`

## 5. Tests de performance (opcional) · k6

Prueba de carga completa contra `POST /quotes/{folio}/calculate` con un flujo real por VU (crear folio → datos → layout → ubicación → cobertura → calcular). Demuestra que el endpoint de cálculo aguanta carga moderada dentro del SLA definido.

**Configuración:**
- Warm-up: 10 VUs × 10s
- Ramp-up: 50 VUs × 30s
- Carga sostenida: **50 VUs × 60s**
- Cool-down: 0 VUs × 10s
- Threshold: **p(95) < 500ms**, error rate < 1%

**Resultado ejecutado:** p95 = XXXms, 0% errores. Ver `tests/reports/k6-summary.html`.

**Ubicación:** `tests/performance/calculate_load.js`

**Ejecutar:**
```bash
docker compose up -d
k6 run tests/performance/calculate_load.js
```

**Reporte HTML:** `tests/reports/k6-summary.html` (generado automáticamente al finalizar).

**No corre en CI** — es una prueba manual que se adjunta como evidencia.

## 6. Matriz de cobertura HU → tipo de test

| HU | Unit | Integration | E2E |
|---|---|---|---|
| HU-001 Crear folio | ✅ | ✅ | ✅ |
| HU-002 Datos generales | ✅ | ✅ | (indirecto en flujo 1) |
| HU-003 Layout | ✅ | (via helper Karate) | (indirecto) |
| HU-004 CRUD ubicaciones | ✅ | ✅ | ✅ |
| HU-005 Edición parcial | ✅ | (cubrir si queda tiempo) | - |
| HU-006 Cobertura | ✅ | ✅ | (indirecto) |
| HU-007 Cálculo | ✅ (crítico) | ✅ | ✅ |
| HU-008 Estado | ✅ | - | (indirecto) |
| HU-009 Versionado | ✅ | ✅ | ✅ |
| HU-010 Incompletas | ✅ | ✅ | ✅ (flujo 2 dedicado) |

## 7. Continuous integration

`.gitlab-ci.yml` ejecuta en cada push:
- Stage `build` — compilación del backend y frontend
- Stage `test` — tests unitarios de ambos
- Stage `coverage` — publicación de reporte JaCoCo

Los tests de integración Karate y E2E Playwright **no corren en CI** por simplicidad; se ejecutan localmente antes de push significativos y se adjuntan screenshots/reportes al PR cuando aplique.

## 8. Datos de prueba

### Backend
- Testcontainers levanta Postgres efímero por clase de test que requiere BD
- Los catálogos se pueblan con los seeders de Flyway

### Frontend
- Mocks de fetch con Jest para hooks
- Fixtures en `tests/e2e/fixtures/test-data.ts` para Playwright

### Integration
- Karate levanta sus propios folios por scenario (idempotency key aleatoria)
- Helpers reusables en `tests/integration/src/test/java/features/helpers/`

## 9. Comandos rápidos

```bash
# Todo el backend
cd apps/api && ./mvnw verify

# Todo el frontend
cd apps/web && pnpm test:coverage

# Karate integration (requiere stack arriba)
docker compose up -d && cd tests/integration && mvn test

# E2E Playwright (requiere stack arriba)
docker compose up -d && cd tests/e2e && pnpm exec playwright test

# Todo junto
make test-all   # si existe un Makefile
```
