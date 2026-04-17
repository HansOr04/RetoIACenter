# Skill: Backend Clean Code Reviewer

## Propósito

Revisar el código Java generado o modificado contra los estándares de Clean Code y Clean Architecture del proyecto. Produce un reporte con observaciones y, si es posible, las correcciones aplicadas.

## Inputs

- Código fuente Java a revisar (archivos o fragmentos)
- `specs/guidelines/dev-guidelines.md`
- `specs/guidelines/architecture-standards.md`

## Outputs

- Lista de observaciones clasificadas por severidad: BLOQUEANTE / ADVERTENCIA / SUGERENCIA
- Código corregido (para observaciones BLOQUEANTE)
- Checklist de conformidad firmado

## Pasos de ejecución

### 1. Verificar reglas de arquitectura
- ¿Ninguna clase de `domain/` importa Spring o JPA? → BLOQUEANTE si falla
- ¿Los controllers solo delegan a use cases (sin lógica)? → BLOQUEANTE si falla
- ¿Los use cases no conocen clases de `infrastructure/`? → BLOQUEANTE si falla

### 2. Verificar Clean Code
- ¿Los métodos tienen ≤20 líneas? → ADVERTENCIA si supera 30, BLOQUEANTE si supera 50
- ¿Los nombres de variables y métodos son descriptivos? → SUGERENCIA
- ¿No hay números o strings mágicos inline? → ADVERTENCIA
- ¿Se usa `ProblemDetail` para errores HTTP? → BLOQUEANTE si no se usa en controllers

### 3. Verificar SOLID
- ¿Cada clase tiene una única responsabilidad? → ADVERTENCIA si parece tener varias
- ¿Se usan interfaces en lugar de implementaciones concretas en dependencias? → BLOQUEANTE si no

### 4. Verificar librerías prohibidas
- Buscar imports de librerías en lista negra → BLOQUEANTE si se encuentra alguna

## Formato del reporte

```markdown
## Clean Code Review — HU-XXX

### Observaciones BLOQUEANTES
- [archivo:línea] Descripción del problema y corrección requerida

### Advertencias
- [archivo:línea] Descripción

### Sugerencias
- [archivo:línea] Mejora opcional

### Checklist de conformidad
- [x] Arquitectura de capas respetada
- [x] Sin frameworks en el dominio
- [x] ProblemDetail en errores
- [ ] Nombres descriptivos (ver sugerencias)
```

## Ejemplo de uso

Input: `FolioController.java` con lógica de negocio en el método `createFolio()`.

Observación BLOQUEANTE: "El controller contiene lógica de validación de negocio. Mover a `CreateFolioUseCase`."
