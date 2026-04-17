# HU-004 · CRUD de ubicaciones

**Como** agente de seguros  
**Quiero** crear, leer, actualizar y eliminar ubicaciones dentro de un folio  
**Para que** pueda capturar los datos de cada inmueble o bien asegurable

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
> TODO: completar

### DÓNDE en la arquitectura
> TODO: completar

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `POST /api/v1/folios/{id}/ubicaciones`, `PUT /api/v1/folios/{id}/ubicaciones/{idx}`, `DELETE /api/v1/folios/{id}/ubicaciones/{idx}`
- Tablas afectadas: `folios` (JSONB)
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-004, TC-I-031

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
