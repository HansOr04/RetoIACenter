# Contratos de API

> Referencia completa de los 13 endpoints REST del backend `plataforma-danos-back`. Cada endpoint incluye request, response de éxito, responses de error y ejemplo curl ejecutable.

## Convenciones generales

- **Base URL:** `http://localhost:8080/api/v1`
- **Content-Type:** `application/json` en todos los endpoints
- **Versionado:** todas las rutas prefijadas con `/v1`
- **Manejo de errores:** formato RFC 7807 `application/problem+json` en todas las respuestas de error
- **Concurrencia optimista:** operaciones de escritura requieren header `If-Match: {version}` y retornan `ETag: {nueva-version}`
- **Idempotencia:** creación de folios requiere header `X-Idempotency-Key`

## Códigos HTTP estándar

| Código | Cuándo |
|---|---|
| 200 OK | Lectura exitosa o escritura exitosa sobre recurso existente |
| 201 Created | Folio creado por primera vez |
| 400 Bad Request | Body inválido, campos obligatorios faltantes |
| 404 Not Found | Folio o índice de ubicación inexistente |
| 409 Conflict | Versión obsoleta en If-Match (optimistic lock) |
| 422 Unprocessable Entity | Validación de negocio falla (ej. cálculo sin ubicaciones válidas) |
| 428 Precondition Required | Falta header If-Match en una escritura |
| 503 Service Unavailable | Core-stub no responde (circuit breaker abierto) |

## Formato de error estándar (RFC 7807)

```http
HTTP/1.1 409 Conflict
Content-Type: application/problem+json

{
  "type": "https://errors.cotizador.sofka/version-conflict",
  "title": "Conflicto de versión",
  "status": 409,
  "detail": "La cotización fue modificada por otro proceso",
  "instance": "/api/v1/quotes/F2026-1473/general-info",
  "numeroFolio": "F2026-1473",
  "currentVersion": 10,
  "receivedVersion": 7
}
```

---

# Endpoints

## 1. Crear folio

`POST /api/v1/folios`

Crea un nuevo folio con idempotencia garantizada.

### Request

```http
POST /api/v1/folios
Content-Type: application/json
X-Idempotency-Key: any-unique-string-from-client
```

```json
{
  "tipoNegocio": "COMERCIAL",
  "codigoAgente": "AG-001"
}
```

### Response 201 Created

```json
{
  "numeroFolio": "F2026-1473",
  "estadoCotizacion": "INICIADA",
  "version": 1,
  "fechaCreacion": "2026-04-19T10:00:00Z",
  "fechaUltimaActualizacion": "2026-04-19T10:00:00Z"
}
```

### Response 200 OK (reintento con misma idempotency key)

Mismo body que el 201 original.

### Response 400 Bad Request

- Falta header `X-Idempotency-Key`
- Body malformado

### Ejemplo curl

```bash
curl -X POST http://localhost:8080/api/v1/folios \
  -H "Content-Type: application/json" \
  -H "X-Idempotency-Key: demo-$(date +%s)" \
  -d '{"tipoNegocio":"COMERCIAL","codigoAgente":"AG-001"}'
```

---

## 2. Consultar datos generales

`GET /api/v1/folios/{numeroFolio}/datos-generales`

Consulta los datos generales de un folio específico.

### Request

```http
GET /api/v1/folios/{numeroFolio}/datos-generales
```

### Response 200 OK

```json
{
  "numeroFolio": "F2026-1473",
  "estadoCotizacion": "INICIADA",
  "version": 1,
  "fechaUltimaActualizacion": "2026-04-19T10:00:00Z",
  "datosGenerales": {
    "nombreTomador": "Empresa XYZ",
    "rucCedula": "1234567890",
    "correoElectronico": "contacto@xyz.com",
    "telefonoContacto": "5551234567",
    "tipoInmueble": "Edificio",
    "usoPrincipal": "Oficinas",
    "anoConstruccion": 2010,
    "numeroPisos": 5,
    "descripcion": "Sede principal corporativa"
  }
}
```

