# FRONTEND-report — Cotizador de Seguros de Daños

Fecha: 2026-04-19

---

## Páginas / Rutas creadas

| Ruta | Archivo | Descripción |
|------|---------|-------------|
| `/` | `src/app/page.tsx` | Landing: crear folio nuevo o continuar por número |
| `/cotizador/[folio]/estado` | `src/app/cotizador/[folio]/estado/page.tsx` | Estado del folio: progreso, calculabilidad, alertas |
| `/cotizador/[folio]/datos-generales` | `src/app/cotizador/[folio]/datos-generales/page.tsx` | Formulario datos del tomador y bien asegurado |
| `/cotizador/[folio]/layout` | `src/app/cotizador/[folio]/layout/page.tsx` | Selector de número de ubicaciones y secciones |
| `/cotizador/[folio]/ubicaciones` | `src/app/cotizador/[folio]/ubicaciones/page.tsx` | Formulario de dirección y valores por ubicación |
| `/cotizador/[folio]/coberturas` | `src/app/cotizador/[folio]/coberturas/page.tsx` | Selección de coberturas y factor comercial |
| `/cotizador/[folio]/calculo` | `src/app/cotizador/[folio]/calculo/page.tsx` | Ejecución del cálculo y desglose de prima por ubicación |

Layout compartido: `src/app/cotizador/[folio]/layout.tsx` (header con navegación de retorno).

---

## Componentes creados

| Componente | Archivo | Responsabilidad |
|------------|---------|-----------------|
| `StatusBadge` | `src/components/StatusBadge.tsx` | Badge de estado con color semántico (BORRADOR / CALCULADO / EMITIDO / CANCELADO) |
| `StepIndicator` | `src/components/StepIndicator.tsx` | Indicador de progreso de 6 pasos con estado done / active / pending |
| `FolioHeader` | `src/components/FolioHeader.tsx` | Cabecera de folio con número, estado, versión y fecha |
| `FieldError` | `src/components/FieldError.tsx` | Mensaje de error de campo de formulario |

---

## Servicios / Lib creados

| Archivo | Descripción |
|---------|-------------|
| `src/lib/api.ts` | Capa HTTP: `foliosApi` y `quotesApi` con todos los endpoints del backend |
| `src/lib/utils.ts` | Helpers: `cn`, `formatCurrency`, `formatDate`, `generateIdempotencyKey` |

---

## Decisiones de diseño

1. **Estilos inline en lugar de clases Tailwind custom**: El proyecto usa Tailwind v4 (`@import "tailwindcss"` + `@theme {}`). Las clases custom como `bg-accent`, `text-muted`, `border-border` se definieron en `@theme` pero para máxima compatibilidad con el compilador JIT de Tailwind v4, los colores de la paleta dark se aplican via `style` inline en los componentes de página. Los componentes reutilizables (badges, indicators) usan el sistema `@theme`.

2. **Sin reemplazo de package.json**: El `package.json` existente usa Next.js 15, React 19, y las dependencias del monorepo (Radix UI, Zustand, React Query). No se reemplazó porque ya es funcional y más completo que el propuesto.

3. **Dockerfile existente preservado**: El `apps/web/Dockerfile` existente usa `corepack enable pnpm` (correcto para monorepo pnpm) y es equivalente al propuesto. No se reemplazó.

4. **next.config.ts preservado**: El archivo existente ya expone `NEXT_PUBLIC_API_URL` y añade `NEXT_PUBLIC_CORE_URL`. No se reemplazó.

5. **tsconfig.json preservado**: El existente es equivalente al propuesto con `target: ES2017` en lugar de `es5`, lo cual es preferible para Next.js moderno.

6. **Providers preservado**: `src/app/providers.tsx` contiene `QueryClientProvider` de React Query ya configurado. El nuevo `layout.tsx` lo envuelve correctamente.

7. **Tipado estricto**: Se usó `unknown` con narrowing explícito en lugar de `any` en todos los catch blocks, siguiendo el lineamiento `LIN-DEV-001` (prohibido `any`). Props marcadas como `Readonly<>` para cumplir con la regla `S6759`.

8. **Claves de lista semánticas**: Se usó el texto del label como key en lugar de índice de array (`key={s}` en lugar de `key={i}`) para cumplir la regla `S6479`.

---

## Estado de HUs frontend

| HU | Descripción | Estado |
|----|-------------|--------|
| HU-F01 | Crear folio nuevo y continuar folio existente | Implementado |
| HU-F02 | Ver estado del folio con progreso y alertas | Implementado |
| HU-F03 | Ingresar datos generales del tomador y bien | Implementado |
| HU-F04 | Configurar layout (número de ubicaciones y secciones) | Implementado |
| HU-F05 | Ingresar datos de cada ubicación (dirección + valores) | Implementado |
| HU-F06 | Seleccionar coberturas y factor comercial | Implementado |
| HU-F07 | Ejecutar cálculo y ver desglose de prima por ubicación | Implementado |

---

## Variables de entorno del API (application.yml)

| Variable | Valor por defecto | Uso |
|----------|-------------------|-----|
| `DB_URL` | `jdbc:postgresql://localhost:5432/cotizador` | URL de base de datos PostgreSQL |
| `DB_USER` | `cotizador` | Usuario de BD |
| `DB_PASS` | `cotizador` | Contraseña de BD |
| `CORE_STUB_URL` | `http://localhost:4000` | URL del servicio core-stub |
| `CORE_TIMEOUT_MS` | `3000` | Timeout de llamadas al core (ms) |
| `SERVER_PORT` | `8080` | Puerto del servidor Spring Boot |

**Nota**: Las variables son `DB_URL`, `DB_USER`, `DB_PASS` — no `SPRING_DATASOURCE_URL`. Verificar que el `docker-compose.yml` las pase con estos nombres exactos.

---

## Estado de Dockerfiles

| Servicio | Archivo | Estado |
|----------|---------|--------|
| `apps/api` | `apps/api/Dockerfile` | Existe — `eclipse-temurin:21-jdk-alpine` builder + `eclipse-temurin:21-jre-alpine` runner |
| `apps/core-stub` | `apps/core-stub/Dockerfile` | Existe — `node:20-alpine` con pnpm, expone puerto 4000 |
| `apps/web` | `apps/web/Dockerfile` | Existe — `node:20-alpine` con `corepack enable pnpm`, standalone output, expone puerto 3000 |

Los tres Dockerfiles están presentes y son funcionales.

---

## Discrepancias y observaciones

1. **Tailwind v4 vs v3**: El spec pedía `tailwind.config.js` con sintaxis v3. El proyecto ya usa Tailwind v4 con `tailwind.config.ts` y `@import "tailwindcss"` en CSS. Se adaptó el `globals.css` para usar `@theme {}` de Tailwind v4 en lugar de `@tailwind base/components/utilities`.

2. **Next.js 15 vs 14**: El spec pedía Next.js 14.2.3. El proyecto ya tiene 15.3.0 que incluye mejoras de rendimiento. Se mantuvo la versión más reciente.

3. **pnpm-workspace.yaml**: Ya existe e incluye `apps/web` y `apps/core-stub`. No requirió creación.

4. **`apps/web/next.config.ts` vs `.js`**: El archivo existente usa TypeScript (`.ts`) con `export default`. Se preservó en lugar de crear el `.js` propuesto para mantener consistencia con el resto del proyecto TypeScript.

5. **Directorio `scripts/`**: No existía — fue creado junto con `verify-docker.sh`.
