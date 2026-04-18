---
name: HU-003 Layout Ubicaciones implementation
description: Key decisions and patterns established while implementing HU-003 (layout de ubicaciones JSONB feature)
type: project
---

HU-003 added `LayoutUbicaciones` and `SeccionesAplican` domain records plus two use cases, JSONB converter, two REST endpoints, and 3+8 tests to the Spring Boot hexagonal monorepo.

**Why:** Feature adds location-layout configuration to the cotizador folio aggregate; direccion=true is a hard domain invariant enforced in the LayoutUbicaciones compact constructor.

**How to apply:** When adding future JSONB fields to `Folio`: (1) create a no-Spring/no-JPA domain record, (2) create an `AttributeConverter` under `infrastructure/persistence/converter/` mirroring `DatosGeneralesConverter`, (3) add field to both `Folio.java` (with a wither method) and `FolioJpaEntity.java`, (4) extend both `toEntity()` and `toDomain()` in `FolioJpaAdapter`, (5) add use cases under `application/usecase/`, (6) inject new use cases as additional constructor params in `FolioController`.

Key decisions:
- `IllegalArgumentException` from domain compact constructor maps to HTTP 400 via `GlobalExceptionHandler.handleIllegalArgument` (type: regla-negocio).
- `actualizarLayoutUbicaciones` wither preserves `datosGenerales` field, and `actualizarDatosGenerales` wither now also preserves `layoutUbicaciones` field — both withers must carry all existing fields forward.
- Endpoints: `PUT /api/v1/folios/{numeroFolio}/ubicaciones/layout` and `GET /api/v1/folios/{numeroFolio}/ubicaciones/layout`.
