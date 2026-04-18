# HU-005 — Edición Puntual de Ubicación

## Estado: Implementado

## Archivos creados

### Aplicación
- `application/usecase/EditarUbicacionPuntualUseCase.java` — lógica de edición parcial con validación de versión e índice
- `application/usecase/EditarUbicacionCommand.java` — command con todos los campos editables (todos nullable excepto folio, indice, versionEsperada)

### Interfaces
- `interfaces/rest/dto/UbicacionPatchRequest.java` — todos los campos opcionales (nullable)
- `interfaces/rest/LocationsController.java` — endpoint PATCH agregado

## Endpoint
| Método | Path | Descripción |
|--------|------|-------------|
| PATCH | `/api/v1/quotes/{folio}/locations/{indice}` | Editar campos puntuales de una ubicación |

## Flujo de edición
1. Busca cotización por numeroFolio; lanza `FolioNotFoundException` si no existe
2. Valida versión con If-Match; lanza `VersionConflictException` si difiere
3. Valida que el índice exista; lanza `UbicacionNotFoundException` si no
4. Aplica solo campos no-null del patch sobre la ubicación existente
5. Si codigoPostal cambió, llama a `ValidadorCodigoPostalService` y actualiza ZonaCatastrofica
6. Re-valida con `ValidadorUbicacion` y actualiza alertas y estadoValidacion
7. Persiste con `Cotizacion.editarUbicacion()` que incrementa versión+1
8. Retorna ubicación actualizada con ETag en cabecera

## Invariante de inmutabilidad
El campo `indice` nunca se modifica en la edición (solo se usa para localizar la ubicación).
