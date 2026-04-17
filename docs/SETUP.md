# Guía de Configuración Local

## Índice

1. [Prerrequisitos](#1-prerrequisitos)
2. [Arranque rápido con Docker Compose](#2-arranque-rápido-con-docker-compose)
3. [Arranque en modo desarrollo](#3-arranque-en-modo-desarrollo)
4. [Variables de entorno](#4-variables-de-entorno)
5. [Ejecución de pruebas](#5-ejecución-de-pruebas)

---

## 1. Prerrequisitos

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| Java | 21 | `java -version` |
| Maven | 3.9 | `./mvnw -v` |
| Node.js | 20 LTS | `node -v` |
| pnpm | 9 | `pnpm -v` |
| Docker | 24+ | `docker -v` |
| Docker Compose | 2.x | `docker compose version` |

---

## 2. Arranque rápido con Docker Compose

```bash
# Clonar el repositorio
git clone <url-repo>
cd reto-ia-center

# Levantar todo el stack
docker compose up -d

# Verificar que todos los servicios están saludables
docker compose ps

# Ver logs
docker compose logs -f api

# Abrir la aplicación
open http://localhost:3000
```

---

## 3. Arranque en modo desarrollo

```bash
# 1. Instalar dependencias del workspace
pnpm install

# 2. Levantar solo Postgres y core-stub
docker compose up -d postgres core-stub

# 3. Backend (nueva terminal)
cd apps/api
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 4. Frontend (nueva terminal)
cd apps/web
pnpm dev

# 5. Verificar endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:4000/health
```

---

## 4. Variables de entorno

### Backend (`apps/api`)

| Variable | Default | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/cotizador` | URL de conexión a PostgreSQL |
| `DB_USER` | `cotizador` | Usuario de base de datos |
| `DB_PASS` | `cotizador` | Contraseña de base de datos |
| `CORE_STUB_URL` | `http://localhost:4000` | URL del core stub |
| `SERVER_PORT` | `8080` | Puerto del servidor |

### Frontend (`apps/web`)

Copiar `.env.example` a `.env.local`:
```bash
cp apps/web/.env.example apps/web/.env.local
```

---

## 5. Ejecución de pruebas

```bash
# Unitarias backend (con cobertura)
cd apps/api && ./mvnw test jacoco:report

# Unitarias frontend (con cobertura)
pnpm --filter @reto/web test:coverage

# Integración Karate (backend debe estar corriendo)
cd tests/integration && mvn test

# E2E Playwright (todo el stack debe estar corriendo)
pnpm --filter @reto/e2e test

# Performance k6 (todo el stack + folio con datos)
k6 run tests/performance/calculate_load.js
```
