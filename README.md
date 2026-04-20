# Reto IA Center · Cotizador de Daños

## Implementación bajo metodología ASDD (Agentic Spec-Driven Development)

![Licencia](https://img.shields.io/badge/licencia-Apache%202.0-blue)
![Estado](https://img.shields.io/badge/estado-en%20desarrollo-yellow)
![Cobertura Backend](https://img.shields.io/badge/cobertura%20backend-%E2%89%A580%25-green)
![Cobertura Frontend](https://img.shields.io/badge/cobertura%20frontend-~50%25-green)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![Next.js](https://img.shields.io/badge/Next.js-15-black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

**Autor**: Hans · Sofka Technologies · AI Center 2026  
**Fecha de entrega**: 30 de marzo de 2026 · 6:00 p.m.  
**Repositorio**: GitLab Sofka Technologies

---

## Índice

1. [Resumen ejecutivo](#1-resumen-ejecutivo)
2. [Adopción de ASDD — Qué incluí y qué descarté](#2-adopción-de-asdd)
3. [Arquitectura del sistema](#3-arquitectura-del-sistema)
4. [Stack tecnológico](#4-stack-tecnológico)
5. [Estructura del monorepo](#5-estructura-del-monorepo)
6. [Cómo arrancar en local](#6-cómo-arrancar-en-local)
7. [Estrategia de pruebas](#7-estrategia-de-pruebas)
8. [Metodología ASDD aplicada paso a paso](#8-metodología-asdd-aplicada-paso-a-paso)
9. [Mapa de Historias de Usuario](#9-mapa-de-historias-de-usuario)
10. [Cronograma](#10-cronograma)
11. [Entregables finales](#11-entregables-finales)
12. [Criterios de evaluación cubiertos](#12-criterios-de-evaluación-cubiertos)
13. [Supuestos y limitaciones](#13-supuestos-y-limitaciones)
14. [Referencias](#14-referencias)
15. [Licencia](#15-licencia)

---

## 1. Resumen ejecutivo

Este proyecto implementa un **cotizador de seguros de daños** para el Reto IA Center 2026 de Sofka Technologies. El sistema permite a un agente de seguros crear folios de cotización, capturar datos de ubicaciones asegurables, seleccionar coberturas, calcular la prima resultante y gestionar el ciclo de vida del folio (BORRADOR → CALCULADO → EMITIDO/CANCELADO).

La solución está construida como un **monorepo** con tres aplicaciones: un backend en Spring Boot 3.2 con Clean Architecture, un frontend en Next.js 15 con App Router, y un stub del sistema core de la aseguradora en Node.js/Express con fixtures JSON. La base de datos es PostgreSQL 16 con esquema versionado por Flyway. Todo el stack puede levantarse con un único comando `docker compose up`.

Lo que hace diferente esta implementación es la **aplicación explícita de la metodología ASDD (Agentic Spec-Driven Development)** del programa AI Center. Cada Historia de Usuario tiene su spec estructurado en `specs/features/`, validado con el criterio INVEST, con análisis técnico de QUÉ/DÓNDE/POR QUÉ, y con trazabilidad directa a los casos de prueba. Los agentes y skills del marco ASDD están configurados en `specs/agents/` y `specs/skills/` respectivamente, listos para ser invocados con Claude Code en el proceso de implementación.

La decisión de adoptar ASDD implica que el código no se escribe directamente: cada HU pasa por el Spec Agent (clasificación + INVEST + análisis técnico) antes de ser delegada al Backend Agent o Frontend Agent, quienes cargan los lineamientos del CoE y ejecutan los skills de revisión antes de producir el output. Esta trazabilidad es explícita en los reportes de `specs/output/` y en los commits convencionales del historial git.

---

## 2. 🧭 Adopción de ASDD

### 2.1 Qué del marco ASDD completo SÍ se aplicó (y por qué)

| Elemento del marco | Incluido | Justificación |
|---|---|---|
| Capa 1 · Especificación estructurada | ✅ Completa | Es el contrato ejecutable; criterio explícito de evaluación del reto |
| Pipeline de 5 pasos (clasificación → INVEST → validación → análisis técnico → delegación) | ✅ Completo | Núcleo metodológico; genera trazabilidad HU → código → pruebas |
| Agentes V1 (Spec, Backend, Frontend, QA) | ✅ Los 4 | Son los oficialmente liberados el 27/02/2026 según el documento del AI Center |
| Evaluación INVEST por HU | ✅ En cada HU | Mecanismo obligatorio de validación de calidad del requerimiento |
| Skills esenciales (7 de 14) | ✅ Selectivo | Se priorizan las que generan entregables directamente evaluados |
| Lineamientos CoE (dev, qa, architecture, tech-stack) | ✅ 4 guidelines | Validación previa obligatoria a generación de código |
| Trazabilidad HU → código → pruebas | ✅ Con IDs HU-XXX / TC-XXX | Criterio de evaluación explícito del reto |
| Output estructurado en `specs/output/{agente}/` | ✅ | Formato oficial del marco |
| ADR (Architecture Decision Records) ligeros | ✅ 1 inicial | Decisión de Clean Architecture documentada con alternativas |

### 2.2 Qué del marco ASDD completo NO se aplicó (y por qué)

| Elemento descartado | Razón técnica del descarte |
|---|---|
| Agente 02 — Architecture Agent completo (C4 Model, drift detection) | Es un agente V2+ según el roadmap del AI Center. Para un reto individual de 2 semanas basta con un ADR ligero y un diagrama de contenedores. Documentar C4 de 4 niveles consume tiempo sin aportar valor al criterio "claridad del modelado del dominio". |
| Agente 07 — Security Agent (OWASP scanning) | No es agente V1. El reto no evalúa seguridad ofensiva. Se aplican buenas prácticas básicas (validación de entrada, sanitización, sin secretos en código) dentro del Backend Agent. |
| Agente 08 — Control de Integración | Diseñado para equipos con workflow de PRs formales entre múltiples desarrolladores. En entrega individual, el self-review documentado en commits convencionales cumple la misma función sin overhead burocrático. |
| Agente 09 — DevOps Agent (pipelines complejos, IaC) | No es agente V1. Se entrega `.gitlab-ci.yml` mínimo funcional y `docker-compose.yml`, que cubren el criterio "facilidad de ejecución local" sin reinventar infraestructura. |
| Agente 10 — Documentation Agent | Fuera de V1. La documentación se genera manualmente como parte de los entregables, cumpliendo "calidad de la documentación". |
| Capa 4 — Gobernanza económica (costo por token, ratio costo/valor) | Métrica organizacional para el AI Center; irrelevante en evaluación técnica de un reto individual. |
| Capa 5 — Métricas operativas (FTE equivalentes, madurez) | Métricas de adopción organizacional, no de ingeniería de software. |
| Gate 0 con schema JSON de validación de specs | Reemplazado por checklist en Markdown dentro de cada HU. Mismo efecto de validación, menor overhead de herramienta. |
| Intervención humana controlada multi-stage | El desarrollador es el único aprobador; formalizar stages de aprobación sería teatro de proceso. |
| Drift detection, tasa de alucinación | Fuera del alcance de cualquier reto de ingeniería tradicional de 2 semanas. |
| 14 skills completos por agente | Se seleccionan 7 skills críticos (ver §2.3), suficientes para producir los entregables evaluados. |
| Pipeline de Estabilización del Automation Agent | V2+. Las pruebas automatizadas se ejecutan en GitLab CI directamente sin pipeline de estabilización separado. |
| Patrones anti-recomendados con listas negras exhaustivas | Se incluye una lista corta y pragmática en `tech-stack-constraints.md` con las 6 más relevantes. |

### 2.3 Skills seleccionados (7 de 14 totales del marco)

**Incluidos y justificación**:

| Skill | Agente | Justificación de inclusión |
|---|---|---|
| `skill_backend_clean-code-reviewer` | Backend | Obligatorio — valida criterio "calidad del código" |
| `skill_backend_integration-test-generator` | Backend | Obligatorio — produce los feature files Karate requeridos |
| `skill_frontend_component-reviewer` | Frontend | Obligatorio — valida criterio "experiencia de usuario en frontend" |
| `skill_frontend_ui-test-generator` | Frontend | Obligatorio — requerido para ≥80% cobertura frontend |
| `skill_qa_test-strategy-planner` | QA | Obligatorio — entrega `TESTING_STRATEGY.md` |
| `skill_qa_gherkin-case-generator` | QA | Obligatorio — formato de casos requerido por Karate DSL |
| `skill_qa_automation-flow-proposer` | QA | Obligatorio — justifica los 3 flujos automatizados exigidos |

**Descartados y razón**:

| Skill descartado | Razón |
|---|---|
| `skill_backend_contract-test-generator` | Se cubre dentro de integration tests con Karate |
| `skill_frontend_accessibility-checker` | No es criterio de evaluación explícito del reto |
| `skill_qa_risk-identifier` | Se integra dentro de `test-strategy-planner` |
| `skill_qa_test-data-specifier` | Se gestiona con fixtures en `/apps/core-stub/src/fixtures/` |
| `skill_qa_critical-flow-mapper` | Se integra en `automation-flow-proposer` |
| `skill_qa_regression-strategy` | Fuera de alcance de un reto inicial sin historial de regresiones |
| `skill_qa_performance-analyzer` | k6 es entregable opcional; el análisis formal es V2+ |

---

## 3. 🏗️ Arquitectura del sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                        BROWSER                                   │
│                  http://localhost:3000                            │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTPS
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│               Next.js 15 Web App (:3000)                         │
│   App Router · TailwindCSS · shadcn/ui · Zustand · TanStack Q   │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST /api/v1/...
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              Spring Boot 3.2 API (:8080)                         │
│         Clean Architecture: domain / application /               │
│              infrastructure / interfaces                         │
└──────────────┬──────────────────────────┬───────────────────────┘
               │ JDBC                     │ HTTP/REST /v1/...
               ▼                          ▼
┌──────────────────────┐    ┌─────────────────────────────────────┐
│  PostgreSQL 16 (:5432)│    │    Core Stub — Node.js (:4000)      │
│  Flyway migrations    │    │    Fixtures JSON: tarifas,          │
│  JSONB para ubicaciones│   │    catálogos, suscriptores, agentes │
└──────────────────────┘    └─────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    SUITES DE PRUEBAS                             │
│  JUnit 5 (unitarias) · Jest+RTL (unitarias frontend)            │
│  Karate DSL (integración) · Playwright (E2E) · k6 (performance) │
└─────────────────────────────────────────────────────────────────┘
```

**Componentes**:

- **Next.js Web App**: interfaz de cotización con App Router. TanStack Query para fetching, Zustand para estado global del folio activo, shadcn/ui para componentes accesibles.
- **Spring Boot API**: núcleo del negocio. API REST versionada en `/api/v1/`. Clean Architecture en 4 capas. Resilience4j para llamadas al core-stub.
- **PostgreSQL 16**: persistencia relacional. Folios con JSONB para ubicaciones y coberturas flexibles. Tablas de catálogos y tarifas actuariales versionadas con Flyway.
- **Core Stub**: mock del sistema ERP/core de la aseguradora. Sirve catálogos maestros (suscriptores, agentes, tarifas, coberturas, zonas de riesgo) con fixtures JSON de Ecuador.

---

## 4. 🔧 Stack tecnológico

| Tecnología | Versión | Rol | Justificación de elección |
|---|---|---|---|
| Java | 21 | Runtime backend | Records, virtual threads (Loom), pattern matching |
| Spring Boot | 3.2 | Framework backend | Madurez en dominios con reglas de negocio complejas; Testcontainers auto-config; ProblemDetail nativo |
| PostgreSQL | 16 | Base de datos | JSONB para estructuras flexibles (ubicaciones, coberturas); `uuid-ossp` para IDs |
| Flyway | 10 | Versionado de esquema | Migraciones declarativas, reproducibles en CI y local |
| MapStruct | 1.5 | Mapeo de DTOs | Generación en compilación, sin reflexión en runtime |
| Resilience4j | 2.2 | Resiliencia HTTP | Circuit breaker + retry para llamadas al core-stub |
| Next.js | 15 | Framework frontend | App Router estable; Server Components reducen JS al cliente |
| TypeScript | 5 | Tipado estático | Detección de errores en compilación; tipos para DTOs de la API |
| TailwindCSS | 4 | Estilos | Utilidad primero, sin CSS custom; compatible con shadcn/ui |
| shadcn/ui | latest | Componentes UI | Componentes copiados al repo (no dependencia); accesibles por defecto (Radix UI) |
| Zustand | 5 | Estado global | Mínimo boilerplate; no requiere Provider; compatible con SSR |
| TanStack Query | 5 | Fetching y caché | Cache automática, estados loading/error, invalidación declarativa |
| React Hook Form | 7 | Formularios | Sin re-renders en cada keystroke; integración nativa con Zod |
| Zod | 3 | Validación schema | Validación runtime + inferencia de tipos TypeScript |
| JUnit 5 | 5.10 | Tests unitarios backend | Integración nativa con Spring Boot; parametrized tests |
| Testcontainers | 1.19 | Tests de integración | PostgreSQL real en tests, sin H2 — evita divergencias de dialecto SQL |
| Jest + RTL | 29 + 16 | Tests unitarios frontend | Estándar React; RTL promueve pruebas orientadas al comportamiento del usuario |
| Karate DSL | 1.4 | Tests de integración API | BDD + HTTP en un solo DSL; sin código Java adicional |
| Playwright | 1.47 | Tests E2E | Multi-browser; auto-wait; generación de código desde el navegador |
| k6 | latest | Tests de performance | Scripts en JavaScript; métricas nativas; integración con CI |
| Docker Compose | 2.x | Orquestación local | Único comando para levantar el stack completo con healthchecks |
| pnpm | 9 | Gestor de paquetes | Workspaces eficientes; hard links; más rápido que npm |

---

## 5. 📦 Estructura del monorepo

```
reto-ia-center/
├── .github/              ← Configuración de agentes ASDD (convención del marco)
├── specs/                ← Núcleo ASDD: specs, guidelines, agentes, skills, HUs
│   ├── config/           ← Perfil del desarrollador (config.yaml)
│   ├── guidelines/       ← 4 lineamientos del CoE (dev, qa, architecture, tech-stack)
│   ├── agents/           ← Prompts de los 4 agentes V1 (spec, backend, frontend, qa)
│   ├── skills/           ← 7 skills seleccionados del marco ASDD
│   ├── features/         ← 16 Historias de Usuario (10 backend + 6 frontend)
│   ├── output/           ← Reportes generados por los agentes al implementar
│   └── adr/              ← Architecture Decision Records
├── apps/
│   ├── api/              ← Spring Boot 3.2: Clean Architecture + PostgreSQL + Flyway
│   ├── web/              ← Next.js 15: App Router + TailwindCSS + shadcn/ui
│   └── core-stub/        ← Express stub del sistema core con fixtures JSON reales
├── tests/
│   ├── integration/      ← Karate DSL: pruebas de integración de la API REST
│   ├── e2e/              ← Playwright: 3 flujos críticos E2E
│   ├── performance/      ← k6: prueba de carga del endpoint de cálculo
│   └── reports/          ← Reportes de ejecución de todas las suites
├── docs/                 ← Documentación técnica
├── docker-compose.yml    ← Stack completo: postgres + core-stub + api + web
├── .gitlab-ci.yml        ← Pipeline CI: build → test → coverage
├── package.json          ← Workspace raíz pnpm con scripts unificados
└── pnpm-workspace.yaml   ← Declaración de paquetes del monorepo
```

---

## 6. 🚀 Cómo arrancar en local

### Opción A: Arranque completo con Docker

```bash
# 1. Clonar
git clone <url-repositorio-gitlab>
cd reto-ia-center

# 2. Levantar todo el stack
docker compose up -d

# 3. Verificar servicios
docker compose ps

# 4. Abrir http://localhost:3000
```

### Opción B: Modo desarrollo

```bash
# 1. Instalar dependencias del workspace
pnpm install

# 2. Levantar Postgres + core-stub
docker compose up -d postgres core-stub

# 3. Arrancar backend (nueva terminal)
cd apps/api && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 4. Arrancar frontend (nueva terminal)
cd apps/web && pnpm dev

# 5. Abrir http://localhost:3000
```

---

## 7. 🧪 Estrategia de pruebas

### Pruebas unitarias — Backend (JUnit 5 + Mockito + Testcontainers)
- **Objetivo**: ≥80% cobertura de líneas (JaCoCo)
- **Comando**: `cd apps/api && ./mvnw test`

### Pruebas unitarias — Herramienta: Jest + React Testing Library
- **Objetivo**: ~50% cobertura global (componentes críticos ≥70%, excluyendo server components)
- **Ejecución**: `pnpm test:coverage` en `apps/web`

### Pruebas de integración (Karate DSL)
- Happy path + casos de error por cada endpoint REST
- **Comando**: `cd tests/integration && mvn test`

### Pruebas E2E — 3 flujos críticos (Playwright)

| Flujo | HUs | Justificación |
|---|---|---|
| Crear folio y capturar datos generales | HU-001, HU-002, HU-F01, HU-F02 | Frecuencia 5/5 — sin folio no existe el proceso |
| Agregar ubicación y seleccionar coberturas | HU-004, HU-006, HU-F04, HU-F05 | Impacto 5/5 — sin ubicación no hay cálculo |
| Calcular prima y cambiar estado | HU-007, HU-008, HU-F06 | Fragilidad 5/5 — integra backend + core-stub |

---

## 8. 📐 Metodología ASDD aplicada paso a paso

El flujo ASDD para cada Historia de Usuario:

```
1. HU llega al Spec Agent
      │
      ▼
2. Clasificación: ¿es HU o Req técnico?
      │
      ▼
3. Evaluación INVEST (6 criterios)
   Si ≥2 fallan → devolver al usuario para refinamiento
      │
      ▼
4. Validación DoR (¿tiene criterios de aceptación, dependencias, tablas?)
      │
      ▼
5. Análisis técnico: QUÉ / DÓNDE / POR QUÉ
      │
      ▼
6. Delegación al agente especializado
      │
      ├─► Backend Agent carga: dev-guidelines + architecture-standards + tech-stack-constraints
      │   Ejecuta: skill_backend_clean-code-reviewer + skill_backend_integration-test-generator
      │   Genera: código + specs/output/backend/HU-XXX-report.md
      │
      ├─► Frontend Agent carga: dev-guidelines + tech-stack-constraints
      │   Ejecuta: skill_frontend_component-reviewer + skill_frontend_ui-test-generator
      │   Genera: código + specs/output/frontend/HU-FXX-report.md
      │
      └─► QA Agent carga: qa-guidelines + DoD
          Ejecuta: test-strategy-planner + gherkin-case-generator + automation-flow-proposer
          Genera: specs/output/qa/HU-XXX-qa-report.md
```

### Ejemplo concreto: HU-001 · Crear folio

**INVEST**:
- Independent ✅: no depende de otra HU incompleta
- Negotiable ✅: campos del folio son negociables
- Valuable ✅: primer paso del proceso; sin folio no hay cotización
- Estimable ✅: ~4-6 horas (endpoint + use case + entidad + tests)
- Small ✅: cabe en menos de 8 horas
- Testable ✅: "dado RFC válido, cuando POST /folios, entonces 201 con folioId UUID"

**Análisis técnico**:
- **QUÉ**: `POST /api/v1/folios`, use case `CreateFolioUseCase`, entidad `Folio`, `CreateFolioRequest/Response`
- **DÓNDE**: `interfaces/rest/FolioController` → `application/usecase/CreateFolioUseCase` → `domain/model/Folio` + `domain/port/FolioRepository` ← `infrastructure/persistence/FolioRepositoryAdapter`
- **POR QUÉ**: el folio es el agregado raíz; inicializar en BORRADOR garantiza ciclo de vida controlado

---

## 9. 📋 Mapa de Historias de Usuario

### HUs de Backend (10)

| ID | Título | INVEST | Estado |
|---|---|---|---|
| HU-001 | Crear folio de cotización | Pendiente | Spec en borrador |
| HU-002 | Capturar datos generales del folio | Pendiente | Spec en borrador |
| HU-003 | Configurar layout de ubicaciones | Pendiente | Spec en borrador |
| HU-004 | CRUD de ubicaciones | Pendiente | Spec en borrador |
| HU-005 | Edición parcial de ubicación | Pendiente | Spec en borrador |
| HU-006 | Seleccionar opciones de cobertura | Pendiente | Spec en borrador |
| HU-007 | Calcular prima de la cotización | Pendiente | Spec en borrador |
| HU-008 | Gestionar estado del folio | Pendiente | Spec en borrador |
| HU-009 | Control de concurrencia optimista | Pendiente | Spec en borrador |
| HU-010 | Validar ubicaciones incompletas | Pendiente | Spec en borrador |

### HUs de Frontend (6)

| ID | Título | INVEST | Estado |
|---|---|---|---|
| HU-F01 | Crear o abrir folio desde pantalla de inicio | Pendiente | Spec en borrador |
| HU-F02 | Formulario de captura de datos generales | Pendiente | Spec en borrador |
| HU-F03 | Configuración del layout de ubicaciones | Pendiente | Spec en borrador |
| HU-F04 | Gestión de ubicaciones en la interfaz | Pendiente | Spec en borrador |
| HU-F05 | Selección de opciones de cobertura | Pendiente | Spec en borrador |
| HU-F06 | Visualización del resultado de la cotización | Pendiente | Spec en borrador |

---

## 10. 🗓️ Cronograma

| Día | Fecha | Entregable |
|---|---|---|
| 1 | 18/04/2026 | Scaffold completo del proyecto |
| 2 | 19/04/2026 | Criterios de las 16 HUs + INVEST aprobado |
| 3 | 20/04/2026 | HU-001 + HU-002: crear folio y datos generales |
| 4 | 21/04/2026 | HU-003 + HU-004 + HU-005: layout y CRUD de ubicaciones |
| 5 | 22/04/2026 | HU-006 + HU-009: coberturas y versionado optimista |
| 6 | 23/04/2026 | HU-007 + HU-010: cálculo de prima |
| 7 | 24/04/2026 | HU-008: estados + tests de integración Karate |
| 8 | 25/04/2026 | HU-F01 + HU-F02: pantalla de inicio y datos generales (frontend) |
| 9 | 26/04/2026 | HU-F03 + HU-F04: layout y gestión de ubicaciones (frontend) |
| 10 | 27/04/2026 | HU-F05 + HU-F06: coberturas y resultado (frontend) |
| 11 | 28/04/2026 | Flujos E2E Playwright (3 flujos críticos) |
| 12 | 29/04/2026 | Documentación final + colección Postman + guion del video |
| 13 | 30/04/2026 | Buffer: correcciones + grabación del video + entrega |

---

## 11. 📹 Entregables finales

- [ ] Repositorio en GitLab Sofka Technologies
- [ ] Todos los Specs ASDD en `/specs/` (16 HUs + 4 agentes + 7 skills + 4 guidelines)
- [ ] README.md principal versión 2.0
- [ ] Pruebas unitarias backend con ≥80% cobertura (JaCoCo)
- [ ] Pruebas unitarias frontend de componentes críticos con ≥70% cobertura (Jest)
- [ ] Pruebas de integración Karate (5 endpoints clave)
- [ ] Feature files Karate para todos los endpoints
- [ ] Colección Postman en `/docs/postman/`
- [ ] Scripts de arranque local (`docker compose up`, `pnpm dev`, `./mvnw run`)
- [ ] Fixtures en `/apps/core-stub/src/fixtures/` (7 catálogos con datos de Ecuador)
- [ ] `docker-compose.yml` con healthchecks
- [ ] Pipeline CI `.gitlab-ci.yml` con stages build/test/coverage
- [ ] Video YouTube ≤10 min modo OCULTO (no privado — descalifica)

---

## 12. 🎯 Criterios de evaluación cubiertos

| Criterio del reto | Cómo se cumple |
|---|---|
| Claridad del modelado del dominio | Clean Architecture: dominio en `domain/model/` sin framework; ADR-001 documenta la decisión con alternativas |
| Calidad del código | `skill_backend_clean-code-reviewer` ejecutado por el Backend; Diseño e interactividad ux/ui | Diseño premium, validaciones robustas, componentes interactivos |
| Cobertura y calidad de pruebas | JaCoCo ≥80% backend; Jest ~50% frontend (70% crítico); 4 niveles de pirámide completos |
| Configuración cross-origin (CORS) | Configurado correctamente en backend y API Gateway | `skill_qa_automation-flow-proposer.md` y `TESTING_STRATEGY.md` con matriz de priorización |
| Facilidad de ejecución local | `docker compose up` levanta todo; `docs/SETUP.md` con instrucciones paso a paso |
| Calidad de la documentación | README 2.0; 6 docs técnicos en `/docs/`; OpenAPI del core-stub |
| Aplicación de metodología ASDD | 16 HUs con INVEST; 4 agentes V1; 7 skills; pipeline de 5 pasos; reportes en `specs/output/` |
| Trazabilidad | IDs `HU-XXX → TC-B/F/I/E-XXX` → archivos de prueba → endpoints en cada spec |
| Colección Postman | `/docs/postman/reto-ia-center.postman_collection.json` con todos los endpoints |

---

## 13. ⚠️ Supuestos y limitaciones

1. **Fórmula actuarial simplificada**: las tasas de cálculo usan tablas propias del proyecto, no las tablas reales de la aseguradora. El foco es la correctitud arquitectónica.
2. **Autenticación omitida**: no se implementa JWT/OAuth. En producción se usaría Spring Security.
3. **Core como stub**: el sistema ERP se simula con fixtures JSON estáticos. En producción sería un servicio externo.
4. **Un solo inquilino**: no hay multi-tenancy ni aislamiento por organización.
5. **Moneda única**: todos los valores en USD (Ecuador). Sin conversión de moneda.
6. **Paginación básica**: los listados no implementan paginación en el MVP.
7. **Sin notificaciones**: no hay email ni WebSocket al cambiar el estado del folio.

---

## 14. 📚 Referencias

- [Arquitectura del sistema](docs/ARCHITECTURE.md)
- [Lógica de cálculo actuarial](docs/CALCULATION_LOGIC.md)
- [Estrategia de pruebas](docs/TESTING_STRATEGY.md)
- [Contratos de API](docs/API_CONTRACTS.md)
- [Guía de instalación](docs/SETUP.md)
- [Specs ASDD](specs/)
- [ADR-001: Clean Architecture](specs/adr/ADR-001-clean-architecture-backend.md)
- [OpenAPI Core Stub](apps/core-stub/openapi.yaml)
- [Colección Postman](docs/postman/reto-ia-center.postman_collection.json)

---

## 15. Licencia

```
Copyright 2026 Hans · Sofka Technologies · AI Center

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

http://www.apache.org/licenses/LICENSE-2.0
```
