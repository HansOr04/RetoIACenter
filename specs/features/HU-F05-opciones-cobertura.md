# HU-F05 · Opciones de cobertura

**Como** usuario del cotizador
**Quiero** seleccionar qué coberturas estarán activas para todas las ubicaciones
**Para que** el cálculo final de la prima considere las protecciones deseadas por el cliente

## Criterios de aceptación

- **CA-F05-01 · Renderizado de la lista de coberturas**
  - Dado que estoy en /cotizador/{folio}/coberturas
  - Entonces se me presentan las opciones de coberturas divididas visualmente en un grid agrupado (por ejemplo, Incendio, Robo, RC, etc.)

- **CA-F05-02 · Validación "Al menos una activa"**
  - Cuando desmarco todas las opciones de cobertura
  - Entonces el formulario se invalida 
  - Y se muestra un mensaje de error requiriendo al menos una cobertura base
  - Y el botón de "Calcular Prima" se desactiva

- **CA-F05-03 · Advertencias del backend**
  - Cuando selecciono una configuración que el backend marca como conflictiva (warning de coberturas no compatibles)
  - Entonces se muestran en la interfaz los warnings recibidos, pero me permite continuar si no son errores bloqueantes.

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | Aunque afecta al cálculo, su interfaz es una colección de booleans / checkboxes aislada. |
| Negotiable | ✅ | La agrupación o el diseño de los checkboxes es modificable en UI. |
| Valuable | ✅ | Personaliza el producto de seguros deseado. |
| Estimable | ✅ | 3 horas |
| Small | ✅ | Formulario compuesto mayormente de múltiples booleanos. |
| Testable | ✅ | Comportamiento del form cuando todas están desmarcadas es trivial de testear. |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar
1. Grupo de 14 checkboxes organizados jerárquicamente.
2. Regla de validación con Zod que asegure que el array/objeto resultante incluya al menos un `true` relevante para "Cobertura Básica".
3. Visualización de mensajes "warnings" retornados quizás por llamadas pre-vuelo o la consulta de estado.
4. Mutación PUT específica para las coberturas globales de la póliza.

### DÓNDE en la arquitectura
`src/app/cotizador/[folio]/coberturas/page.tsx` + hook `useUpdateCoberturas`.

### POR QUÉ
Las coberturas en este sistema aplican "a nivel póliza", no por ubicación. Por lo que su vista se separa de la gestión individual de los inmuebles.

## Contrato API consumido
- PUT /api/v1/folios/{folio}/coberturas
- Headers: `If-Match: "{version}"`

## Componentes usados
- Checkbox / Switch
- Grupo de campos (Fieldset)
- Alert (para warnings)

## Reglas de negocio aplicadas
- Requiere cobertura básica de Incendio o similar; al menos 1 cobertura general en true.

## Trazabilidad
- **HU backend requeridas:** HU-006
- **Páginas:** `/cotizador/[folio]/coberturas`
- **Test cases:** TC-F05-a a TC-F05-b

## Definition of Done
- [x] Checkboxes implementados en UI.
- [x] Agrupación visual terminada.
- [x] Validación Zod (al menos 1) integrada.
- [x] Hook de sincronización conectado con PUT de coberturas.

## Estado
- [x] Spec aprobado
- [x] Implementación
- [x] Tests unitarios
- [x] Integrado en flujo E2E
