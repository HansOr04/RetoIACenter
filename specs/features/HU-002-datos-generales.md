# HU-002 · Captura y consulta de datos generales

**Como** usuario del cotizador
**Quiero** capturar y consultar los datos generales de una cotización (asegurado, agente, clasificación de riesgo, tipo de negocio)
**Para que** el contexto mínimo del riesgo quede registrado antes de configurar ubicaciones y calcular la prima

## Criterios de aceptación

- **CA-01 · Guardar datos generales exitosamente**
  - Dado que existe un folio `F2026-0042` en estado `BORRADOR` con `version: 1`
  - Cuando envío `PUT /api/v1/folios/F2026-0042/datos-generales` con body válido y `version: 1`
  - Entonces el sistema responde `200 OK`
  - Y el body contiene los datos guardados, `version: 2` y `fechaUltimaActualizacion` actualizada a `now()`

- **CA-02 · Consultar datos generales**
  - Dado que el folio `F2026-0042` tiene datos generales capturados
  - Cuando envío `GET /api/v1/folios/F2026-0042/datos-generales`
  - Entonces el sistema responde `200 OK` con el body completo de datos generales actuales

- **CA-03 · Agente inexistente en catálogo core**
  - Dado que el folio existe y el body contiene `"codigoAgente": "AGT-999"` que no existe en el core-stub
  - Cuando envío `PUT /api/v1/folios/F2026-0042/datos-generales`
  - Entonces el sistema responde `422 Unprocessable Entity`
  - Y el `ProblemDetail` incluye `detail: "El agente AGT-999 no existe en el catálogo"`

- **CA-04 · Folio inexistente**
  - Dado que no existe ningún folio con número `F9999-9999`
  - Cuando envío `PUT /api/v1/folios/F9999-9999/datos-generales` con body válido
  - Entonces el sistema responde `404 Not Found`
  - Y el `ProblemDetail` incluye `detail: "No existe un folio con número F9999-9999"`

- **CA-05 · Versión desactualizada — conflicto optimista**
  - Dado que el folio `F2026-0042` tiene `version: 3` en base de datos
  - Cuando envío `PUT /api/v1/folios/F2026-0042/datos-generales` con `version: 1` (desactualizada)
  - Entonces el sistema responde `409 Conflict`
  - Y el `ProblemDetail` incluye `detail: "El folio fue modificado por otra sesión. Versión actual: 3"`

- **CA-06 · Actualización parcial — no sobrescribe otras secciones**
  - Dado que el folio tiene ubicaciones configuradas en `ubicaciones`
  - Cuando envío `PUT /api/v1/folios/F2026-0042/datos-generales` con datos válidos
  - Entonces el sistema actualiza únicamente los campos de `datos_generales`
  - Y el campo `ubicaciones` del folio permanece sin cambios

- **CA-07 · Campos requeridos faltantes**
  - Dado que envío `PUT` con body `{}` (sin ningún campo)
  - Cuando el servidor valida la petición
  - Entonces responde `400 Bad Request` con `ProblemDetail` listando los campos requeridos: `rfcContratante`, `codigoAgente`

- **CA-08 · GET con folio sin datos generales aún**
  - Dado que el folio existe pero nunca se le capturaron datos generales
  - Cuando envío `GET /api/v1/folios/F2026-0042/datos-generales`
  - Entonces el sistema responde `200 OK` con todos los campos nulos o vacíos

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ⚠️ | Depende de HU-001 (el folio debe existir); la dependencia es mínima y el mock permite pruebas aisladas |
| Negotiable | ✅ | Los campos específicos de `datosAsegurado` y `datosConduccion` son negociables según el ramo |
| Valuable | ✅ | Sin datos del asegurado no se puede emitir la póliza ni personalizar coberturas |
| Estimable | ✅ | Estimación: 6 horas (PUT + GET + validación core + versionado + tests) |
| Small | ✅ | Cabe en un día; la validación de agente es una llamada HTTP sencilla al core-stub |
| Testable | ✅ | Cada CA define entrada, condición y resultado verificable; el versionado optimista es determinístico |

**Veredicto:** APROBADA CON OBSERVACIONES (⚠️ dependencia de HU-001 resuelta con mock en tests unitarios)

## Análisis técnico

### QUÉ implementar

1. **Endpoints:**
   - `PUT /api/v1/folios/{numeroFolio}/datos-generales` — actualiza parcialmente el agregado
   - `GET /api/v1/folios/{numeroFolio}/datos-generales` — consulta los datos generales actuales
