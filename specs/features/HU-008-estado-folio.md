# HU-008 · Consulta de estado y progreso del folio

**Como** usuario del cotizador y stepper del frontend
**Quiero** consultar el estado completo y el progreso de un folio en cualquier momento
**Para que** sepa qué secciones están completas, cuáles faltan, si la cotización es calculable, y cuál es la versión vigente

## Criterios de aceptación

- **CA-01 · Estado de folio recién creado**
  - Dado que el folio `F2026-0042` fue creado con HU-001 y no tiene ninguna sección adicional completada
  - Cuando envío `GET /api/v1/folios/F2026-0042/estado`
  - Entonces el sistema responde `200 OK`
  - Y el body incluye `estadoCotizacion: "BORRADOR"`, `progreso: { completadas: 1, total: 5, porcentaje: 20 }`, `esCalculable: false`, `alertasBloqueantes: ["Datos generales incompletos", "Sin ubicaciones configuradas"]`

- **CA-02 · Estado con todas las secciones completas**
  - Dado que el folio tiene datos generales, layout y al menos una ubicación con coberturas seleccionadas
  - Cuando envío `GET /api/v1/folios/F2026-0042/estado`
  - Entonces el body incluye `esCalculable: true`, `alertasBloqueantes: []`, `progreso: { completadas: 5, total: 5, porcentaje: 100 }`

- **CA-03 · Folio inexistente**
  - Dado que no existe el folio `F9999-9999`
  - Cuando envío `GET /api/v1/folios/F9999-9999/estado`
  - Entonces el sistema responde `404 Not Found` con `ProblemDetail`

- **CA-04 · Progreso parcial — datos generales completados pero sin ubicaciones**
  - Dado que el folio tiene datos generales y layout configurado, pero cero ubicaciones registradas
  - Cuando envío `GET /api/v1/folios/F2026-0042/estado`
  - Entonces el body incluye `progreso: { completadas: 3, total: 5, porcentaje: 60 }`, `esCalculable: false`
  - Y `alertasBloqueantes` contiene `"Sin ubicaciones registradas"`

- **CA-05 · El endpoint no modifica el folio (idempotente de lectura)**
  - Dado que el folio tiene `version: 3`
  - Cuando envío `GET /api/v1/folios/F2026-0042/estado` múltiples veces
  - Entonces la `version` permanece en `3` después de cada llamada
  - Y el `updated_at` no cambia

- **CA-06 · Folio en estado CALCULADO**
  - Dado que el folio ya fue calculado (HU-007 ejecutada exitosamente)
  - Cuando envío `GET /api/v1/folios/F2026-0042/estado`
  - Entonces `estadoCotizacion: "CALCULADO"`, `progreso: { completadas: 5, total: 5, porcentaje: 100 }`, `esCalculable: false` (ya fue calculado), `esEmitible: true`

- **CA-07 · Folio cancelado**
  - Dado que el folio está en estado `CANCELADO`
  - Cuando envío `GET /api/v1/folios/F2026-0042/estado`
  - Entonces `estadoCotizacion: "CANCELADO"`, `esCalculable: false`, `esEmitible: false`, `alertasBloqueantes: ["Folio cancelado — no se pueden realizar modificaciones"]`

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ⚠️ | Depende de HU-001 para existir; su lógica de progreso depende de HU-002 y HU-003 en ejecución, pero el endpoint en sí es independiente de implementar |
| Negotiable | ✅ | El cálculo de `progreso` (secciones 1-5) es negociable; lo que no es negociable es el campo `esCalculable` |
| Valuable | ✅ | Sin este endpoint el stepper del frontend no puede mostrar el estado real ni habilitar/deshabilitar pasos |
| Estimable | ✅ | Estimación: 4 horas (GET + lógica de progreso en dominio + tests) |
| Small | ✅ | Solo lectura; no persiste nada; la lógica de progreso es un método puro en la entidad |
| Testable | ✅ | Los estados de progreso son determinísticos según los campos del agregado |

**Veredicto:** APROBADA CON OBSERVACIONES (⚠️ la lógica de progreso se enriquece a medida que se implementen HU-002 a HU-006; la estructura del endpoint es estable desde el día 1)

