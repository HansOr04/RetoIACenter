# HU-001 — Reporte de Implementación Backend

**Feature:** Crear folio con idempotencia  
**Fecha:** 2026-04-18  
**Estado:** IMPLEMENTADO

## Archivos creados

### Dominio
| Archivo | Propósito |
|---|---|
| `domain/model/EstadoCotizacion.java` | Enum BORRADOR/CALCULADO/EMITIDO/CANCELADO |
| `domain/model/Folio.java` | Agregado raíz — @Builder @Getter, sin anotaciones de framework |
| `domain/exception/CoreServiceUnavailableException.java` | Excepción de infraestructura elevada al dominio |
| `domain/exception/FolioNotFoundException.java` | Excepción para 404 |
| `domain/port/FolioRepository.java` | Puerto de persistencia |
| `domain/port/CoreFolioService.java` | Puerto del servicio externo de folios |

### Aplicación
| Archivo | Propósito |
|---|---|
| `application/usecase/CrearFolioCommand.java` | Input del caso de uso (record) |
| `application/usecase/CrearFolioResult.java` | Output del caso de uso — incluye `creado: boolean` para 201 vs 200 |
| `application/usecase/CrearFolioUseCase.java` | Lógica: idempotency-check → generarNumeroFolio → save |

### Infraestructura
| Archivo | Propósito |
|---|---|
| `infrastructure/persistence/FolioJpaEntity.java` | Entidad JPA — status como String |
| `infrastructure/persistence/FolioJpaRepository.java` | Spring Data JPA |
| `infrastructure/persistence/FolioJpaAdapter.java` | Adaptador dominio ↔ JPA |
| `infrastructure/http/CoreFolioClient.java` | @CircuitBreaker(core-stub) — genera F{año}-{seq} |
| `infrastructure/config/AppConfig.java` | Bean RestClient con base-url del core-stub |

### Interfaces REST
| Archivo | Propósito |
|---|---|
| `interfaces/rest/dto/CrearFolioRequest.java` | DTO request (record) |
| `interfaces/rest/dto/FolioResponse.java` | DTO response (record) |
| `interfaces/rest/FolioController.java` | POST /api/v1/folios — 201/200 según `creado` |
| `interfaces/rest/GlobalExceptionHandler.java` | @RestControllerAdvice — ProblemDetail 404/503/400 |

### Configuración
| Archivo | Cambio |
|---|---|
| `application.yml` | +Resilience4j circuit-breaker config para core-stub |
| `pom.xml` | +spring-boot-starter-aop, +karate-junit5 1.4.1 |
| `db/migration/V3__add_idempotency_and_layout.sql` | +idempotency_key, +tipo_negocio, +layout_ubicaciones, +datos_generales |

### Tests
| Archivo | Tipo | Casos |
|---|---|---|
| `CrearFolioUseCaseTest.java` | Unitario (Mockito) | 3 casos: nuevo, idempotente, core unavailable |
| `HU001_crear_folio.feature` | Integración (Karate) | 6 escenarios: CA-01 a CA-06 |
| `KarateRunnerIT.java` | Runner Karate IT | — |

## Decisiones técnicas

| Decisión | Justificación |
|---|---|
| Número de folio generado en backend (`F{año}-{seq}`) | El core-stub no expone endpoint de generación — solo health check. Se confirma disponibilidad del core y se genera localmente. |
| `status` como String en JPA | Evita problemas de mapeo con enums PostgreSQL nativos en Hibernate sin configuración extra. |
| `CrearFolioResult(Folio, boolean creado)` | Permite distinguir HTTP 201 (nuevo) de 200 (idempotente) sin inspeccionar el folio devuelto. |
| `@CircuitBreaker(name = "core-stub")` | Protege contra indisponibilidad del core-stub; fallback lanza `CoreServiceUnavailableException` → 503. |
| Body del POST optional | La spec no requiere campos obligatorios en HU-001; `tipoNegocio` y `codigoAgente` son opcionales. |

## Criterios de aceptación cubiertos

| CA | Estado |
|---|---|
| CA-01: POST nuevo → 201 + BORRADOR | ✅ |
| CA-02: POST mismo key → 200 + mismo folio | ✅ |
| CA-03: Sin body → 201 | ✅ |
| CA-04: Sin header → 400 ProblemDetail | ✅ |
| CA-05: codigoAgente null → 201 | ✅ |
| CA-06: Core caído → 503 ProblemDetail | ✅ (circuit breaker fallback) |