2. **Objeto de valor:** `DatosGenerales` en `domain/model/` — encapsula rfcContratante, nombreContratante, codigoAgente, clasificacionRiesgo, tipoNegocio
3. **Objeto de valor:** `DatosAsegurado` en `domain/model/` — rfcContratante, nombreContratante, correo, telefono
4. **Objeto de valor:** `DatosConduccion` en `domain/model/` — codigoAgente, codigoRamo
5. **Use case:** `ActualizarDatosGeneralesUseCase` en `application/usecase/`
6. **Use case:** `ConsultarDatosGeneralesUseCase` en `application/usecase/`
7. **Puerto:** `FolioRepository` (extiende el de HU-001) — agregar método `findByNumeroFolio(String)`
8. **Puerto:** `CoreAgentService` en `domain/port/` — método `existeAgente(String codigoAgente): boolean`
9. **Adaptador HTTP:** `CoreAgentClient` en `infrastructure/http/` — llama a `GET /v1/agents/{id}` del core-stub
10. **Controller:** `DatosGeneralesController` en `interfaces/rest/` (o método adicional en `FolioController`)
11. **DTO request:** `ActualizarDatosGeneralesRequest` con `version` obligatorio, `datosAsegurado`, `datosConduccion`, `clasificacionRiesgo`, `tipoNegocio`
12. **DTO response:** `DatosGeneralesResponse`
13. **Excepción:** `FolioNotFoundException`, `AgenteNoEncontradoException`, `VersionConflictException` en `domain/exception/`

### DÓNDE en la arquitectura

```
PUT /api/v1/folios/{folio}/datos-generales
  ↓
interfaces/rest/FolioController#actualizarDatosGenerales()
  ↓  valida @Valid, extrae version del body
application/usecase/ActualizarDatosGeneralesUseCase
  ├─► domain/port/FolioRepository.findByNumeroFolio(folio)
  │     ← infrastructure/persistence/FolioJpaAdapter
  │   Si no existe → lanza FolioNotFoundException → 404
  │   Si version != body.version → lanza VersionConflictException → 409
  ├─► domain/port/CoreAgentService.existeAgente(codigoAgente)
  │     ← infrastructure/http/CoreAgentClient
  │     ← GET http://core-stub:4000/v1/agents/{id}
  │   Si no existe → lanza AgenteNoEncontradoException → 422
  └─► folio.actualizarDatosGenerales(datosGenerales)  [lógica en dominio]
      domain/port/FolioRepository.save(folio)
        ← infrastructure/persistence/FolioJpaAdapter
        ← PostgreSQL UPDATE folios SET datos_generales=?, version=version+1, updated_at=now()
  ↓
HTTP 200 OK con DatosGeneralesResponse

GET /api/v1/folios/{folio}/datos-generales
  ↓
interfaces/rest/FolioController#consultarDatosGenerales()
  ↓
application/usecase/ConsultarDatosGeneralesUseCase
  └─► domain/port/FolioRepository.findByNumeroFolio(folio)
        ← infrastructure/persistence/FolioJpaAdapter
HTTP 200 OK con DatosGeneralesResponse (o campos nulos si nunca se capturaron)
```

### POR QUÉ desde la perspectiva del dominio

Los datos generales son el **contexto del riesgo** que determina tarifas, comisiones y obligaciones legales. El RFC del asegurado identifica si es persona física o moral, con implicaciones fiscales directas. El agente define la cadena de distribución de la póliza. La clasificación de riesgo orienta qué coberturas son adecuadas. Sin estos datos no existe una cotización comercialmente válida. La actualización parcial es fundamental: el agente puede corregir un dato del asegurado sin perder las ubicaciones ya capturadas, que pueden llevar tiempo de ingreso.

## Contrato API

### Request

```http
PUT /api/v1/folios/F2026-0042/datos-generales
Content-Type: application/json

{
  "version": 1,
  "datosAsegurado": {
    "rfcContratante": "SOFK900101XXX",
    "nombreContratante": "Sofka Technologies S.A.S.",
    "correo": "seguros@sofka.com",
    "telefono": "+593-99-0000001"
  },
  "datosConduccion": {
    "codigoAgente": "AGT-001",
    "codigoRamo": "BL-DANOS"
  },
  "clasificacionRiesgo": "SOLIDA",
  "tipoNegocio": "COMERCIAL"
}
```