### Response 404 Not Found

Si el folio no existe.

### Ejemplo curl

```bash
curl -X GET http://localhost:8080/api/v1/folios/F2026-1473/datos-generales \
  -H "Accept: application/json"
```

---

## 3. Actualizar datos generales

`PUT /api/v1/folios/{numeroFolio}/datos-generales`

Actualiza los datos generales de un folio específico.

### Request

```http
PUT /api/v1/folios/{numeroFolio}/datos-generales
Content-Type: application/json
```

```json
{
  "nombreTomador": "Empresa XYZ Modificada",
  "rucCedula": "1234567890",
  "correoElectronico": "nuevo_contacto@xyz.com",
  "telefonoContacto": "5559876543",
  "tipoInmueble": "Edificio",
  "usoPrincipal": "Oficinas",
  "anoConstruccion": 2010,
  "numeroPisos": 5,
  "descripcion": "Sede principal corporativa actualizada"
}
```

### Response 200 OK

```json
{
  "numeroFolio": "F2026-1473",
  "estadoCotizacion": "DATOS_COMPLETOS",
  "version": 2,
  "fechaUltimaActualizacion": "2026-04-19T10:05:00Z",
  "datosGenerales": {
    "nombreTomador": "Empresa XYZ Modificada",
    "rucCedula": "1234567890",
    "correoElectronico": "nuevo_contacto@xyz.com",
    "telefonoContacto": "5559876543",
    "tipoInmueble": "Edificio",
    "usoPrincipal": "Oficinas",
    "anoConstruccion": 2010,
    "numeroPisos": 5,
    "descripcion": "Sede principal corporativa actualizada"
  }
}
```

### Response 400 Bad Request

- Body de la petición malformado o faltan campos requeridos

### Response 404 Not Found

Si el folio no existe.

### Ejemplo curl

```bash
curl -X PUT http://localhost:8080/api/v1/folios/F2026-1473/datos-generales \
  -H "Content-Type: application/json" \
  -d '{"nombreTomador":"Empresa XYZ","rucCedula":"123456789","correoElectronico":"a@b.com","telefonoContacto":"1234567890","tipoInmueble":"Casa","usoPrincipal":"Habitacional","anoConstruccion":2000,"numeroPisos":2,"descripcion":"Casa habitacion"}'
```

---

## 4. Consultar layout de ubicaciones

`GET /api/v1/folios/{numeroFolio}/ubicaciones/layout`

Retorna la configuración estructural (layout) de cuántas ubicaciones y qué secciones aplican.

### Request

```http
GET /api/v1/folios/{numeroFolio}/ubicaciones/layout
```

### Response 200 OK

```json
{
  "numeroFolio": "F2026-1473",
  "estadoCotizacion": "INICIADA",
  "version": 2,
  "fechaUltimaActualizacion": "2026-04-19T10:10:00Z",
  "layout": {
    "numeroUbicaciones": 3,
    "seccionesAplican": {
      "direccion": true,
      "datosTecnicos": true,
      "giroComercial": false,
      "garantias": true
    }
  }
}
```

### Response 404 Not Found

Si el folio no existe.

### Ejemplo curl

```bash
curl -X GET http://localhost:8080/api/v1/folios/F2026-1473/ubicaciones/layout \
  -H "Accept: application/json"
```

---

## 5. Actualizar layout de ubicaciones

`PUT /api/v1/folios/{numeroFolio}/ubicaciones/layout`

Actualiza la configuración estructural (layout) de ubicaciones para el cotizador.

### Request

```http
PUT /api/v1/folios/{numeroFolio}/ubicaciones/layout
Content-Type: application/json
```

```json
{
  "numeroUbicaciones": 3,
  "seccionesAplican": {
    "direccion": true,
    "datosTecnicos": true,
    "giroComercial": true,
    "garantias": true
  }
}
```

### Response 200 OK

