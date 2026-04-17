# HU-009 · Control de concurrencia con versionado optimista

**Como** sistema  
**Quiero** detectar ediciones concurrentes del mismo folio  
**Para que** no se pierdan cambios cuando dos sesiones editan el mismo folio simultáneamente

## Criterios de aceptación

> TODO: completar criterios en formato Dado/Cuando/Entonces

## Evaluación INVEST

- [ ] Independent · justificación
- [ ] Negotiable · justificación
- [ ] Valuable · justificación
- [ ] Estimable · estimación en horas
- [ ] Small · cabe en un día
- [ ] Testable · cómo se probará

## Análisis técnico

### QUÉ implementar
> TODO: completar — campo `version` en la entidad Folio, `@Version` JPA, respuesta 409 si versión no coincide

### DÓNDE en la arquitectura
> TODO: completar

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: todos los `PUT`/`PATCH` de folios (agregar `If-Match` header o campo `version` en body)
- Tablas afectadas: `folios` (columna `version INT`)
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-009, TC-I-081

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
