# HU-001 · Crear folio con idempotencia

**Como** usuario del cotizador
**Quiero** crear un nuevo folio de cotización enviando una clave de idempotencia
**Para que** los reintentos de red no dupliquen cotizaciones y el proceso sea confiable

## Criterios de aceptación

- **CA-01 · Creación exitosa de folio**
  - Dado que el sistema está operativo y el core-stub responde
  - Cuando envío `POST /api/v1/folios` con header `X-Idempotency-Key: abc-123` y body `{ "tipoNegocio": "COMERCIAL", "codigoAgente": "AGT-001" }`
  - Entonces el sistema responde `201 Created`
  - Y el body contiene `numeroFolio` con valor alfanumérico único, `estadoCotizacion: "BORRADOR"`, `version: 1`, `fechaCreacion` y `fechaUltimaActualizacion` en ISO 8601

- **CA-02 · Idempotencia en reintento**
  - Dado que ya existe un folio creado con `X-Idempotency-Key: abc-123`
  - Cuando envío exactamente la misma petición `POST /api/v1/folios` con el mismo header
  - Entonces el sistema responde `200 OK` con el mismo folio original (mismo `numeroFolio`, misma `version`)
  - Y no se crea un registro duplicado en la tabla `folios`

- **CA-03 · Header de idempotencia ausente**
  - Dado que envío `POST /api/v1/folios` sin el header `X-Idempotency-Key`
  - Cuando el servidor procesa la petición
  - Entonces responde `400 Bad Request`
  - Y el body es un `ProblemDetail` con `title: "Header requerido"` y `detail` explicando que `X-Idempotency-Key` es obligatorio

- **CA-04 · Core-stub caído — circuit breaker**
  - Dado que el core-stub no responde (timeout o error de conexión)
  - Cuando envío `POST /api/v1/folios` con header válido
  - Entonces el sistema responde `503 Service Unavailable`
  - Y el body es un `ProblemDetail` con `title: "Servicio no disponible"` y `detail: "El servicio core no está disponible temporalmente"`

- **CA-05 · Body opcional vacío**
  - Dado que el sistema está operativo
  - Cuando envío `POST /api/v1/folios` con header `X-Idempotency-Key: xyz-999` y body vacío `{}`
  - Entonces el sistema responde `201 Created` con folio en estado `BORRADOR`
  - Y los campos `tipoNegocio` y `codigoAgente` quedan nulos en el registro

- **CA-06 · Clave de idempotencia con longitud inválida**
  - Dado que envío `X-Idempotency-Key` con más de 100 caracteres
  - Cuando el servidor procesa la petición
  - Entonces responde `400 Bad Request` con `ProblemDetail` indicando longitud máxima

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | No depende de ninguna otra HU del proyecto para implementarse ni desplegarse |
| Negotiable | ✅ | El mecanismo de idempotencia (header vs. body field) y el formato de `numeroFolio` son negociables |
| Valuable | ✅ | Sin folio no existe ningún proceso de cotización; es el punto de entrada del dominio |
| Estimable | ✅ | Estimación: 5 horas (endpoint + entidad + use case + repositorio + tests + circuit breaker) |
| Small | ✅ | Cabe en un día de trabajo; el circuit breaker usa configuración Resilience4j estándar |
| Testable | ✅ | Cada CA tiene condición de entrada y salida verificable de forma automatizada |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar

