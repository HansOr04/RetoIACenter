# Skill: Frontend UI Test Generator

## Propósito

Generar pruebas unitarias para componentes React usando Jest y React Testing Library (RTL), siguiendo el patrón AAA y las convenciones del proyecto.

## Inputs

- Código fuente del componente a probar
- Props y comportamientos esperados
- `specs/guidelines/qa-guidelines.md`

## Outputs

- Archivos de test en `apps/web/__tests__/` o co-localizados junto al componente
- IDs de casos asignados (TC-F-XXX)
- Cobertura estimada del componente

## Pasos de ejecución

### 1. Identificar escenarios de prueba

Por cada componente:
- Renderizado inicial (estado vacío, con datos, con error)
- Interacciones del usuario (click, input, submit)
- Comportamiento condicional (mostrar/ocultar elementos)

### 2. Generar tests con RTL

Seguir el principio de RTL: "probar como el usuario interactúa, no los detalles de implementación".

- Usar `screen.getByRole`, `screen.getByLabelText` — NO `getByTestId` excepto como último recurso
- `userEvent` sobre `fireEvent` para simular interacciones reales
- Mockear módulos externos (`TanStack Query`, `Zustand`) con `jest.mock()`

### 3. Verificar cobertura

Asegurar que los tests cubren los estados: loading, success, error.

## Plantilla de test

```typescript
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { ComponentName } from '../components/ComponentName'

describe('ComponentName', () => {
  // TC-F-001
  it('should_renderForm_when_mounted_given_emptyState', () => {
    // Arrange
    render(<ComponentName />)

    // Act — (ninguna en este caso, es renderizado inicial)

    // Assert
    expect(screen.getByRole('button', { name: /guardar/i })).toBeInTheDocument()
  })

  // TC-F-002
  it('should_showError_when_submitWithEmptyFields_given_requiredFields', async () => {
    // Arrange
    const user = userEvent.setup()
    render(<ComponentName />)

    // Act
    await user.click(screen.getByRole('button', { name: /guardar/i }))

    // Assert
    expect(screen.getByText(/campo requerido/i)).toBeInTheDocument()
  })
})
```
