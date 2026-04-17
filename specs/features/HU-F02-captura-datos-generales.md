# HU-F02 · Formulario de captura de datos generales

**Como** agente de seguros  
**Quiero** capturar los datos generales del asegurado y agente mediante un formulario  
**Para que** los datos queden guardados en el folio antes de continuar con las ubicaciones

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
> TODO: completar — `src/components/forms/DatosGeneralesForm.tsx`, schema Zod en `src/lib/schemas/`

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `PUT /api/v1/folios/{id}/datos-generales`
- Tablas afectadas: N/A (consume API)
- Componentes frontend afectados: `DatosGeneralesForm`, Zustand store `useFolioStore`
- Test cases relacionados: TC-F-002, TC-E-001

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
