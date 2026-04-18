# HU-003 · Configuración del layout de ubicaciones

**Como** usuario del cotizador
**Quiero** definir cuántas ubicaciones tendrá la cotización y cómo se organizan antes de registrar los datos específicos de cada una
**Para que** el sistema prepare la estructura del formulario y evite reordenamientos costosos después de capturar datos

## Criterios de aceptación

- **CA-01 · Guardar layout válido**
  - Dado que existe el folio `F2026-0042` en estado `BORRADOR` con `version: 2`
  - Cuando envío `PUT /api/v1/folios/F2026-0042/ubicaciones/layout` con `cantidadUbicaciones: 3` y `version: 2`
  - Entonces el sistema responde `200 OK`
  - Y el body contiene `cantidadUbicaciones: 3`, `version: 3` y `fechaUltimaActualizacion` actualizada

- **CA-02 · Consultar layout existente**
  - Dado que el layout ya fue configurado en el folio
  - Cuando envío `GET /api/v1/folios/F2026-0042/ubicaciones/layout`
  - Entonces el sistema responde `200 OK` con `cantidadUbicaciones: 3` y los datos del layout guardados

- **CA-03 · Consultar layout sin configurar**
  - Dado que el folio existe pero nunca se configuró el layout
  - Cuando envío `GET /api/v1/folios/F2026-0042/ubicaciones/layout`
  - Entonces el sistema responde `200 OK` con `{ "configurado": false, "cantidadUbicaciones": null }`

- **CA-04 · Reducir cantidad con ubicaciones ya registradas**
  - Dado que el folio tiene 3 ubicaciones ya registradas (`ubicaciones` JSONB con 3 elementos)
  - Cuando envío `PUT /api/v1/folios/F2026-0042/ubicaciones/layout` con `cantidadUbicaciones: 2`
  - Entonces el sistema responde `409 Conflict`
  - Y el `ProblemDetail` incluye `detail: "No es posible reducir el layout a 2 ubicaciones: ya existen 3 ubicaciones registradas"`

- **CA-05 · Cantidad de ubicaciones inválida**
  - Dado que envío `PUT` con `cantidadUbicaciones: 0` o valor negativo
  - Cuando el servidor valida el body
  - Entonces responde `400 Bad Request`
  - Y el `ProblemDetail` incluye `detail: "cantidadUbicaciones debe ser mayor o igual a 1"`

- **CA-06 · Versión desactualizada**
  - Dado que el folio tiene `version: 4` en base de datos
  - Cuando envío `PUT` con `version: 2` (desactualizada)
  - Entonces el sistema responde `409 Conflict` con mensaje de versión actual

- **CA-07 · Layout ampliado — permitido siempre**
  - Dado que el folio tiene `cantidadUbicaciones: 2` configurado y 2 ubicaciones registradas
  - Cuando envío `PUT` con `cantidadUbicaciones: 5`
  - Entonces el sistema responde `200 OK` con el layout actualizado
  - Y las 2 ubicaciones existentes no se modifican

- **CA-08 · Folio inexistente**
  - Dado que el folio `F9999-9999` no existe
  - Cuando envío `PUT /api/v1/folios/F9999-9999/ubicaciones/layout`
  - Entonces el sistema responde `404 Not Found`

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ⚠️ | Depende de HU-001 (folio debe existir); independiente de HU-002 (datos generales no son prerrequisito) |
| Negotiable | ✅ | La estructura de `ubicacionesPrevistas[]` es negociable; `cantidadUbicaciones` es el mínimo necesario |
| Valuable | ✅ | Permite al stepper del frontend saber cuántos formularios mostrar; evita datos huérfanos |
| Estimable | ✅ | Estimación: 4 horas (PUT + GET + validación de conteo de ubicaciones existentes + tests) |
| Small | ✅ | Cabe en un día; la validación de conteo es una consulta JSONB sencilla |
| Testable | ✅ | La regla de conflicto tiene condición exacta; el versionado es determinístico |

**Veredicto:** APROBADA CON OBSERVACIONES (⚠️ dependencia de HU-001 resuelta con mock en tests unitarios)

## Análisis técnico