1. **Endpoint:** `POST /api/v1/folios` — crea o recupera un folio según la clave de idempotencia
2. **Header de validación:** `X-Idempotency-Key` extraído en el controller antes de delegar
3. **Entidad de dominio:** `Folio` en `domain/model/` con campos: `id`, `numeroFolio`, `idempotencyKey`, `estado`, `tipoNegocio`, `codigoAgente`, `version`, `fechaCreacion`, `fechaUltimaActualizacion`
4. **Value Object:** `EstadoCotizacion` (enum BORRADOR, CALCULADO, EMITIDO, CANCELADO) en `domain/model/`
5. **Use case:** `CrearFolioUseCase` en `application/usecase/` — verifica idempotencia, invoca core-stub, persiste
6. **Puerto de repositorio:** `FolioRepository` en `domain/port/` con métodos `save(Folio)`, `findByIdempotencyKey(String)`, `findByNumeroFolio(String)`
7. **Puerto de servicio externo:** `CoreFolioService` en `domain/port/` con método `generarNumeroFolio(String codigoAgente)`
8. **Adaptador JPA:** `FolioJpaAdapter` en `infrastructure/persistence/` — implementa `FolioRepository`
9. **Entidad JPA:** `FolioJpaEntity` en `infrastructure/persistence/` — mapeada a tabla `folios`
10. **Cliente HTTP al core-stub:** `CoreFolioClient` en `infrastructure/http/` — implementa `CoreFolioService` con Resilience4j
11. **Controller:** `FolioController` en `interfaces/rest/` — extrae header, delega, mapea respuesta
12. **DTO request:** `CrearFolioRequest` en `interfaces/rest/dto/` (campos opcionales `tipoNegocio`, `codigoAgente`)
13. **DTO response:** `FolioResponse` en `interfaces/rest/dto/`
14. **Excepción:** `FolioYaExisteException` y `CoreServiceUnavailableException` en `domain/exception/`
15. **Migración SQL:** `V3__add_idempotency_and_layout.sql` — agrega columna `idempotency_key` e `idempotency_status` a `folios`

### DÓNDE en la arquitectura

```
POST /api/v1/folios  +  Header: X-Idempotency-Key
  ↓
interfaces/rest/FolioController
  ↓  valida header, construye CrearFolioCommand(key, tipoNegocio, codigoAgente)
application/usecase/CrearFolioUseCase
  ├─► domain/port/FolioRepository.findByIdempotencyKey(key)
  │     ← infrastructure/persistence/FolioJpaAdapter
  │     ← PostgreSQL tabla folios
  │   Si existe → retorna Folio existente (idempotencia)
  │   Si no existe:
  ├─► domain/port/CoreFolioService.generarNumeroFolio(codigoAgente)
  │     ← infrastructure/http/CoreFolioClient  [Resilience4j circuit breaker]
  │     ← GET http://core-stub:4000/v1/folios  (fixture de numeración)
  └─► domain/port/FolioRepository.save(nuevoFolio)
        ← infrastructure/persistence/FolioJpaAdapter
        ← PostgreSQL INSERT INTO folios
  ↓
interfaces/rest/FolioController
  ↓  mapea Folio → FolioResponse (MapStruct)
HTTP 201 Created / 200 OK
```

### POR QUÉ desde la perspectiva del dominio

El folio es el **agregado raíz** del dominio de cotización. Todo proceso — captura de datos generales, configuración de ubicaciones, cálculo de prima — existe como parte del ciclo de vida de un folio. La idempotencia es una garantía de negocio crítica: un agente que recarga la pantalla o tiene un problema de red no debe generar cotizaciones duplicadas que confundan al asegurado. El circuit breaker protege la disponibilidad del cotizador ante fallos del sistema core, que en producción sería un ERP externo sobre el que no se tiene control de SLA.

## Contrato API

### Request

```http
POST /api/v1/folios
Content-Type: application/json
X-Idempotency-Key: abc-123-unique-key

{
  "tipoNegocio": "COMERCIAL",
  "codigoAgente": "AGT-001"
}
```

### Response — caso exitoso (creación)

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "numeroFolio": "F2026-0042",
  "estadoCotizacion": "BORRADOR",
  "version": 1,
  "fechaCreacion": "2026-04-18T10:30:00Z",
  "fechaUltimaActualizacion": "2026-04-18T10:30:00Z",
  "tipoNegocio": "COMERCIAL",
  "codigoAgente": "AGT-001"
}
```

### Response — caso exitoso (idempotencia)

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "numeroFolio": "F2026-0042",
  "estadoCotizacion": "BORRADOR",
  "version": 1,
  "fechaCreacion": "2026-04-18T10:30:00Z",
  "fechaUltimaActualizacion": "2026-04-18T10:30:00Z"
}
```