```json
{
  "numeroFolio": "F2026-1473",
  "estadoCotizacion": "LAYOUT_CONFIGURADO",
  "version": 3,
  "fechaUltimaActualizacion": "2026-04-19T10:15:00Z",
  "layout": {
    "numeroUbicaciones": 3,
    "seccionesAplican": {
      "direccion": true,
      "datosTecnicos": true,
      "giroComercial": true,
      "garantias": true
    }
  }
}
```

### Response 400 Bad Request

- Tipos de datos inválidos en el body

### Response 404 Not Found

Si el folio no existe.

### Ejemplo curl

```bash
curl -X PUT http://localhost:8080/api/v1/folios/F2026-1473/ubicaciones/layout \
  -H "Content-Type: application/json" \
  -d '{"numeroUbicaciones":3,"seccionesAplican":{"direccion":true,"datosTecnicos":true,"giroComercial":true,"garantias":false}}'
```

---

## 6. Listar ubicaciones

`GET /api/v1/quotes/{numeroFolio}/locations`

Consulta todas las ubicaciones asociadas a un folio.

### Request

```http
GET /api/v1/quotes/{numeroFolio}/locations
```

### Response 200 OK

```json
[
  {
    "indice": 0,
    "nombreUbicacion": "Almacén Norte",
    "direccion": "Avenida Siempre Viva 123",
    "codigoPostal": "01000",
    "zonaCatastrofica": {
      "zonaTev": "3",
      "zonaFhm": "A"
    },
    "tipoConstructivo": "MACIZO",
    "nivel": 2,
    "anioConstruccion": 2012,
    "giro": {
      "codigo": "G001",
      "descripcion": "Venta al por menor",
      "claveIncendio": "I04"
    },
    "garantias": ["ALARMAS", "EXTINTORES"],
    "alertasBloqueantes": [],
    "estadoValidacion": "COMPLETA",
    "version": 0,
    "fechaUltimaActualizacion": null
  }
]
```

### Response 404 Not Found

Si el folio o cotización no existe.

### Ejemplo curl

```bash
curl -X GET http://localhost:8080/api/v1/quotes/F2026-1473/locations \
  -H "Accept: application/json"
```

---

## 7. Registrar ubicación

`PUT /api/v1/quotes/{numeroFolio}/locations`

Registra una nueva ubicación, incluyéndola al final de la lista.

### Request

```http
PUT /api/v1/quotes/{numeroFolio}/locations
Content-Type: application/json
If-Match: "3"
```

```json
{
  "nombreUbicacion": "Planta Sur",
  "direccion": "Calle Industria 456",
  "codigoPostal": "04000",
  "estado": "CDMX",
  "municipio": "Coyoacán",
  "colonia": "Centro Histórico",
  "ciudad": "CDMX",
  "tipoConstructivo": "LIGERO",
  "nivel": 1,
  "anioConstruccion": 2020,
  "giro": {
    "codigo": "G002",
    "descripcion": "Fábrica general",
    "claveIncendio": "I06"
  },
  "garantias": ["HIDRANTES"]
}
```

### Response 200 OK

Headers: `ETag: 4`

```json
{
  "indice": 1,
  "nombreUbicacion": "Planta Sur",
  "direccion": "Calle Industria 456",
  "codigoPostal": "04000",
  "zonaCatastrofica": {
    "zonaTev": "3",
    "zonaFhm": "B"
  },
  "tipoConstructivo": "LIGERO",
  "nivel": 1,
  "anioConstruccion": 2020,
  "giro": {
    "codigo": "G002",
    "descripcion": "Fábrica general",
    "claveIncendio": "I06"
  },
  "garantias": ["HIDRANTES"],
  "alertasBloqueantes": [],
  "estadoValidacion": "COMPLETA",
  "version": 4,
  "fechaUltimaActualizacion": "2026-04-19T10:20:00Z"
}
```

### Response 409 Conflict

- Header `If-Match` no coincide con la versión actual (modificado concurrentemente).