### QUÉ implementar

1. **Endpoints:**
   - `PUT /api/v1/folios/{numeroFolio}/ubicaciones/layout` — define o actualiza el layout
   - `GET /api/v1/folios/{numeroFolio}/ubicaciones/layout` — consulta el layout actual
2. **Objeto de valor:** `LayoutUbicaciones` en `domain/model/` con campos: `cantidadUbicaciones` (int, min 1), `ubicacionesPrevistas` (lista opcional de descriptores), `configurado` (boolean derivado)
3. **Lógica de dominio en entidad:** `Folio.configurarLayout(layout)` — valida que `cantidadUbicaciones >= ubicacionesActuales.size()` y actualiza `layoutUbicaciones`
4. **Use case:** `ConfigurarLayoutUseCase` en `application/usecase/`
5. **Use case:** `ConsultarLayoutUseCase` en `application/usecase/`
6. **Puerto:** `FolioRepository` (reutiliza el de HU-001/002) — método `findByNumeroFolio(String)`
7. **Método de dominio en `Folio`:** `contarUbicacionesRegistradas(): int` — cuenta los elementos en el campo JSONB `ubicaciones`
8. **Controller:** método `configurarLayout` y `consultarLayout` en `FolioController` (o `UbicacionController`)
9. **DTO request:** `ConfigurarLayoutRequest` con `version` (obligatorio, int ≥ 1), `cantidadUbicaciones` (obligatorio, int ≥ 1), `ubicacionesPrevistas` (opcional)
10. **DTO response:** `LayoutResponse` con `configurado`, `cantidadUbicaciones`, `ubicacionesRegistradas`, `version`, `fechaUltimaActualizacion`
11. **Excepción:** `LayoutConflictException` en `domain/exception/` para el caso CA-04
12. **Migración SQL:** incluida en `V3__add_idempotency_and_layout.sql` (columna `layout_ubicaciones JSONB`)

### DÓNDE en la arquitectura

```
PUT /api/v1/folios/{folio}/ubicaciones/layout
  ↓
interfaces/rest/FolioController#configurarLayout()
  ↓  @Valid ConfigurarLayoutRequest
application/usecase/ConfigurarLayoutUseCase
  ├─► domain/port/FolioRepository.findByNumeroFolio(folio)
  │     ← infrastructure/persistence/FolioJpaAdapter
  │   Si no existe → FolioNotFoundException → 404
  │   Si version != request.version → VersionConflictException → 409
  ├─► folio.contarUbicacionesRegistradas()
  │   Si request.cantidadUbicaciones < ubicacionesActuales → LayoutConflictException → 409
  └─► folio.configurarLayout(LayoutUbicaciones)  [dominio puro]
      domain/port/FolioRepository.save(folio)
        ← UPDATE folios SET layout_ubicaciones=?, version=version+1, updated_at=now()
  ↓
HTTP 200 OK con LayoutResponse

GET /api/v1/folios/{folio}/ubicaciones/layout
  ↓
interfaces/rest/FolioController#consultarLayout()
  ↓
application/usecase/ConsultarLayoutUseCase
  └─► domain/port/FolioRepository.findByNumeroFolio(folio)
HTTP 200 OK con LayoutResponse (configurado: false si layout_ubicaciones es null)
```

### POR QUÉ desde la perspectiva del dominio

El layout define la **capacidad del contenedor** de ubicaciones antes de que se ingresen datos. En seguros de daños, un mismo asegurado puede tener múltiples bienes (bodega en Quito, oficina en Guayaquil, almacén en Cuenca). Conocer de antemano cuántas ubicaciones se esperan permite al frontend diseñar un stepper con pasos numerados, y al backend rechazar inmediatamente intentos de reducir el layout cuando ya hay ubicaciones capturadas — lo cual evitaría pérdida de datos accidental. La validación es una regla de integridad del agregado, no de infraestructura.

## Contrato API

### Request

```http
PUT /api/v1/folios/F2026-0042/ubicaciones/layout
Content-Type: application/json

{
  "version": 2,
  "cantidadUbicaciones": 3,
  "ubicacionesPrevistas": [
    { "descripcion": "Bodega principal Quito" },
    { "descripcion": "Oficina Guayaquil" },
    { "descripcion": "Almacén Cuenca" }
  ]
}
```