### Response — casos de error

- `400 Bad Request` — header `X-Idempotency-Key` ausente o con longitud > 100 caracteres
- `503 Service Unavailable` — core-stub no responde (circuit breaker Resilience4j activado)

Todos los errores usan RFC 7807 `ProblemDetail`:
```json
{
  "type": "https://cotizador.sofka.com/errors/header-requerido",
  "title": "Header requerido",
  "status": 400,
  "detail": "El header X-Idempotency-Key es obligatorio para crear un folio",
  "instance": "/api/v1/folios"
}
```

## Modelo de datos afectado

### Migración V3 requerida

```sql
-- V3: Agregar idempotency_key a folios y columnas de layout
ALTER TABLE folios
  ADD COLUMN idempotency_key   VARCHAR(100) UNIQUE,
  ADD COLUMN tipo_negocio      VARCHAR(50),
  ADD COLUMN layout_ubicaciones JSONB NOT NULL DEFAULT 'null'::jsonb,
  ADD COLUMN datos_generales   JSONB NOT NULL DEFAULT '{}'::jsonb;

CREATE INDEX idx_folios_idempotency ON folios(idempotency_key);
```

### Campos del agregado `Folio` que se leen/escriben en esta HU

| Campo | Operación | Valor inicial |
|---|---|---|
| `id` | INSERT | UUID generado |
| `numero_folio` | INSERT | Alfanumérico del core-stub |
| `idempotency_key` | INSERT / SELECT | Valor del header |
| `status` | INSERT | `BORRADOR` |
| `tipo_negocio` | INSERT | Del request body (nullable) |
| `agent_id` | INSERT | `codigoAgente` del request (nullable) |
| `version` | INSERT | `1` |
| `created_at` | INSERT | `now()` |
| `updated_at` | INSERT | `now()` |

## Reglas de negocio aplicadas

- "la cotización se identifica por `numeroFolio`" — cada folio creado recibe un `numeroFolio` único generado por el sistema.
- "el backend debe persistir la cotización como agregado principal" — el folio se persiste en la tabla `folios` como agregado raíz del dominio.
- La generación del número de folio en el stub se sirve desde `GET /v1/folios` del core-stub; en esta implementación el backend genera el número secuencial internamente usando el timestamp y un contador atómico si el core-stub no tiene endpoint dedicado (ver Observaciones).

## Trazabilidad

- **Endpoints afectados:** `POST /api/v1/folios`
- **Tablas afectadas:** `folios` (INSERT + migración V3 para `idempotency_key`)
- **Componentes frontend relacionados:** HU-F01 (botón "Crear cotización")
- **Test cases relacionados:** TC-001-a (creación exitosa), TC-001-b (idempotencia reintento), TC-001-c (header ausente → 400), TC-001-d (circuit breaker → 503), TC-001-e (body vacío → 201), TC-001-f (key muy larga → 400)
- **Skills ASDD invocados:** `skill_backend_clean-code-reviewer`, `skill_backend_integration-test-generator`
- **Reporte de output esperado:** `specs/output/backend/HU-001-report.md`

## Definition of Done

- [ ] Código implementado y compilando
- [ ] Tests unitarios del use case con cobertura ≥ 80%
- [ ] Test de integración Karate pasando (TC-001-a a TC-001-f)
- [ ] Contrato API documentado en OpenAPI
- [ ] Reporte en `specs/output/backend/HU-001-report.md` generado
- [ ] Self-review de clean code aprobado
- [ ] Sin librerías deprecadas
- [ ] Manejo de errores con ProblemDetail
- [ ] Logs estructurados añadidos
- [ ] Migración V3 incluida y aplicada

## Estado

- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Documentación
