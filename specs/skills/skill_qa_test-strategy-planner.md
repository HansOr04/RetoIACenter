# Skill: QA Test Strategy Planner

## Propósito

Definir la estrategia de prueba para una HU o conjunto de HUs, ubicando cada tipo de prueba en el nivel correcto de la pirámide de testing y justificando las decisiones.

## Inputs

- HU analizada con criterios de aceptación
- Análisis técnico del Spec Agent
- `specs/guidelines/qa-guidelines.md`

## Outputs

- Sección "Estrategia de Prueba" para `docs/TESTING_STRATEGY.md`
- Clasificación de pruebas por nivel de pirámide
- Identificación de riesgos y mitigaciones
- Contribución a `specs/output/qa/HU-XXX-qa-report.md`

## Pasos de ejecución

### 1. Clasificar por pirámide de testing

```
         /‾‾‾‾‾‾‾‾‾\
        /    E2E    \       Playwright — 3 flujos críticos
       /─────────────\
      / Integración   \     Karate DSL — happy path + errores por endpoint
     /─────────────────\
    /    Unitarias      \   JUnit 5 + Jest — lógica de negocio y componentes
   /─────────────────────\
```

### 2. Asignar tipo de prueba por criterio de aceptación

Para cada criterio de aceptación de la HU:
- ¿Es lógica de negocio pura? → Prueba unitaria (JUnit)
- ¿Es un flujo de API completo? → Prueba de integración (Karate)
- ¿Es un flujo visible al usuario? → Candidato a E2E (Playwright) si es flujo crítico

### 3. Identificar riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Ejemplo: Cálculo de prima incorrecto | Alta | Crítico | Test unitario exhaustivo con tabla de valores |

### 4. Verificar DoD

Confirmar que la suma de pruebas planificadas cubre el DoD definido en `qa-guidelines.md`.

## Formato del output de estrategia

```markdown
## Estrategia de Prueba — HU-XXX

### Nivel Unitario
- [ ] TC-B-XXX: [descripción] — [clase a probar]
- [ ] TC-F-XXX: [descripción] — [componente a probar]

### Nivel Integración
- [ ] TC-I-XXX: [descripción] — [endpoint]

### Nivel E2E
- [ ] TC-E-XXX: [descripción] — [flujo crítico]

### Riesgos identificados
1. [riesgo] — mitigación: [acción]

### DoD
- [ ] 80% cobertura unitaria backend
- [ ] 80% cobertura unitaria frontend
- [ ] Karate happy path + error
- [ ] E2E si es flujo crítico
```