### Response 428 Precondition Required

- Falta header `If-Match`.

### Response 404 Not Found

- Folio no encontrado.

### Ejemplo curl

```bash
curl -X PUT http://localhost:8080/api/v1/quotes/F2026-1473/locations \
  -H "Content-Type: application/json" \
  -H "If-Match: 3" \
  -d '{"nombreUbicacion":"Oficina C","direccion":"Av C","codigoPostal":"01000","tipoConstructivo":"MACIZO"}'
```

---

## 8. Editar ubicación puntual

`PATCH /api/v1/quotes/{numeroFolio}/locations/{indice}`

Modifica específicamente una sección de una ubicación por su índice. 

### Request

```http
PATCH /api/v1/quotes/{numeroFolio}/locations/{indice}
Content-Type: application/json
If-Match: "4"
```

```json
{
  "nivel": 3,
  "garantias": ["ALARMAS", "HIDRANTES", "EXTINTORES"]
}
```

### Response 200 OK

Headers: `ETag: 5`

```json
{
  "indice": 0,
  "nombreUbicacion": "Almacén Norte",
  "direccion": "Avenida Siempre Viva 123",
  "codigoPostal": "01000",
  "zonaCatastrofica": {
    "zonaTev": "3",
    "zonaFhm": "A"
  },
  "tipoConstructivo": "MACIZO",
  "nivel": 3,
  "anioConstruccion": 2012,
  "giro": {
    "codigo": "G001",
    "descripcion": "Venta al por menor",
    "claveIncendio": "I04"
  },
  "garantias": ["ALARMAS", "HIDRANTES", "EXTINTORES"],
  "alertasBloqueantes": [],
  "estadoValidacion": "COMPLETA",
  "version": 5,
  "fechaUltimaActualizacion": "2026-04-19T10:25:00Z"
}
```

### Response 404 Not Found

- Ubicación con ese índice o folio no existe.

### Response 409 Conflict

- Versión obsoleta en `If-Match`.

### Ejemplo curl

```bash
curl -X PATCH http://localhost:8080/api/v1/quotes/F2026-1473/locations/0 \
  -H "Content-Type: application/json" \
  -H "If-Match: 4" \
  -d '{"nivel":3,"garantias":["ALARMAS","HIDRANTES"]}'
```

---

## 9. Resumen de ubicaciones

`GET /api/v1/quotes/{numeroFolio}/locations/summary`

Resume el estado de las ubicaciones del folio, indicando cuántas están completas, incompletas y calculables.

### Request

```http
GET /api/v1/quotes/{numeroFolio}/locations/summary
```

### Response 200 OK

```json
{
  "total": 3,
  "completas": 2,
  "incompletas": 1,
  "calculables": 2,
  "indicesIncompletos": [2],
  "detalleIncompletas": [
    {
      "indice": 2,
      "nombreUbicacion": "Local Este",
      "alertas": ["Falta definir tipoConstructivo"]
    }
  ]
}
```

### Response 404 Not Found

- Folio no encontrado.

### Ejemplo curl

```bash
curl -X GET http://localhost:8080/api/v1/quotes/F2026-1473/locations/summary \
  -H "Accept: application/json"
```

---

## 10. Consultar estado del folio

`GET /api/v1/folios/{numeroFolio}/estado`

Response incluye `progreso`, `esCalculable`, `alertasBloqueantes`, `estadoCotizacion`, etc.

### Request

```http
GET /api/v1/folios/{numeroFolio}/estado
```

### Response 200 OK

```json
{
  "numeroFolio": "F2026-1473",
  "estadoCotizacion": "UBICACIONES_COMPLETAS",
  "version": 5,
  "fechaCreacion": "2026-04-19T10:00:00Z",
  "fechaUltimaActualizacion": "2026-04-19T10:25:00Z",
  "porcentajeProgreso": 85.0,
  "esCalculable": true,
  "alertas": [],
  "seccionesCompletadas": ["DATOS_GENERALES", "UBICACIONES"]
}
```

