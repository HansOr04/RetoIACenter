# Restricciones del Stack Tecnológico

> Versiones mínimas obligatorias y librerías prohibidas para este proyecto.

## 1. Versiones mínimas obligatorias

| Tecnología | Versión mínima | Motivo |
|---|---|---|
| Java | 21 | Records, sealed classes, virtual threads (Loom) |
| Spring Boot | 3.2 | Auto-configuración de Testcontainers, ProblemDetail nativo |
| PostgreSQL | 16 | JSONB mejorado, pg_walinspector |
| Node.js | 20 LTS | ESM estable, test runner nativo |
| Next.js | 15 | App Router estable, Server Components |
| TypeScript | 5.x | Satisfies operator, const type parameters |
| pnpm | 9.x | Workspaces v2, hoisting configurable |

## 2. Librerías prohibidas

| Librería | Motivo del bloqueo |
|---|---|
| `commons-lang` < 3 | API deprecated, CVEs. Usar `commons-lang3`. |
| `jackson-databind` < 2.15 | CVEs críticos (CVE-2022-42003, etc.). |
| `moment.js` | Bundle de 67 KB, deprecated. Usar `date-fns` ≥3 o `dayjs`. |
| `enzyme` | Sin mantenimiento para React 18+. Usar RTL. |
| `javax.*` | Reemplazado por `jakarta.*` desde Spring Boot 3. |
| `log4j` < 2.17.1 | Log4Shell (CVE-2021-44228). |

## 3. Patrones anti-recomendados

| Anti-patrón | Descripción | Alternativa |
|---|---|---|
| Lógica de negocio en controllers | El controller orquesta, no calcula | Mover a use case o entidad de dominio |
| Repositorios en domain | La implementación JPA contamina el dominio | Puertos en domain, implementación en infrastructure |
| `@Transactional` en controllers | Acoplamiento innecesario con la capa de acceso a datos | `@Transactional` solo en use cases o adapters de persistencia |
| `any` en TypeScript | Pierde type safety | Definir interfaces o usar `unknown` con narrowing |
| Fetch directo en componentes React | Lógica de red mezclada con UI | Centralizar en `services/api/` con TanStack Query |
| Estado global para todo | Zustand solo para estado compartido entre rutas | Estado local con `useState` para estado de formularios |

## 4. Herramientas de calidad obligatorias

- **Backend**: Checkstyle + SpotBugs en pipeline CI
- **Frontend**: ESLint (next/core-web-vitals) + Prettier
- **Cobertura**: JaCoCo ≥80% en backend, Jest ≥80% en frontend