## Análisis técnico

### QUÉ implementar

1. **Endpoint:** `GET /api/v1/folios/{numeroFolio}/estado` — solo lectura, no modifica el folio
2. **Objeto de valor:** `EstadoFolio` en `domain/model/` — representa el snapshot del estado del ciclo de vida
3. **Objeto de valor:** `ProgresoFolio` en `domain/model/` — encapsula `completadas`, `total`, `porcentaje`
4. **Objeto de valor:** `SeccionFolio` (enum) en `domain/model/` — DATOS_GENERALES, LAYOUT, UBICACIONES, COBERTURAS, CALCULO
5. **Método de dominio en `Folio`:** `calcularProgreso(): ProgresoFolio` — evalúa presencia de cada sección y devuelve el progreso
6. **Método de dominio en `Folio`:** `esCalculable(): boolean` — retorna true si todas las secciones previas al cálculo están completas
7. **Método de dominio en `Folio`:** `calcularAlertas(): List<String>` — lista las secciones incompletas como mensajes legibles
8. **Use case:** `ConsultarEstadoFolioUseCase` en `application/usecase/`
9. **Puerto:** `FolioRepository` (reutilizado) — método `findByNumeroFolio(String)`
10. **Controller:** método `consultarEstado` en `FolioController`
11. **DTO response:** `EstadoFolioResponse` con: `numeroFolio`, `estadoCotizacion`, `version`, `fechaCreacion`, `fechaUltimaActualizacion`, `progreso`, `esCalculable`, `esEmitible`, `alertasBloqueantes[]`

### DÓNDE en la arquitectura

```
GET /api/v1/folios/{folio}/estado
  ↓
interfaces/rest/FolioController#consultarEstado()
  ↓
application/usecase/ConsultarEstadoFolioUseCase
  └─► domain/port/FolioRepository.findByNumeroFolio(folio)
        ← infrastructure/persistence/FolioJpaAdapter
        ← SELECT * FROM folios WHERE numero_folio = ?
      Si no existe → FolioNotFoundException → 404
      ↓
  folio.calcularProgreso()         [lógica pura en dominio]
  folio.esCalculable()             [lógica pura en dominio]
  folio.calcularAlertas()          [lógica pura en dominio]
  folio.esEmitible()               [lógica pura en dominio]
      ↓
  MapStruct: Folio → EstadoFolioResponse
  ↓
HTTP 200 OK con EstadoFolioResponse
(NO se ejecuta ningún UPDATE ni INSERT)
```

### POR QUÉ desde la perspectiva del dominio

El estado del folio es la **brújula del proceso de cotización**. El agente necesita saber en cualquier momento cuánto falta para poder calcular, especialmente cuando retoma una cotización iniciada el día anterior. El campo `esCalculable` es una pre-condición de negocio: el cálculo de prima (HU-007) debe rechazar folios incalculables sin necesidad de validar cada sección individualmente en el use case de cálculo — basta con consultar `esCalculable()` en la entidad. Concentrar esta lógica en el dominio asegura que sea consistente sin importar desde qué caso de uso se invoque.

## Contrato API

### Request

```http
GET /api/v1/folios/F2026-0042/estado
```

