---
name: HU-006/007 Coverage Options and Premium Calculation implementation
description: Key decisions, patterns, and invariants from HU-006 (OpcionesCobertura) and HU-007 (CalculoPrima) implementation
type: project
---

## Key facts

- `CoberturaReglaVioladaException` maps to HTTP 422 via `GlobalExceptionHandler`; `IllegalArgumentException` still maps to 400 — do NOT conflate these
- `SinUbicacionesCalculablesException` maps to HTTP 422 and carries `List<PrimaPorUbicacion>` in the response body
- `OpcionesCobertura` is a record with a compact constructor that throws `CoberturaReglaVioladaException` when all 14 flags are false
- `Cotizacion` now has a 7-param constructor `(numeroFolio, ubicaciones, version, fechaUltimaActualizacion, opcionesCobertura, resultadoCalculo, estado)` plus a backward-compat 4-param constructor that delegates with nulls
- `actualizarResultadoCalculo()` increments version AND sets `estado=CALCULADO` in one call — use cases must NOT call `actualizarEstado` separately afterward
- `EjecutarCalculoUseCase` returns `EjecutarCalculoResult(ResultadoCalculo, Cotizacion)` — not just `ResultadoCalculo`
- Catalog tables use `cotizacion_*` prefix (V5 migration) to avoid conflict with V1 tables (`parametros_calculo`, `tarifas_incendio`, `factores_equipo_electronico`, `catalogo_cp_zonas`)
- `CalculoPrimaService` is a `@Service` but receives `CatalogoTarifasRepository` as a method parameter, not injected field
- Default `valorAsegurado = 1_000_000.00`; ROBO_FACTOR = 0.15; equipo fallback factor = 0.0045
- `DatosCotizacion` stores opciones/resultado/estado via inner POJOs (`OpcionesCoberturaDatos`, `ResultadoCalculoDatos`, etc.) serialized as JSONB
- `IfMatchHeaderInterceptor` only guards PUT/PATCH — `POST /calculate` header is handled by Spring's `@RequestHeader` (returns 400 if missing, not 428)

**Why:** Spec had several inconsistencies with codebase (enum name `CALCULADO`, constructor param order, V1 table name conflicts). These decisions reconcile spec with real code.

**How to apply:** When extending calculation logic, always go through `CalculoPrimaService` (domain service), never add DB calls to it. New coverage components follow the same pattern: check cobertura flag → look up rate from catalog → multiply by valorAsegurado or base prima.
