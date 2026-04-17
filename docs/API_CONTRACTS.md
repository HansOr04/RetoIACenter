# Contratos de API

## Índice

1. [Convenciones generales](#1-convenciones-generales)
2. [Endpoints de Folios](#2-endpoints-de-folios)
3. [Endpoints de Ubicaciones](#3-endpoints-de-ubicaciones)
4. [Endpoint de Cálculo](#4-endpoint-de-cálculo)
5. [Manejo de errores (RFC 7807)](#5-manejo-de-errores-rfc-7807)
6. [Contratos del Core Stub](#6-contratos-del-core-stub)

---

## 1. Convenciones generales

- **Base URL backend**: `http://localhost:8080/api/v1`
- **Base URL core-stub**: `http://localhost:4000/v1`
- **Content-Type**: `application/json`
- **Formato de fechas**: ISO 8601 (`2026-04-17T10:30:00Z`)
- **IDs**: UUID v4

---

## 2. Endpoints de Folios

> TODO: documentar request/response completo por endpoint

### `POST /api/v1/folios`
### `GET /api/v1/folios`
### `GET /api/v1/folios/{id}`
### `PUT /api/v1/folios/{id}/datos-generales`
### `PUT /api/v1/folios/{id}/layout`
### `PATCH /api/v1/folios/{id}/estado`

---

## 3. Endpoints de Ubicaciones

> TODO: documentar request/response

### `POST /api/v1/folios/{id}/ubicaciones`
### `PUT /api/v1/folios/{id}/ubicaciones/{idx}`
### `PATCH /api/v1/folios/{id}/ubicaciones/{idx}`
### `DELETE /api/v1/folios/{id}/ubicaciones/{idx}`
### `PUT /api/v1/folios/{id}/ubicaciones/{idx}/coberturas`

---

## 4. Endpoint de Cálculo

> TODO: documentar request/response con ejemplo de prima calculada

### `POST /api/v1/folios/{id}/calcular`

---

## 5. Manejo de errores (RFC 7807)

Todos los errores HTTP siguen el estándar `ProblemDetail`:

```json
{
  "type": "https://cotizador.sofka.com/errors/folio-not-found",
  "title": "Folio no encontrado",
  "status": 404,
  "detail": "No existe un folio con ID: 550e8400-e29b-41d4-a716-446655440000",
  "instance": "/api/v1/folios/550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 6. Contratos del Core Stub

> Ver `apps/core-stub/openapi.yaml` para la especificación completa.
