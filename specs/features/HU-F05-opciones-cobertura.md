# HU-F05 · Selección de opciones de cobertura en la interfaz

**Como** agente de seguros  
**Quiero** seleccionar las coberturas para cada ubicación mediante checkboxes y sliders  
**Para que** pueda personalizar la protección de cada bien asegurable visualmente

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
> TODO: completar — `src/components/cotizacion/CoberturasPanel.tsx`

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `PUT /api/v1/folios/{id}/ubicaciones/{idx}/coberturas`, `GET /v1/catalogs/guarantees`
- Tablas afectadas: N/A (consume API y core-stub)
- Componentes frontend afectados: `CoberturasPanel`, `useFolioStore`
- Test cases relacionados: TC-F-005, TC-E-002

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
