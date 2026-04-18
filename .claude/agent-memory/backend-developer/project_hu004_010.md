---
name: HU-004/005/009/010 Cotizacion aggregate implementation
description: Key decisions and patterns from implementing HU-004 (CRUD ubicaciones), HU-005 (edición puntual), HU-009 (versionado optimista), HU-010 (validaciones)
type: project
---

HU-004/005/009/010 added the Cotizacion aggregate with ubicaciones JSONB, 4 use cases, a REST controller at `/api/v1/quotes`, and If-Match versioning.

**Why:** Insurance quoter needs per-location configuration with validation alerts and optimistic concurrency control before premium calculation.

**How to apply:** When extending Cotizacion or Ubicacion: follow the same immutable builder pattern (Cotizacion returns new instance from `agregarUbicacion`/`editarUbicacion`). ETag = integer version string. IfMatchHeaderInterceptor guards all PUT/PATCH on `/api/v1/quotes/**`.

Key decisions:
- Cotizacion created lazy on first `RegistrarUbicacionUseCase` call (not in `CrearFolioUseCase`).
- `Ubicacion.indice` = position in list at insertion time — immutable after creation.
- `ValidadorUbicacion` is a pure domain service (no Spring), registered as `@Bean` in `AppConfig`.
- `CoreZipCodeClient` calls `GET {core-base-url}/v1/zip-codes/validate?cp={cp}`; returns `Optional.empty()` on 404 or circuit breaker fallback.
- `VersionConflictException` includes `currentVersion`, `receivedVersion`, `numeroFolio` as ProblemDetail properties (409).
- `LayoutCapacityExceededException` → 409 type `capacity-exceeded`.
- `UbicacionNotFoundException` → 404.
- `IfMatchHeaderInterceptor` intercepts only PUT/PATCH; GET passes freely. Registered via `WebConfig` (separate from `AppConfig`).
- JSONB serialized via `CotizacionDatosConverter` → `DatosCotizacion` → `List<UbicacionJson>` with nested `GiroJson`, `ZonaCatastroficaJson`, `AlertaBloquenanteJson` inner classes.
- Controller path variable is `folio` (not `numeroFolio` as in FolioController).
- `GET /api/v1/quotes/{folio}/locations/summary` excluded from If-Match interceptor path patterns.
