# HU-F04 · Gestión de ubicaciones en la interfaz

**Como** agente de seguros  
**Quiero** agregar, editar y eliminar ubicaciones mediante formularios interactivos  
**Para que** pueda capturar todos los datos de los bienes asegurables de forma guiada

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
> TODO: completar — `src/components/forms/UbicacionForm.tsx`, `src/components/cotizacion/UbicacionesTable.tsx`

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `POST /api/v1/folios/{id}/ubicaciones`, `PUT`, `PATCH`, `DELETE`
- Tablas afectadas: N/A (consume API)
- Componentes frontend afectados: `UbicacionForm`, `UbicacionesTable`, `useFolioStore`
- Test cases relacionados: TC-F-004, TC-E-002

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
