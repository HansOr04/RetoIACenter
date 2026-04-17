# HU-005 · Edición parcial de ubicación

**Como** agente de seguros  
**Quiero** modificar campos individuales de una ubicación sin reenviar todos los datos  
**Para que** pueda corregir información específica de manera eficiente

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

- Endpoints afectados: `PATCH /api/v1/folios/{id}/ubicaciones/{idx}`
- Tablas afectadas: `folios` (JSONB)
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-005, TC-I-041

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