### Response 404 Not Found

- Folio no encontrado.

### Ejemplo curl

```bash
curl -X GET http://localhost:8080/api/v1/folios/F2026-1473/estado \
  -H "Accept: application/json"
```

---

## 11. Consultar opciones de cobertura

`GET /api/v1/quotes/{numeroFolio}/coverage-options`

Consulta qué coberturas están activas o configuradas para la cotización.

### Request

```http
GET /api/v1/quotes/{numeroFolio}/coverage-options
```

### Response 200 OK

Headers: `ETag: 5`

```json
{
  "incendioEdificios": true,
  "incendioContenidos": true,
  "extensionCobertura": true,
  "catTev": false,
  "catFhm": false,
  "remocionEscombros": true,
  "gastosExtraordinarios": false,
  "perdidaRentas": true,
  "bi": false,
  "equipoElectronico": false,
  "robo": false,
  "dineroValores": false,
  "vidrios": false,
  "anunciosLuminosos": false,
  "warnings": [],
  "version": 5,
  "fechaUltimaActualizacion": null
}
```

### Response 404 Not Found

- Folio no encontrado.

### Ejemplo curl

```bash
curl -X GET http://localhost:8080/api/v1/quotes/F2026-1473/coverage-options \
  -H "Accept: application/json"
```

---

## 12. Actualizar opciones de cobertura

`PUT /api/v1/quotes/{numeroFolio}/coverage-options`

Body con los 14 booleanos de cobertura. Al menos uno debe ser `true` o retorna 422.

### Request

```http
PUT /api/v1/quotes/{numeroFolio}/coverage-options
Content-Type: application/json
If-Match: "5"
```

```json
{
  "incendioEdificios": true,
  "incendioContenidos": true,
  "extensionCobertura": true,
  "catTev": true,
  "catFhm": false,
  "remocionEscombros": true,
  "gastosExtraordinarios": false,
  "perdidaRentas": true,
  "bi": false,
  "equipoElectronico": false,
  "robo": false,
  "dineroValores": false,
  "vidrios": false,
  "anunciosLuminosos": false
}
```

### Response 200 OK

Headers: `ETag: 6`

```json
{
  "incendioEdificios": true,
  "incendioContenidos": true,
  "extensionCobertura": true,
  "catTev": true,
  "catFhm": false,
  "remocionEscombros": true,
  "gastosExtraordinarios": false,
  "perdidaRentas": true,
  "bi": false,
  "equipoElectronico": false,
  "robo": false,
  "dineroValores": false,
  "vidrios": false,
  "anunciosLuminosos": false,
  "warnings": ["La cobertura CAT TEV requiere que todas las ubicaciones tengan Zona C.T.E.V. válida"],
  "version": 6,
  "fechaUltimaActualizacion": "2026-04-19T10:35:00Z"
}
```

### Response 422 Unprocessable Entity

- Falla validación de negocio (ej. todas las coberturas en false).

### Response 409 Conflict

- Versión desactualizada.

### Ejemplo curl

```bash
curl -X PUT http://localhost:8080/api/v1/quotes/F2026-1473/coverage-options \
  -H "Content-Type: application/json" \
  -H "If-Match: 5" \
  -d '{"incendioEdificios":true,"incendioContenidos":true,"extensionCobertura":true,"catTev":true,"catFhm":false,"remocionEscombros":true,"gastosExtraordinarios":false,"perdidaRentas":true,"bi":false,"equipoElectronico":false,"robo":false,"dineroValores":false,"vidrios":false,"anunciosLuminosos":false}'
```

---

## 13. Ejecutar cálculo

`POST /api/v1/quotes/{numeroFolio}/calculate`

Ejecuta el cálculo de tarifas e importes. Body vacío. Retorna el desglose completo de primas por cada cobertura y por cada ubicación.

### Request

