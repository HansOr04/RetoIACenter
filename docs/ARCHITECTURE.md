# Arquitectura del sistema

> Documento de referencia técnica sobre cómo está construido el cotizador de daños: capas, dependencias, patrones y decisiones clave.

## 1. Visión general

El sistema se compone de tres aplicaciones independientes comunicándose por HTTP:

```
┌─────────────────┐     ┌─────────────────────┐     ┌─────────────────┐
│  cotizador-web  │────▶│ plataforma-danos-back│────▶│plataforma-core-ohs│
│   (Next.js)     │ REST│   (Spring Boot)     │ REST│  (stub Express)  │
│   :3000         │     │   :8080             │     │   :4000          │
└─────────────────┘     └──────────┬──────────┘     └─────────────────┘
                                   │
                                   ▼
                          ┌──────────────────┐
                          │  PostgreSQL 16   │
                          │   :5432          │
                          └──────────────────┘
```

Todas las aplicaciones se orquestan localmente con `docker compose`.

## 2. Arquitectura del backend (Clean Architecture)

El backend sigue Clean Architecture con 4 capas concéntricas donde las dependencias apuntan hacia el centro:

```
                    ┌──────────────────────────────┐
                    │      interfaces/rest          │  ← capa externa
                    │  Controllers · DTOs · Handlers │    (HTTP)
                    └────────────┬─────────────────┘
                                 ▼
                    ┌──────────────────────────────┐
                    │      application/usecase      │
                    │    Casos de uso orquestan     │
                    └────────────┬─────────────────┘
                                 ▼
                    ┌──────────────────────────────┐
                    │           domain              │  ← centro
                    │  model · port · service · ex  │    (lógica pura)
                    └────────────┬─────────────────┘
                                 ▲
                                 │ implementa puertos
                    ┌────────────┴─────────────────┐
                    │      infrastructure           │
                    │  persistence · http · config  │
                    └──────────────────────────────┘
```

### Responsabilidades por capa

| Capa | Clases | Responsabilidad |
|---|---|---|
| `interfaces/rest/` | Controllers, DTOs, ExceptionHandler | Recibir HTTP, mapear a commands, serializar response |
| `application/usecase/` | `CrearFolioUseCase`, `EjecutarCalculoUseCase`, etc. | Orquestar lógica de negocio, invocar puertos |
| `domain/model/` | `Cotizacion`, `Folio`, `Ubicacion` | Entidades, value objects, reglas invariantes |
| `domain/port/` | `CotizacionRepository`, `CatalogoTarifasRepository` | Interfaces que la infra implementa |
| `domain/service/` | `CalculoPrimaService`, `ValidadorUbicacion` | Lógica compleja que no cabe en una entidad |
| `infrastructure/persistence/` | JPA repositories, mappers | Implementación de puertos contra Postgres |
| `infrastructure/http/` | `CoreServiceHttpClient` | Consumo del core-stub con circuit breaker |
| `infrastructure/config/` | `DomainServicesConfig`, interceptors | Registro de beans y configuración cross-cutting |

### Reglas de dependencia

- Domain no importa nada de application, infrastructure ni interfaces
- Application no importa nada de infrastructure ni interfaces
- Infrastructure importa domain (implementa puertos)
- Interfaces importa application (invoca use cases)

Esto se verifica automáticamente ejecutando:

```bash
grep -rn "import com.sofka.cotizador.infrastructure" src/main/java/com/sofka/cotizador/domain/
# Resultado esperado: vacío
```

## 3. Persistencia

### Estrategia de modelo de datos

Dos tablas principales:

**`folios`** — identidad inmutable
- `numero_folio` (PK) generado secuencialmente por core-stub
- `idempotency_key` (UNIQUE) para deduplicación
- Metadatos de creación

**`cotizaciones`** — estado operacional
- `numero_folio` (FK → folios)
- `datos` JSONB con `datosAsegurado`, `ubicaciones[]`, `opcionesCobertura`, `primasPorUbicacion[]`
- `prima_neta`, `prima_comercial` como columnas dedicadas (para consultas analíticas futuras)
- `version` INT para optimistic locking
- `fecha_ultima_actualizacion` TIMESTAMP

### Por qué JSONB para ubicaciones y cobertura

Las ubicaciones tienen estructura variable (algunas con claveIncendio, otras con equipoElectronico, distintos sets de garantías). Modelarlas como tablas hijas requeriría 8-10 tablas adicionales y JOIN complejos para cada query. JSONB permite:

- Escritura parcial atómica con `jsonb_set(datos, '{ubicaciones,N}', nuevoValor)`
- Queries con índices GIN para filtros comunes
- Evolución del esquema sin migraciones

Trade-off: perdemos foreign keys estrictas sobre elementos del array. Aceptable para un reto de 2 semanas.

### Migraciones

Flyway gestiona 2 migraciones:
- `V1__initial_schema.sql` — DDL completo
- `V2__seed_catalogs.sql` — datos de tarifas, zonas, factores

## 4. Arquitectura del frontend

Next.js 15 con App Router. Estructura clave:

```
src/
├── app/                          # Páginas (App Router)
│   ├── page.tsx                  # Home
│   └── cotizador/[folio]/...     # Rutas dinámicas del flujo
├── components/                   # Componentes reutilizables
│   ├── ui/                       # Primitivos (Button, Input, Card)
│   ├── forms/                    # Formularios compuestos
│   └── cotizacion/               # Componentes de dominio
├── hooks/                        # Custom hooks (useFolio, useCalculation)
├── stores/                       # Zustand stores
├── services/api/                 # Cliente HTTP hacia backend
└── lib/schemas/                  # Esquemas Zod de validación
```

### Patrón de data fetching

TanStack Query maneja caché, retry, invalidación. Cada endpoint del backend tiene un hook correspondiente en `src/hooks/`:

- `useFolio(folio)` → GET /quotes/{folio}/state
- `useCreateFolio()` → POST /quotes con optimistic update
- `useUpdateGeneralInfo(folio)` → PUT con invalidación del cache

### Validación

Zod schemas en `src/lib/schemas/` + React Hook Form para validación client-side. Los mismos schemas se podrían compartir con el backend si se migrara a TypeScript, pero hoy son independientes.

## 5. Comunicación con el core-stub

El backend consume el core-stub mediante `CoreServiceHttpClient` con:

- Resilience4j circuit breaker (abre tras 50% de fallos en 10 requests)
- Timeout por request: 3 segundos
- Retry automático: 2 intentos
- Fallback local cuando el circuit breaker está abierto (respuesta cacheada o error controlado 503)

Ver `apps/api/src/main/java/com/sofka/cotizador/infrastructure/http/CoreServiceHttpClient.java`.

## 6. Manejo de errores

Formato RFC 7807 `application/problem+json` en todas las respuestas de error. Un `GlobalExceptionHandler` (clase `@RestControllerAdvice`) mapea excepciones de dominio a HTTP:

| Excepción | Código HTTP |
|---|---|
| `FolioNotFoundException` | 404 |
| `VersionConflictException` | 409 |
| `ValidationException` | 400 |
| `BusinessRuleException` | 422 |
| `PreconditionRequiredException` | 428 |
| `CoreServiceUnavailableException` | 503 |

Todos los ProblemDetail incluyen `type`, `title`, `status`, `detail`, `instance` y campos extendidos relevantes al contexto (ej. `currentVersion`, `receivedVersion` en el 409).

## 7. Modelo de versionado optimista

El sistema mantiene **dos agregados con versiones independientes**: `Folio` y `Cotizacion`.

### Diseño dual

```
Folio (tabla: folios)
  └── version: INT  →  controla actualizaciones de datos generales (asegurado, RFC, etc.)

Cotizacion (tabla: cotizaciones)
  └── version: INT  →  controla actualizaciones de ubicaciones, coberturas y resultados
```

Esto permite que dos operadores trabajen en paralelo: uno editando datos del asegurado (modifica `Folio`) y otro configurando ubicaciones (modifica `Cotizacion`) sin bloquearse mutuamente.

### Contrato de la cabecera `If-Match`

Cada `PUT` y `PATCH` requiere la cabecera `If-Match` con la versión conocida del cliente:

```http
PUT /quotes/{folio}/general-info
If-Match: 3
Content-Type: application/json

{ "nombreAsegurado": "Empresa S.A.", ... }
```

Si la versión en BD es distinta a la enviada, el backend responde:

```json
HTTP/1.1 409 Conflict
Content-Type: application/problem+json

{
  "type": "https://cotizador.sofka.com/errors/version-conflict",
  "title": "Version Conflict",
  "status": 409,
  "detail": "Expected version 3 but current is 4",
  "currentVersion": 4,
  "receivedVersion": 3
}
```

### Flujo de actualización sin conflicto

```
Cliente            Backend               BD
  │                   │                  │
  │  GET /state       │                  │
  │──────────────────▶│                  │
  │◀──────────────────│  {version: 4}    │
  │                   │                  │
  │  PUT If-Match: 4  │                  │
  │──────────────────▶│                  │
  │                   │  UPDATE ... WHERE version=4
  │                   │─────────────────▶│
  │                   │◀─────────────────│  1 row updated
  │◀──────────────────│  200 {version:5} │
```

### Implementación

Ver `apps/api/src/main/java/com/sofka/cotizador/domain/service/VersioningService.java` para la validación central y `apps/api/src/main/java/com/sofka/cotizador/infrastructure/persistence/CotizacionJpaRepository.java` para la query con `@Modifying` + `@Query` que incluye el WHERE version=:expectedVersion.

## 8. ADRs relacionados

- [ADR-001 · Clean Architecture en el backend](../specs/adr/ADR-001-clean-architecture-backend.md)

## 9. Decisiones arquitectónicas resumidas

| Decisión | Alternativa evaluada | Por qué ganó |
|---|---|---|
| Clean Architecture | Arquitectura en capas clásica | Testabilidad y separación dominio/framework |
| JSONB para ubicaciones | Tablas hijas | Velocidad de iteración en reto corto |
| Stub Express para core | Mock en Spring | Aislar el core del stack principal |
| Next.js App Router | Pages Router | Server Components mejoran rendimiento |
| TanStack Query | SWR / Redux Query | Mejor DX con invalidación |
| Resilience4j | Hystrix (deprecated) | Estándar actual en Spring |
| Versionado dual (Folio + Cotizacion) | Versión única global | Edición paralela sin bloqueo innecesario |
| `@Bean` en `DomainServicesConfig` | `@Service` en dominio | Mantiene el dominio libre de anotaciones Spring |