### Response — caso exitoso (folio recién creado)

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "numeroFolio": "F2026-0042",
  "estadoCotizacion": "BORRADOR",
  "version": 1,
  "fechaCreacion": "2026-04-18T10:30:00Z",
  "fechaUltimaActualizacion": "2026-04-18T10:30:00Z",
  "progreso": {
    "completadas": 1,
    "total": 5,
    "porcentaje": 20,
    "secciones": [
      { "nombre": "FOLIO_CREADO",     "completada": true  },
      { "nombre": "DATOS_GENERALES",  "completada": false },
      { "nombre": "LAYOUT",           "completada": false },
      { "nombre": "UBICACIONES",      "completada": false },
      { "nombre": "COBERTURAS",       "completada": false }
    ]
  },
  "esCalculable": false,
  "esEmitible": false,
  "alertasBloqueantes": [
    "Datos generales incompletos",
    "Sin configuración de layout",
    "Sin ubicaciones registradas",
    "Sin coberturas seleccionadas"
  ]
}
```

### Response — caso exitoso (listo para calcular)

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "numeroFolio": "F2026-0042",
  "estadoCotizacion": "BORRADOR",
  "version": 8,
  "progreso": {
    "completadas": 5,
    "total": 5,
    "porcentaje": 100,
    "secciones": [
      { "nombre": "FOLIO_CREADO",    "completada": true },
      { "nombre": "DATOS_GENERALES", "completada": true },
      { "nombre": "LAYOUT",          "completada": true },
      { "nombre": "UBICACIONES",     "completada": true },
      { "nombre": "COBERTURAS",      "completada": true }
    ]
  },
  "esCalculable": true,
  "esEmitible": false,
  "alertasBloqueantes": []
}
```

### Response — casos de error

- `404 Not Found` — el `numeroFolio` de la ruta no existe en base de datos

## Modelo de datos afectado

### Sin cambios de schema

Este endpoint es de solo lectura. No requiere columnas nuevas en V3. Usa las columnas existentes del aggregado para calcular el progreso:

| Campo leído | Propósito |
|---|---|
| `status` | Determina `estadoCotizacion` y transiciones válidas |
| `datos_generales` JSONB | `!= null && != {}` → sección DATOS_GENERALES completada |
| `layout_ubicaciones` JSONB | `!= null` → sección LAYOUT completada |
| `ubicaciones` JSONB | `length > 0` → sección UBICACIONES completada |
| `ubicaciones` JSONB (coberturas) | Todos los elementos tienen coberturas → sección COBERTURAS completada |
| `version` | Devuelto en la respuesta |
| `created_at` / `updated_at` | Devueltos en la respuesta |

### Campos del agregado `Folio` leídos en esta HU

Todos los campos del aggregado son de solo lectura. No se escribe ningún campo.

## Reglas de negocio aplicadas

- "consulte el estado de la cotización" — esta HU implementa la consulta del estado con el objeto `EstadoFolio` enriquecido con progreso y alertas.
- "visualizar el progreso y estado del folio" — el stepper del frontend consume `GET /estado` para habilitar o deshabilitar pasos del wizard, según `progreso.secciones[].completada`.
- El campo `esCalculable` es una pre-condición que el use case de HU-007 (Calcular Prima) consultará en el dominio antes de ejecutar el cálculo, garantizando que no se calcule un folio con datos incompletos.

## Trazabilidad

- **Endpoints afectados:** `GET /api/v1/folios/{folio}/estado`
- **Tablas afectadas:** `folios` (SELECT únicamente — sin modificaciones)
- **Componentes frontend relacionados:** HU-F01 (stepper inicio), HU-F06 (visualización resultado)
- **Test cases relacionados:** TC-008-a (estado recién creado → progreso 1/5), TC-008-b (folio completo → esCalculable:true), TC-008-c (folio inexistente → 404), TC-008-d (progreso parcial → alertas), TC-008-e (idempotencia — version no cambia), TC-008-f (estado CALCULADO → esEmitible:true), TC-008-g (estado CANCELADO → alertas bloqueantes)
- **Skills ASDD invocados:** `skill_backend_clean-code-reviewer`, `skill_backend_integration-test-generator`
- **Reporte de output esperado:** `specs/output/backend/HU-008-report.md`

## Definition of Done

- [ ] Código implementado y compilando
- [ ] Tests unitarios del use case y de la entidad (métodos de progreso) con cobertura ≥ 80%
- [ ] Test de integración Karate pasando (TC-008-a a TC-008-g)
- [ ] Contrato API documentado en OpenAPI
- [ ] Reporte en `specs/output/backend/HU-008-report.md` generado
- [ ] Self-review de clean code aprobado
- [ ] Sin librerías deprecadas
- [ ] Manejo de errores con ProblemDetail
- [ ] Logs estructurados añadidos
- [ ] Verificado que el endpoint no modifica ningún campo del folio

## Estado

- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Documentación
