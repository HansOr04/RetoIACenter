---
name: API environment variables
description: Spring Boot API env var names from application.yml — critical for docker-compose alignment
type: project
---

Backend (apps/api) env var names from application.yml:
- DB_URL (default: jdbc:postgresql://localhost:5432/cotizador)
- DB_USER (default: cotizador)
- DB_PASS (default: cotizador)
- CORE_STUB_URL (default: http://localhost:4000)
- CORE_TIMEOUT_MS (default: 3000)
- SERVER_PORT (default: 8080)

Frontend uses: NEXT_PUBLIC_API_URL (points to localhost:8080 by default).

**Why:** These names must match exactly in docker-compose.yml — NOT SPRING_DATASOURCE_URL or similar variants.

**How to apply:** When editing docker-compose.yml, use these exact env var names for the api service.
