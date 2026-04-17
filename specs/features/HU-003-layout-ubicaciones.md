# HU-003 · Configurar layout de ubicaciones

**Como** agente de seguros  
**Quiero** definir el número y tipo de ubicaciones que tendrá la cotización  
**Para que** el sistema prepare la estructura para capturar cada ubicación asegurable

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

- Endpoints afectados: `PUT /api/v1/folios/{id}/layout`
- Tablas afectadas: `folios` (campo JSONB de ubicaciones)
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-003, TC-I-021

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
