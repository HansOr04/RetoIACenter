# HU-010 · Validar ubicaciones incompletas al calcular

**Como** sistema  
**Quiero** validar que todas las ubicaciones tengan datos suficientes antes de calcular  
**Para que** no se genere un cálculo incorrecto por datos faltantes

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
> TODO: completar — validación en el use case de cálculo antes de ejecutar la lógica actuarial

### DÓNDE en la arquitectura
> TODO: completar

### POR QUÉ desde la perspectiva del dominio
> TODO: completar

## Trazabilidad

- Endpoints afectados: `POST /api/v1/folios/{id}/calcular` (pre-validación)
- Tablas afectadas: ninguna (validación en memoria)
- Componentes frontend afectados: N/A (HU de backend)
- Test cases relacionados: TC-B-010, TC-I-091

## Estado
- [ ] Spec aprobado
- [ ] Implementación
- [ ] Tests unitarios
- [ ] Tests integración
- [ ] Tests E2E
- [ ] Documentación
