# HU-F06 · Visualización del resultado de la cotización

**Como** agente de seguros  
**Quiero** ver el resumen del cálculo de prima por ubicación y el total de la cotización  
**Para que** pueda presentar el resultado al asegurado y proceder a la emisión o ajuste

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
> TODO: completar — `src/components/cotizacion/ResultadoCotizacion.tsx`, `src/app/cotizador/[id]/resultado/page.tsx`

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `GET /api/v1/folios/{id}` (con primas calculadas)
- Tablas afectadas: N/A (consume API)
- Componentes frontend afectados: `ResultadoCotizacion`, `PrimaTable`
- Test cases relacionados: TC-F-006, TC-E-003

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