### Response — caso exitoso

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "numeroFolio": "F2026-0042",
  "version": 2,
  "fechaUltimaActualizacion": "2026-04-18T11:00:00Z",
  "datosAsegurado": {
    "rfcContratante": "SOFK900101XXX",
    "nombreContratante": "Sofka Technologies S.A.S.",
    "correo": "seguros@sofka.com",
    "telefono": "+593-99-0000001"
  },
  "datosConduccion": {
    "codigoAgente": "AGT-001",
    "codigoRamo": "BL-DANOS"
  },
  "clasificacionRiesgo": "SOLIDA",
  "tipoNegocio": "COMERCIAL"
}
```

### Response — casos de error

- `400 Bad Request` — campos requeridos faltantes (`rfcContratante`, `codigoAgente`) o formato de RFC inválido
- `404 Not Found` — folio con `numeroFolio` no existe en la base de datos
- `409 Conflict` — el campo `version` del body no coincide con la `version` actual en base de datos
- `422 Unprocessable Entity` — el `codigoAgente` no existe en el catálogo del core-stub

## Modelo de datos afectado

### Migración V3 (continuación)

```sql
-- Incluido en V3__add_idempotency_and_layout.sql
-- El campo datos_generales JSONB almacena el objeto DatosGenerales serializado
-- Los campos individuales rfc_contratante, nombre_contratante, agent_id permanecen
-- como columnas indexadas para búsquedas; datos_generales tiene el objeto completo.
```

### Campos del agregado `Folio` leídos/escritos en esta HU

| Campo | Operación | Notas |
|---|---|---|
| `numero_folio` | SELECT (por path param) | Clave de búsqueda |
| `rfc_contratante` | UPDATE | Columna individual + dentro de `datos_generales` JSONB |
| `nombre_contratante` | UPDATE | Columna individual + dentro de `datos_generales` JSONB |
| `agent_id` | UPDATE | Columna individual + dentro de `datos_generales` JSONB |
| `business_line_id` | UPDATE | Columna individual (codigoRamo) |
| `datos_generales` | UPDATE | JSONB completo serializado (campo nuevo V3) |
| `version` | SELECT + UPDATE (optimista) | Incrementa en +1 por cada PUT exitoso |
| `updated_at` | UPDATE | `now()` en cada PUT exitoso |

## Reglas de negocio aplicadas

- "las escrituras deben hacerse por actualización parcial" — el PUT modifica únicamente la sección `datos_generales` del agregado; las demás secciones (`ubicaciones`, `layout_ubicaciones`, `primas_por_ubicacion`) permanecen intactas.
- "al editar secciones funcionales, debe incrementarse la versión" — cada PUT exitoso incrementa `version` en 1 y actualiza `fechaUltimaActualizacion`.
- "debe actualizarse `fechaUltimaActualizacion`" — manejado por el trigger `update_folios_updated_at` de V1 + reflejado en la respuesta.

## Trazabilidad

- **Endpoints afectados:** `PUT /api/v1/folios/{folio}/datos-generales`, `GET /api/v1/folios/{folio}/datos-generales`
- **Tablas afectadas:** `folios` (UPDATE campos individuales + `datos_generales` JSONB)
- **Componentes frontend relacionados:** HU-F02 (formulario de datos generales)
- **Test cases relacionados:** TC-002-a (PUT exitoso + version++), TC-002-b (GET con datos), TC-002-c (agente inexistente → 422), TC-002-d (folio inexistente → 404), TC-002-e (version conflicto → 409), TC-002-f (actualización parcial no borra ubicaciones), TC-002-g (campos faltantes → 400), TC-002-h (GET sin datos → 200 con nulos)
- **Skills ASDD invocados:** `skill_backend_clean-code-reviewer`, `skill_backend_integration-test-generator`
- **Reporte de output esperado:** `specs/output/backend/HU-002-report.md`

## Definition of Done

- [ ] Código implementado y compilando
- [ ] Tests unitarios del use case con cobertura ≥ 80%
- [ ] Test de integración Karate pasando (TC-002-a a TC-002-h)
- [ ] Contrato API documentado en OpenAPI
- [ ] Reporte en `specs/output/backend/HU-002-report.md` generado
- [ ] Self-review de clean code aprobado
- [ ] Sin librerías deprecadas
- [ ] Manejo de errores con ProblemDetail
- [ ] Logs estructurados añadidos
- [ ] Actualización parcial verificada (campo `ubicaciones` no se modifica)

## Estado

- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Documentación
