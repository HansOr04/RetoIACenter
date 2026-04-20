# HU-F06 · Visualización del resultado

**Como** usuario del cotizador
**Quiero** visualizar la prima neta y comercial calculada en base a mi cotización
**Para que** pueda comunicarle al cliente el monto a pagar o corregir errores en ubicaciones problemáticas

## Criterios de aceptación

- **CA-F06-01 · Cálculo exitoso y visualización de montos**
  - Dado que el folio está en un estado válido general (aunque tenga algunas ubicaciones incompletas permitidas)
  - Cuando llego a /cotizador/{folio}/calculo
  - Entonces veo un Card principal con `primaNeta` y `primaComercial` mostradas en tamaño grande
  - Y veo una tabla con el desglose de prima aportada por cada ubicación válida.

- **CA-F06-02 · Ubicaciones excluidas/incompletas (Regla del negocio)**
  - Cuando el cálculo se hace con 15 ubicaciones pero 3 están incompletas
  - Entonces el sistema me muestra un aviso de exclusión.
  - Y percibo un panel de alertas rojo/amarillo listando "Ubicación 4, 7 y 9 excluidas por falta de datos", y con links respectivos a corregirlas. 

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | Es una vista de solo lectura/presentación (o disparador) separada del resto. |
| Negotiable | ✅ | La estructura de la tabla de desglose puede ser simplificada o expandible. |
| Valuable | ✅ | Es el "Gran final" del flujo, donde se entrega la cotización financiera. |
| Estimable | ✅ | 4 horas |
| Small | ✅ | Consiste en un request final calculador y en el render de UI de resultados monetarios. |
| Testable | ✅ | Display correcto de formato de moneda y tabla iterada sobre los resultados. |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar
1. Hook `useCalculoPrima` o aprovechar el estado general si retorna la prima. Puede disparar GET o delegar al POST emitir-calculo.
2. Componentes UI grandes y enfocados en "Moneda": mostrar cifras financieras con separación de miles (.toLocalString).
3. Entender la lista de `alertasDeExclusion` devueltas por el backend si las ubicaciones no logran cotizarse por estar incompletas.

### DÓNDE en la arquitectura
`src/app/cotizador/[folio]/calculo/page.tsx` + Componentes para mostrar cifras financieras y panel de `Alert`.

### POR QUÉ
El usuario agente de seguros necesita una vista limpia del resultado. El motor excluye ubicaciones corruptas para no frenar la venta, pero el usuario debe ser notificado para que tenga la opción de regresar a corregirlas.

## Contrato API consumido
- GET /api/v1/folios/{folio}/calculo (o su equivalente para obtener el estado final con calculo)

## Componentes usados
- ResultCard
- DataTable
- AlertBanner
- LinkButton (para volver a editar)

## Reglas de negocio aplicadas
- El cálculo excluye ubicaciones incompletas sin failear toda la transacción, pero reporta alertas.

## Trazabilidad
- **HU backend requeridas:** HU-007, HU-010
- **Páginas:** `/cotizador/[folio]/calculo`
- **Test cases:** TC-F06-a a TC-F06-b

## Definition of Done
- [x] Card de montos implementada y con formato de currency.
- [x] Tabla de desglose funcionando.
- [x] Panel de Notificación de ubicaciones incompletas integrado redigiendo a la pantalla respectiva.
- [x] Presenta correctamente fallas de tarificadores por riesgos inasegurables.

## Estado
- [x] Spec aprobado
- [x] Implementación
- [x] Tests unitarios
- [x] Integrado en flujo E2E
