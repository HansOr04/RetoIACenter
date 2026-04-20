# HU-F03 · Configuración de layout

**Como** usuario del cotizador
**Quiero** establecer la cantidad total de ubicaciones que tendrá esta póliza
**Para que** el sistema genere la estructura de datos que me permita gestionarlas posterioremente

## Criterios de aceptación

- **CA-F03-01 · Input numérico válido**
  - Dado que estoy en /cotizador/{folio}/layout
  - Cuando introduzco una cantidad mayor a 0
  - Y presiono "Continuar"
  - Entonces se invoca la mutación PUT de layout

- **CA-F03-02 · Restricción de cantidad negativa o cero**
  - Cuando introduzco 0 o un valor negativo en el campo cantidad de ubicaciones
  - Entonces validación client-side muestra mensaje de error y desactiva el botón

- **CA-F03-03 · Guardado de nueva configuración**
  - Dado que el folio tiene un If-Match válido
  - Cuando el layout es actualizado en el backend
  - Entonces se redirige fluidamente al paso de gestión de ubicaciones

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | Vista enfocada exclusivamente en inicialización de la matriz de ubicaciones. |
| Negotiable | ✅ | La UI o la mecánica de confirmación en caso de reducir el número de ubicaciones es refinable. |
| Valuable | ✅ | Define el volumen de la cotización que puede representar gran variación en esfuerzo de captura. |
| Estimable | ✅ | 2 horas |
| Small | ✅ | Un solo componente/formulario |
| Testable | ✅ | Lógica de input e If-Match son probables en Testing Library. |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar
1. Input numérico para `cantidadUbicaciones`.
2. Validación client-side de número entero > 0.
3. Mutación PUT al endpoint layout.
4. Hook para la transición de página.

### DÓNDE en la arquitectura
`src/app/cotizador/[folio]/layout/page.tsx` (Nota: layout real o una subruta page.tsx, no Layout de Next).

### POR QUÉ
El layout es necesario separar de la "gestión de ubicaciones" masiva porque es una acción estructurante. Reducir la cantidad causaría remoción en el backend.

## Contrato API consumido
- PUT /api/v1/folios/{folio}/layout
- Headers: `If-Match: "{version}"`

## Componentes usados
- NumericInput / Input
- Form validation

## Reglas de negocio aplicadas
- Mínimo 1 ubicación global para cotizar.

## Trazabilidad
- **HU backend relacionada:** HU-003
- **Páginas:** `/cotizador/[folio]/layout` (ruta de vista)
- **Test cases:** TC-F03-a a TC-F03-b

## Definition of Done
- [x] Input numérico configurado.
- [x] Validaciones de client-side integradas.
- [x] Hook de mutación implementado.
- [x] Versionado cubierto.

## Estado
- [x] Spec aprobado
- [x] Implementación
- [x] Tests unitarios
- [x] Integrado en flujo E2E
