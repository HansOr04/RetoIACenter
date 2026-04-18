# HU-002 — Captura y consulta de datos generales: Reporte de implementación

## Qué se implementó

### Dominio
- **`DatosGenerales`** (`domain/model/DatosGenerales.java`): value object como Java record con 9 campos. Inmutable, sin anotaciones de framework. Igualdad estructural aportada por el compilador del record.
- **`Folio`** actualizado: campo `datosGenerales: DatosGenerales` (nullable) y método de dominio `actualizarDatosGenerales(DatosGenerales)` que retorna un nuevo `Folio` con `version+1` o el mismo folio si los datos son idénticos.
- **`FolioNotFoundException`**: mensaje actualizado a `"Folio no encontrado: {numeroFolio}"` para alinearlo con el ProblemDetail del spec.

### Aplicación
- **`ActualizarDatosGeneralesCommand`** y **`ConsultarDatosGeneralesCommand`**: records CQRS ligeros.
- **`ActualizarDatosGeneralesUseCase`**: busca folio → delega lógica de versión al agregado → persiste → retorna folio actualizado.
- **`ConsultarDatosGeneralesUseCase`**: busca folio → retorna (con `datosGenerales` potencialmente null).

### Infraestructura
- **`DatosGeneralesConverter`** (`infrastructure/persistence/converter/`): `AttributeConverter<DatosGenerales, String>` con `ObjectMapper` estático (`findAndRegisterModules()`). Convierte `null` → `"{}"` para satisfacer el `NOT NULL` de la columna; convierte `"{}"` → `null` al leer (semántica de "sin datos").
- **`FolioJpaEntity`**: campo `datosGenerales` con `@Convert(converter = DatosGeneralesConverter.class)` y `columnDefinition = "jsonb"`.
- **`FolioJpaAdapter`**: mapeo entity↔domain extendido con `datosGenerales` en ambas direcciones.

### REST
- **`DatosGeneralesRequest`**: record con `@NotBlank` en campos requeridos (Jakarta Validation).
- **`DatosGeneralesData`** y **`DatosGeneralesResponse`**: records de respuesta.
- **`FolioController`**: endpoints `PUT /{numeroFolio}/datos-generales` (200 OK) y `GET /{numeroFolio}/datos-generales` (200 OK, `datosGenerales` puede ser null).
- **`GlobalExceptionHandler`**: handler añadido para `MethodArgumentNotValidException` → 400 con ProblemDetail y detalle de campos fallidos.

### Tests
- **`ActualizarDatosGeneralesUseCaseTest`**: 3 casos (version+1 con datos nuevos, sin incremento con datos idénticos, excepción con folio inexistente).
- **`ConsultarDatosGeneralesUseCaseTest`**: 3 casos (folio con datos, folio sin datos, folio inexistente).
- **`HU002_datos_generales.feature`**: 8 escenarios Karate cubriendo todos los CAs (TC-002-a..h).

---

## Decisión técnica: idempotencia del PUT

La comparación se realiza en el **agregado de dominio** mediante `Objects.equals(datos, this.datosGenerales)`.

`DatosGenerales` es un Java record; el compilador genera `equals()` comparando todos los campos estructuralmente. Si el cliente envía exactamente los mismos valores, `actualizarDatosGenerales` retorna `this` (mismo objeto, misma versión). El use case persiste el folio resultante, pero como la versión no cambió, el response refleja el mismo número de versión (CA-07, CA-08).

Alternativas descartadas:
- **Hash SHA-256 del JSON**: mayor complejidad, dependencia de serialización en dominio — prohibida por restricciones del proyecto.
- **Flag `dirty` en entidad JPA**: lógica fuera del dominio.

---

## Serialización JSONB con DatosGeneralesConverter

```
PUT body → DatosGeneralesRequest (Jakarta Validation)
         → DatosGenerales record (dominio, en controller)
         → ObjectMapper.writeValueAsString() → String JSON
         → PostgreSQL columna jsonb (datos_generales)

GET      ← String JSON ← PostgreSQL
         ← ObjectMapper.readValue(String, DatosGenerales.class)
         ← Folio.datosGenerales
         → DatosGeneralesData → DatosGeneralesResponse
```

El `ObjectMapper` estático usa `findAndRegisterModules()` que incluye `ParameterNamesModule` (disponible en classpath de Spring Boot). Esto permite deserializar Java records usando el constructor canónico con los nombres de parámetros del bytecode (`-parameters` habilitado por `spring-boot-starter-parent`).

Valor nulo en dominio se almacena como `"{}"` para respetar `NOT NULL DEFAULT '{}'` de la migración V3. Al leer `"{}"`, el converter devuelve `null` (CA-03: GET sin datos → `datosGenerales: null`).

---

## DoD — Checklist

| Criterio | Estado |
|----------|--------|
| CA-01: PUT exitoso → 200 + version+1 | ✅ |
| CA-02: GET tras PUT → datos persistidos | ✅ |
| CA-03: GET sin datos → 200 + datosGenerales null | ✅ |
| CA-04: PUT folio inexistente → 404 ProblemDetail | ✅ |
| CA-05: GET folio inexistente → 404 ProblemDetail | ✅ |
| CA-06: PUT body inválido → 400 ProblemDetail | ✅ |
| CA-07: PUT idempotente → version no incrementa | ✅ |
| CA-08: version en response = valor persistido | ✅ |
| Dominio sin imports de Spring/JPA/Jackson | ✅ |
| Lógica version+1 en agregado, no en use case | ✅ |
| Logs estructurados en use cases y controller | ✅ |
| Tests HU-001 no rotos (Folio.builder() compatible) | ✅ |
| Tests unitarios use cases (6 casos) | ✅ |
| Tests Karate (8 escenarios) | ✅ |
