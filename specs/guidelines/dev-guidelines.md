# Lineamientos de Desarrollo

> Cargado obligatoriamente por los agentes Backend y Frontend antes de generar código.

## 1. Clean Code

- **Nombres significativos**: variables, métodos y clases deben revelar intención. Evitar abreviaturas crípticas.
- **Funciones pequeñas**: máximo 20 líneas por método. Una función hace una sola cosa.
- **Sin comentarios explicativos del "qué"**: el código legible no necesita comentarios de qué hace, solo del porqué no obvio.
- **Principio de menor sorpresa**: el comportamiento de una función debe coincidir con su nombre.
- **Constantes con nombre**: nunca números o strings "mágicos" inline.

## 2. Clean Architecture — Las 4 capas

```
domain        →  lógica de negocio pura, sin framework
application   →  orquestación de casos de uso
infrastructure→  adaptadores externos (DB, HTTP, mensajería)
interfaces    →  entrada/salida (REST controllers, serialización)
```

**Regla de dependencias**: las capas externas dependen de las internas, nunca al revés.
`interfaces → application → domain` (infrastructure implementa puertos de domain).

## 3. SOLID

| Principio | Aplicación concreta |
|---|---|
| SRP | Cada clase tiene un único motivo de cambio |
| OCP | Nuevas coberturas se agregan implementando interfaces, sin modificar clases existentes |
| LSP | Los adapters sustituyen puertos sin alterar comportamiento esperado |
| ISP | Puertos pequeños y cohesivos (no un único `RepositoryGod`) |
| DIP | Domain solo conoce abstracciones (interfaces/puertos) |

## 4. Manejo de errores

- **Backend**: usar `ProblemDetail` (RFC 7807) en todos los errores HTTP.
- **Frontend**: errores de API se manejan en la capa `services/api`, nunca en componentes.
- **Core-stub**: responder siempre JSON con estructura `{ error: string, code: string }` en errores.
- No exponer stack traces en producción.

## 5. Versionado de API

- Todas las rutas del backend inician con `/api/v1/...`
- El core-stub usa `/v1/...`

## 6. Logging

- **Formato**: JSON estructurado (no plain text).
- **Campos obligatorios**: `timestamp`, `level`, `service`, `traceId`, `message`.
- **Sin PII**: no loguear nombres, documentos, contraseñas, tokens.
- **Backend**: usar SLF4J + Logback con appender JSON.

## 7. Validación de entrada

- **Backend**: Bean Validation (`@Valid`, `@NotNull`, `@Size`...) en DTOs de la capa `interfaces`.
- **Frontend**: Zod schemas en `src/lib/schemas/` — nunca validar en componentes directamente.
- Rechazar entradas inválidas en el borde del sistema, no en el dominio.

## 8. Lista negra de librerías

| Librería | Versión prohibida | Razón |
|---|---|---|
| `commons-lang` | < 3.x | API deprecated, reemplazar con `commons-lang3` |
| `jackson-databind` | < 2.15 | CVEs críticos conocidos |
| `moment.js` | cualquier versión | Bundle pesado, usar `date-fns` o `dayjs` |
| `enzyme` | cualquier versión | Sin soporte React 18+, usar RTL |
