# HU-008 — Consulta de estado y progreso del folio

## Artefactos creados

### Nuevos
| Archivo | Tipo |
|---|---|
| `domain/model/AlertaProgreso.java` | Value Object |
| `domain/model/ProgresoCotizacion.java` | Value Object |
| `application/usecase/ConsultarEstadoFolioCommand.java` | Command |
| `application/usecase/EstadoFolioResult.java` | Result record |
| `application/usecase/ConsultarEstadoFolioUseCase.java` | Use Case (`@Service`) |
| `interfaces/rest/dto/EstadoFolioResponse.java` | DTO (con `AlertaData` anidado) |
| `test/.../domain/model/FolioProgresoTest.java` | Test unitario dominio |
| `test/.../usecase/ConsultarEstadoFolioUseCaseTest.java` | Test unitario aplicación |
| `test/resources/karate/HU008_estado_folio.feature` | Karate E2E (7 escenarios) |

### Modificados
| Archivo | Cambio |
|---|---|
| `domain/model/Folio.java` | Método `calcularProgreso()` + imports `LinkedHashMap`, `ArrayList` |
| `interfaces/rest/FolioController.java` | `GET /{folio}/estado`, inyección `ConsultarEstadoFolioUseCase`, `toEstadoFolioResponse()` |

## Decisiones de diseño

### Lógica de progreso en el agregado Folio
`calcularProgreso()` vive en `Folio` porque depende únicamente del estado interno del agregado
(datosGenerales, layoutUbicaciones). Es lógica de dominio pura — no requiere servicios externos.
El use case solo orquesta: busca el folio y delega el cálculo.

### EstadoFolioResult como record contenedor
El use case retorna `EstadoFolioResult(folio, progreso)` en lugar de calcular en el controller.
El controller queda delgado: mapea el result a DTO sin lógica de negocio.

## Extensibilidad: añadir nueva sección en el futuro

Para agregar una sección (p.ej. `datosUbicacion`):
1. Agregar el campo al agregado `Folio`
2. Agregar `secciones.put("datosUbicacion", datosUbicacion != null)` en `calcularProgreso()`
3. Agregar alerta correspondiente en el mismo método
4. Agregar columna/converter en infraestructura si persiste en BD
5. Los tests de dominio y E2E cubren automáticamente el nuevo porcentaje

No se requieren cambios en el controller ni en el use case de estado.

## Estado DoD

- [x] CA-01 GET folio recién creado → 0% progreso — TC-008-a
- [x] CA-02 GET con datos generales → sección marcada completa — TC-008-b
- [x] CA-03 GET con layout → sección marcada completa — TC-008-d
- [x] CA-04 GET todas las secciones → esCalculable true — TC-008-c
- [x] CA-05 GET folio inexistente → 404 — TC-008-e
- [x] CA-06 porcentajeProgreso calculado correctamente — TC-008-f + tests unitarios
- [x] CA-07 alertas lista secciones pendientes — cubierto en todos los escenarios
- [x] Solo lectura — ningún `save` en el use case
- [x] Dominio sin imports Spring/JPA/Jackson
- [x] No rompe HU-001, HU-002, HU-003