```http
POST /api/v1/quotes/{numeroFolio}/calculate
If-Match: "6"
```

### Response 200 OK

Headers: `ETag: 7`

```json
{
  "primaNeta": 6008.00,
  "primaComercial": 7510.00,
  "factorComercial": 1.25,
  "primasPorUbicacion": [
    {
      "indice": 0,
      "calculada": true,
      "total": 6008.00,
      "desglose": {
        "incendioEdificios": 1960.00,
        "incendioContenidos": 2352.00,
        "extensionCobertura": 98.00,
        "catTev": 1500.00,
        "catFhm": 0.00,
        "remocionEscombros": 39.20,
        "gastosExtraordinarios": 0.00,
        "perdidaRentas": 58.80,
        "bi": 0.00,
        "equipoElectronico": 0.00,
        "robo": 0.00,
        "dineroValores": 0.00,
        "vidrios": 0.00,
        "anunciosLuminosos": 0.00,
        "total": 6008.00
      },
      "alertasMensajes": []
    }
  ],
  "fechaCalculo": "2026-04-19T10:45:00Z",
  "estado": "CALCULADA",
  "version": 7
}
```

### Response 422 Unprocessable Entity

- Si ninguna ubicación es calculable (faltan datos, o reglas no cumplidas).

### Response 409 Conflict

- Versión del folio obsoleta en respecto a `If-Match`.

### Response 404 Not Found

- Si el folio no existe.

### Ejemplo curl

```bash
curl -X POST http://localhost:8080/api/v1/quotes/F2026-1473/calculate \
  -H "If-Match: 6"
```

---

# Tabla resumen de endpoints

| # | Método | Ruta | Propósito | Headers obligatorios |
|---|---|---|---|---|
| 1 | POST | /api/v1/folios | Crear folio | X-Idempotency-Key |
| 2 | GET | /api/v1/folios/{numeroFolio}/datos-generales | Leer datos generales | - |
| 3 | PUT | /api/v1/folios/{numeroFolio}/datos-generales | Actualizar datos generales | - (según diseño del controller) |
| 4 | GET | /api/v1/folios/{numeroFolio}/ubicaciones/layout | Leer layout | - |
| 5 | PUT | /api/v1/folios/{numeroFolio}/ubicaciones/layout | Actualizar layout | - (según diseño del controller) |
| 6 | GET | /api/v1/quotes/{folio}/locations | Listar ubicaciones | - |
| 7 | PUT | /api/v1/quotes/{folio}/locations | Registrar ubicación | If-Match |
| 8 | PATCH | /api/v1/quotes/{folio}/locations/{indice} | Editar ubicación puntual | If-Match |
| 9 | GET | /api/v1/quotes/{folio}/locations/summary | Resumen ubicaciones | - |
| 10 | GET | /api/v1/folios/{numeroFolio}/estado | Consultar estado | - |
| 11 | GET | /api/v1/quotes/{folio}/coverage-options | Leer cobertura | - |
| 12 | PUT | /api/v1/quotes/{folio}/coverage-options | Actualizar cobertura | If-Match |
| 13 | POST | /api/v1/quotes/{folio}/calculate | Ejecutar cálculo | If-Match |

# Contrato con core-stub

El backend consume estos endpoints del mock de core-stub (puerto 4000):

| Endpoint core | Uso |
|---|---|
| GET /v1/subscribers | HU-002 (validar asegurado) |
| GET /v1/agents | HU-002 (validar codigoAgente) |
| GET /v1/business-lines | HU-002 (validar tipoNegocio) |
| GET /v1/zip-codes/{cp} | HU-004 (validar código postal) |
| POST /v1/zip-codes/validate | HU-004 (validación estructurada) |
| POST /v1/folios | HU-001 (generador secuencial) |
| GET /v1/catalogs/risk-classification | HU-002 |
| GET /v1/catalogs/guarantees | HU-004 (validar garantías tarifables) |
| GET /v1/tariffs/* | HU-007 (consulta tarifas) |
