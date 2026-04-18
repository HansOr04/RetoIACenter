# HU-009 — Versionado Optimista con If-Match

## Estado: Implementado

## Archivos creados

### Infraestructura
- `infrastructure/config/IfMatchHeaderInterceptor.java` — `HandlerInterceptor` que intercepta PUT y PATCH
- `infrastructure/config/WebConfig.java` — `WebMvcConfigurer` registra el interceptor en `/api/v1/quotes/**`

### Excepciones de dominio
- `domain/exception/VersionConflictException.java` — con campos `currentVersion`, `receivedVersion`, `numeroFolio`

### Manejador de errores
- `GlobalExceptionHandler.java` — handlers añadidos para `VersionConflictException` → 409 con body extendido

## Comportamiento del interceptor

| Condición | Resultado |
|-----------|-----------|
| PUT sin `If-Match` | 428 Precondition Required con ProblemDetail |
| PATCH sin `If-Match` | 428 Precondition Required con ProblemDetail |
| GET cualquier path | Pasa sin validación |
| PUT/PATCH con `If-Match` presente | Pasa; validación de valor en use case |

## ETag en respuestas
- `PUT /api/v1/quotes/{folio}/locations` → `ETag: {version}`
- `PATCH /api/v1/quotes/{folio}/locations/{indice}` → `ETag: {version}`
- El valor del ETag es el número de versión entero de la Cotizacion

## Validación de versión en use case
`EditarUbicacionPuntualUseCase` compara `cotizacion.getVersion() != command.versionEsperada()`.  
El controlador parsea el header `If-Match` como entero (elimina comillas si las tiene).
