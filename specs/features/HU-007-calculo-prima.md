# HU-007 · Calcular prima de la cotización

**Como** agente de seguros  
**Quiero** calcular la prima total y por ubicación de la cotización  
**Para que** el asegurado conozca el costo de su póliza antes de contratarla

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
> TODO: completar — NOTA: lógica de cálculo actuarial a definir por el desarrollador

### DÓNDE en la arquitectura
> TODO: completar

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `POST /api/v1/folios/{id}/calcular`
- Tablas afectadas: `folios` (JSONB primasPorUbicacion), `tarifas_incendio`, `tarifas_cat`, `tarifa_fhm`, `factores_equipo_electronico`
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-007, TC-I-061

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
