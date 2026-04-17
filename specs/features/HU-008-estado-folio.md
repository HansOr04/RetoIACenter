# HU-008 · Gestionar estado del folio

**Como** agente de seguros  
**Quiero** cambiar el estado del folio (BORRADOR → CALCULADO → EMITIDO / CANCELADO)  
**Para que** el ciclo de vida de la cotización quede controlado y trazable

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
> TODO: completar — máquina de estados en la entidad Folio

## Trazabilidad

- Endpoints afectados: `PATCH /api/v1/folios/{id}/estado`
- Tablas afectadas: `folios`
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-008, TC-I-071

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
