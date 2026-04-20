# HU-F02 · Captura de datos generales

**Como** usuario del cotizador
**Quiero** poder capturar los datos generales del cliente (nombre, email, vigencia)
**Para que** el folio tenga la información base requerida antes de avanzar al modelo de ubicaciones

## Criterios de aceptación

- **CA-F02-01 · Validación de formulario client-side**
  - Dado que estoy en /cotizador/{folio}/datos-generales
  - Cuando intento enviar el formulario con datos inválidos (ej. email sin formato, vigencia menor a 1 año)
  - Entonces veo errores de validación inmediatamente en pantalla y no se envía la petición

- **CA-F02-02 · Mutación exitosa**
  - Dado que el formulario es válido
  - Cuando hago click en "Siguiente"
  - Entonces se realiza una mutación PUT /folios/{folio}/datos-generales
  - Y se incluye el header `If-Match` para el versionado optimista
  - Y soy redirigido al siguiente paso

- **CA-F02-03 · Conflicto de versión (412 Precondition Failed)**
  - Cuando la mutación falla por error 412 (If-Match)
  - Entonces se muestra mensaje indicando que los datos fueron modificados por otra sesión

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | Depende de F01, pero su lógica de validación e interfaz es independiente de las demás vistas. |
| Negotiable | ✅ | La librería de validación y UI puede adaptarse (react-hook-form + Zod). |
| Valuable | ✅ | Es el primer recaudo de datos de tarificación. |
| Estimable | ✅ | 4 horas |
| Small | ✅ | Un solo formulario con 4-5 campos base. |
| Testable | ✅ | Criterios verificables con unit tests en los hooks y form validation. |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar
1. Formulario usando `react-hook-form` y esquemas de `Zod` para validación client-side.
2. Hook `useUpdateDatosGenerales()` con mutación PUT a través de Fetch / Server Action.
3. Manejo explícito de las versiones (`If-Match`) requeridas por optimistic locking.

### DÓNDE en la arquitectura
Página principal en `src/app/cotizador/[folio]/datos-generales/page.tsx`, validaciones en `src/lib/schemas/`, y componentes UI reusables (`Input`, form adapters).

### POR QUÉ
La validación en cliente (Zod) da un UX instantáneo y evita llamadas innecesarias al backend. Implementar `If-Match` es obligatorio por arquitectura (optimistic concurrency).

## Contrato API consumido
- PUT /api/v1/folios/{folio}/datos-generales
- Headers: `If-Match: "{version}"`

## Componentes usados
- Form
- Input
- Select (si aplica)
- Button (Siguiente/Atrás)

## Reglas de negocio aplicadas
- Vigencia de póliza, edad mínima, u otras reglas especificadas en el backend se replican en Zod.
- Control de concurrencia obligatoria.

## Trazabilidad
- **HU backend relacionada:** HU-002
- **Páginas:** `/cotizador/[folio]/datos-generales`
- **Test cases:** TC-F02-a a TC-F02-c

## Definition of Done
- [x] UI del formulario implementada y conectada a RHF.
- [x] Esquema `Zod` definido con alertas y mensajes claros.
- [x] Hook de mutación maneja correctamente los estatus HTTP, incluyendo 412.
- [x] Formato de Payload y Headers coinciden con OpenApi.
- [x] Test unitario para RHF / Zod form behaviors.

## Estado
- [x] Spec aprobado
- [x] Implementación
- [x] Tests unitarios
- [x] Integrado en flujo E2E
