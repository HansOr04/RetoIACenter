# HU-006 · Seleccionar opciones de cobertura

**Como** agente de seguros  
**Quiero** seleccionar las coberturas y garantías para cada ubicación  
**Para que** el cálculo de prima incluya exactamente las protecciones contratadas

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

- Endpoints afectados: `PUT /api/v1/folios/{id}/ubicaciones/{idx}/coberturas`
- Tablas afectadas: `folios` (JSONB coberturas por ubicación)
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-006, TC-I-051

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