### Response — caso exitoso

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "numeroFolio": "F2026-0042",
  "configurado": true,
  "cantidadUbicaciones": 3,
  "ubicacionesRegistradas": 0,
  "ubicacionesPrevistas": [
    { "descripcion": "Bodega principal Quito" },
    { "descripcion": "Oficina Guayaquil" },
    { "descripcion": "Almacén Cuenca" }
  ],
  "version": 3,
  "fechaUltimaActualizacion": "2026-04-18T12:00:00Z"
}
```

### Response — casos de error

- `400 Bad Request` — `cantidadUbicaciones` menor o igual a 0, o campo `version` ausente
- `404 Not Found` — el `numeroFolio` de la ruta no existe en base de datos
- `409 Conflict` — versión desactualizada, o reducción de layout con ubicaciones ya registradas

## Modelo de datos afectado

### Migración V3 (campo afectado)

```sql
-- Incluido en V3__add_idempotency_and_layout.sql
-- layout_ubicaciones almacena el objeto LayoutUbicaciones serializado
-- Ejemplo de valor:
-- {
--   "cantidadUbicaciones": 3,
--   "ubicacionesPrevistas": [{"descripcion":"Bodega principal"}]
-- }
```

### Campos del agregado `Folio` leídos/escritos en esta HU

| Campo | Operación | Notas |
|---|---|---|
| `numero_folio` | SELECT | Clave de búsqueda por path param |
| `layout_ubicaciones` | SELECT + UPDATE | JSONB — campo nuevo en V3 |
| `ubicaciones` | SELECT (solo lectura) | Para contar cuántas hay antes de validar reducción |
| `version` | SELECT + UPDATE | Incrementa +1 en cada PUT exitoso |
| `updated_at` | UPDATE | `now()` vía trigger |

## Reglas de negocio aplicadas

- "consulte y guarde la configuración del layout de ubicaciones" — esta HU implementa exactamente esa capacidad con persistencia en el campo `layout_ubicaciones` JSONB.
- "al editar secciones funcionales, debe incrementarse la versión" — el PUT actualiza `version + 1` y `fechaUltimaActualizacion`.
- La restricción de no reducir el layout por debajo del número de ubicaciones existentes es una **regla de integridad del agregado**: la entidad `Folio` es responsable de validarla en el método `configurarLayout(LayoutUbicaciones)`.

## Trazabilidad

- **Endpoints afectados:** `PUT /api/v1/folios/{folio}/ubicaciones/layout`, `GET /api/v1/folios/{folio}/ubicaciones/layout`
- **Tablas afectadas:** `folios` (UPDATE `layout_ubicaciones` JSONB — V3)
- **Componentes frontend relacionados:** HU-F03 (configuración del layout en el stepper)
- **Test cases relacionados:** TC-003-a (PUT layout válido → 200), TC-003-b (GET layout existente), TC-003-c (GET sin configurar → 200 configurado:false), TC-003-d (reducir con ubicaciones → 409), TC-003-e (cantidad ≤ 0 → 400), TC-003-f (version conflicto → 409), TC-003-g (ampliar layout → 200), TC-003-h (folio inexistente → 404)
- **Skills ASDD invocados:** `skill_backend_clean-code-reviewer`, `skill_backend_integration-test-generator`
- **Reporte de output esperado:** `specs/output/backend/HU-003-report.md`

## Definition of Done

- [ ] Código implementado y compilando
- [ ] Tests unitarios del use case con cobertura ≥ 80%
- [ ] Test de integración Karate pasando (TC-003-a a TC-003-h)
- [ ] Contrato API documentado en OpenAPI
- [ ] Reporte en `specs/output/backend/HU-003-report.md` generado
- [ ] Self-review de clean code aprobado
- [ ] Sin librerías deprecadas
- [ ] Manejo de errores con ProblemDetail
- [ ] Logs estructurados añadidos
- [ ] Validación en capa de dominio (no en controller ni use case)

## Estado

- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Documentación
