# Estándares de Arquitectura

> Referencia para los agentes Backend y QA al analizar y generar código.

## 1. Clean Architecture — Enforcement

La arquitectura del backend sigue las 4 capas de Clean Architecture. El incumplimiento de las reglas de dependencia es un **bloqueante** de revisión.

```
┌──────────────────────────────────────────────┐
│  interfaces/rest  (controllers, DTOs, mappers)│
├──────────────────────────────────────────────┤
│  application/usecase  (orquestación)          │
├──────────────────────────────────────────────┤
│  domain/  (model, port, exception)            │
├──────────────────────────────────────────────┤
│  infrastructure/  (persistence, http, config) │
└──────────────────────────────────────────────┘
```

**Flujo de dependencias permitido**:
- `interfaces` → `application` → `domain`
- `infrastructure` → `domain` (implementa puertos)
- `infrastructure` → `application` (nunca al revés)

## 2. Patrón Repository

- Los puertos (interfaces) viven en `domain/port/`.
- Las implementaciones viven en `infrastructure/persistence/`.
- Los casos de uso solo conocen el puerto, nunca la implementación JPA.

```java
// domain/port/FolioRepository.java  ← el dominio define el contrato
public interface FolioRepository {
    Folio save(Folio folio);
    Optional<Folio> findById(UUID id);
}

// infrastructure/persistence/FolioRepositoryAdapter.java  ← la infra implementa
```

## 3. Use Cases como orquestadores

- Los use cases (`application/usecase/`) son la única capa que coordina múltiples puertos.
- No contienen lógica de negocio — esta vive en las entidades de dominio.
- Reciben y devuelven DTOs de aplicación (no entidades de dominio) hacia la capa `interfaces`.

## 4. DTOs solo en `interfaces/`

- `interfaces/rest/` contiene los `Request` y `Response` DTOs usados por los controllers.
- Los `mappers` (MapStruct) viven en `interfaces/rest/` o en un sub-paquete `interfaces/mapper/`.
- El dominio nunca tiene anotaciones Jackson (`@JsonProperty`, etc.).

## 5. Dominio libre de framework

Las clases en `domain/model/` y `domain/port/`:
- No importan nada de `org.springframework.*`
- No importan nada de `jakarta.persistence.*`
- No tienen anotaciones Lombok de persistencia (`@Entity`, `@Table`, etc.)
- Pueden usar Lombok de conveniencia (`@Value`, `@Builder`, `@Getter`)

## 6. Diagrama de contenedores (C4 nivel 2)

```
[Browser]
   │ HTTPS
   ▼
[Next.js Web App :3000]
   │ HTTP/REST
   ▼
[Spring Boot API :8080]
   │                  │
   ▼                  ▼
[PostgreSQL :5432] [Core Stub :4000]
```
