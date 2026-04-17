# HU-F01 · Crear o abrir un folio desde la pantalla de inicio

**Como** agente de seguros  
**Quiero** crear un nuevo folio o abrir uno existente desde la pantalla de inicio  
**Para que** pueda comenzar o continuar el proceso de cotización de daños

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
> TODO: completar — `src/app/page.tsx`, `src/components/cotizacion/FolioSelector.tsx`

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `POST /api/v1/folios`, `GET /api/v1/folios`
- Tablas afectadas: N/A (consume API)
- Componentes frontend afectados: `page.tsx`, nuevo componente `FolioSelector`
- Test cases relacionados: TC-F-001, TC-E-001

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
