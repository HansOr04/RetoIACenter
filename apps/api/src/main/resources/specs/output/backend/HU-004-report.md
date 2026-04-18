# HU-004 — CRUD de Ubicaciones

## Estado: Implementado

## Archivos creados

### Dominio
- `domain/model/ubicacion/Giro.java` — record con codigo, descripcion, claveIncendio
- `domain/model/ubicacion/ZonaCatastrofica.java` — record con zonaTev, zonaFhm
- `domain/model/ubicacion/EstadoValidacionUbicacion.java` — enum VALIDO/INCOMPLETO/INVALIDO
- `domain/model/ubicacion/AlertaBloqueante.java` — record con codigo, mensaje, campoAfectado
- `domain/model/ubicacion/CodigoAlertaBloqueante.java` — enum con 6 códigos de alerta
- `domain/model/ubicacion/Ubicacion.java` — agregado con builder inmutable, `esCalculable()`
- `domain/model/Cotizacion.java` — agregado raíz: `agregarUbicacion`, `editarUbicacion`, `capacidadExcedida`
- `domain/service/ValidadorUbicacion.java` — servicio de dominio puro, `validar(Ubicacion)`
- `domain/port/CotizacionRepository.java` — puerto de persistencia
- `domain/port/ValidadorCodigoPostalService.java` — puerto para validación de CP

### Infraestructura
- `infrastructure/persistence/DatosCotizacion.java` — POJO JSONB
- `infrastructure/persistence/UbicacionJson.java` — POJO de serialización con clases anidadas
- `infrastructure/persistence/converter/CotizacionDatosConverter.java` — AttributeConverter
- `infrastructure/persistence/CotizacionJpaEntity.java` — entidad JPA tabla cotizaciones
- `infrastructure/persistence/CotizacionJpaRepository.java` — JpaRepository
- `infrastructure/persistence/CotizacionJpaAdapter.java` — adaptador puerto ↔ JPA
- `infrastructure/http/CoreZipCodeClient.java` — client REST con circuit breaker, fallback → Optional.empty()
- `db/migration/V4__create_cotizaciones.sql` — migración tabla cotizaciones + GIN index

### Aplicación
- `application/usecase/RegistrarUbicacionUseCase.java` — flujo 10 pasos con validación de layout y CP
- `application/usecase/RegistrarUbicacionCommand.java` — command record con GiroCommand anidado
- `application/usecase/ListarUbicacionesUseCase.java` — devuelve lista de Ubicacion del cotización
- `application/usecase/ObtenerResumenUbicacionesUseCase.java` — calcula métricas VALIDO/INCOMPLETO/calculable

### Interfaces
- `interfaces/rest/dto/UbicacionRequest.java`, `GiroRequest.java`, `UbicacionResponse.java`
- `interfaces/rest/dto/ZonaCatastroficaData.java`, `GiroData.java`, `ResumenUbicacionesResponse.java`
- `interfaces/rest/LocationsController.java` — PUT, GET, GET /summary en `/api/v1/quotes/{folio}/locations`

## Endpoints
| Método | Path | Descripción |
|--------|------|-------------|
| PUT | `/api/v1/quotes/{folio}/locations` | Registrar ubicación |
| GET | `/api/v1/quotes/{folio}/locations` | Listar todas las ubicaciones |
| GET | `/api/v1/quotes/{folio}/locations/summary` | Resumen con métricas de validación |

## Decisiones de diseño
- Cotización creada lazy (primera vez que se registra ubicación)
- Índice de ubicación = posición en lista (0-based, inmutable)
- ZonaCatastrófica null cuando CP no encontrado; genera alerta CODIGO_POSTAL_INVALIDO
- Circuit breaker `core-stub` en CoreZipCodeClient con fallback → Optional.empty()
