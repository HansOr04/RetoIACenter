# Skill: QA Automation Flow Proposer

## Propósito

Identificar y justificar los flujos críticos del sistema que deben automatizarse con Playwright, generando el esqueleto de los tests E2E y la documentación de justificación.

## Inputs

- Lista de HUs implementadas o en implementación
- `specs/guidelines/qa-guidelines.md`
- Criterios del reto: 3 flujos automatizados obligatorios con justificación

## Outputs

- Archivos esqueleto de tests Playwright en `tests/e2e/tests/`
- Justificación documentada por flujo (frecuencia, impacto, fragilidad)
- Contribución a `docs/TESTING_STRATEGY.md`

## Pasos de ejecución

### 1. Identificar candidatos a flujo crítico

Evaluar cada flujo de usuario con la matriz:

| Flujo | Frecuencia (1-5) | Impacto si falla (1-5) | Fragilidad (1-5) | Score |
|---|---|---|---|---|
| ... | | | | Sum |

Seleccionar los 3 con mayor score.

### 2. Justificar cada flujo seleccionado

Para cada flujo elegido documentar:
- **Frecuencia**: cuántas veces por sesión ejecuta el usuario este flujo
- **Impacto en el negocio**: consecuencia directa de un fallo (ej. "el cotizador no puede generar folios")
- **Fragilidad**: qué tan susceptible es a romperse con cambios de UI o backend

### 3. Generar esqueleto Playwright

```typescript
import { test, expect } from '@playwright/test'

test.describe('Flujo X — [Nombre]', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  test('TC-E-001 — [descripción del paso principal]', async ({ page }) => {
    // Arrange
    // TODO: navegar al punto de inicio del flujo

    // Act
    // TODO: ejecutar los pasos del flujo

    // Assert
    // TODO: verificar resultado esperado
  })
})
```

## Los 3 flujos críticos definidos para este reto

### Flujo 1: Creación de folio y datos generales (HU-001 + HU-002)
- **Frecuencia**: 5/5 — es el primer paso de toda cotización
- **Impacto**: 5/5 — sin folio no existe el proceso
- **Fragilidad**: 3/5 — formulario con validaciones puede cambiar

### Flujo 2: Agregar ubicación y seleccionar coberturas (HU-004 + HU-006)
- **Frecuencia**: 5/5 — al menos una ubicación por cotización
- **Impacto**: 5/5 — sin ubicación no hay cálculo
- **Fragilidad**: 4/5 — tabla de ubicaciones compleja con CRUD

### Flujo 3: Calcular prima y cambiar estado (HU-007 + HU-008)
- **Frecuencia**: 4/5 — al finalizar la captura
- **Impacto**: 5/5 — es el output principal del cotizador
- **Fragilidad**: 5/5 — integra con backend y core-stub
