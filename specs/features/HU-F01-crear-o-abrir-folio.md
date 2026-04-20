# HU-F01 · Crear o abrir folio

**Como** usuario del cotizador
**Quiero** poder crear una cotización nueva o reabrir una existente por su número de folio
**Para que** pueda iniciar el proceso o continuar uno previo sin perder datos

## Criterios de aceptación

- **CA-F01-01 · Crear folio nuevo**
  - Dado que estoy en la home
  - Cuando click en "Crear cotización"
  - Entonces se crea un folio nuevo en el backend
  - Y soy redirigido a /cotizador/{folio}/datos-generales

- **CA-F01-02 · Abrir folio existente**
  - Dado que tengo un número de folio (ej. F2026-1473)
  - Cuando lo ingreso en el campo "Abrir folio" y click "Continuar"
  - Entonces se carga el estado del folio
  - Y soy redirigido al paso pendiente

- **CA-F01-03 · Folio inexistente**
  - Cuando ingreso un folio que no existe
  - Entonces se muestra mensaje "Folio no encontrado"
  - Y no se redirige

- **CA-F01-04 · Idempotencia en botón "Crear"**
  - Cuando hago click múltiples veces en "Crear cotización"
  - Entonces solo se crea un folio (botón se deshabilita mientras procesa)

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | Independiente de las demás HUs de frontend |
| Negotiable | ✅ | La UI del campo puede ser input o modal |
| Valuable | ✅ | Puerta de entrada al sistema |
| Estimable | ✅ | 3 horas |
| Small | ✅ | Cabe en medio día |
| Testable | ✅ | 4 criterios automatizables con Playwright |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar
1. Página home `/` con 2 acciones: "Crear cotización" y "Abrir folio existente"
2. Hook `useCreateFolio()` que usa TanStack Query con mutación POST /folios
3. Hook `useFolio(numeroFolio)` que fetch GET /folios/{f}/estado
4. Redirección con `useRouter` de Next.js
5. Generación de `X-Idempotency-Key` en el cliente (crypto.randomUUID)

### DÓNDE en la arquitectura
`src/app/page.tsx` + `src/hooks/useFolio.ts` + `src/services/api/folios.ts`

### POR QUÉ
Sin entrada al sistema no hay uso. Separar creación de reapertura permite a agentes reanudar el trabajo sin empezar desde cero.

## Contrato API consumido
- POST /api/v1/folios (crear)
- GET /api/v1/folios/{folio}/estado (consultar)

## Componentes usados
- Button (primario: crear, secundario: abrir)
- Input
- Card
- FieldError

## Reglas de negocio aplicadas
- Generación de clave de idempotencia en cliente
- Redirección basada en `progreso` del backend

## Trazabilidad
- **HU backend relacionada:** HU-001, HU-008
- **Páginas:** `/` (home)
- **Test cases:** TC-F01-a a TC-F01-d
- **Flujos E2E:** flujo1-happy-path

## Definition of Done
- [x] Página home renderizada
- [x] Botón "Crear cotización" dispara POST
- [x] Input para reabrir folio funcional
- [x] Redirección al paso pendiente
- [x] Test unitario del hook `useFolio`

## Estado
- [x] Spec aprobado
- [x] Implementación
- [x] Tests unitarios
- [x] Integrado en flujo E2E
