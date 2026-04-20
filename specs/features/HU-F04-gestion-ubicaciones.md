# HU-F04 · Gestión de ubicaciones

**Como** usuario del cotizador
**Quiero** visualizar, agregar y editar las características individuales de múltiples ubicaciones
**Para que** pueda tarificar cada uno de los riesgos representados conforme a la cantidad seleccionada en layout

## Criterios de aceptación

- **CA-F04-01 · Visualización de panel y lista de estados**
  - Dado que estoy en /cotizador/{folio}/ubicaciones
  - Entonces veo una lista o grilla de cards correspondientes a las N ubicaciones configuradas.
  - Y aquellas que tienen datos faltantes están marcadas como INCOMPLETO, y las listas como VÁLIDO.

- **CA-F04-02 · Apertura del formulario de edición**
  - Cuando hago clic en una tarjeta de ubicación, o uso el botón de "Agregar" / "Editar"
  - Entonces se despliega un panel lateral, modal o sección expandida con el formulario específico.

- **CA-F04-03 · Actualización puntual (PATCH)**
  - Dado que edito los valores de una ubicación particular (ej. Tipo constructivo)
  - Cuando guardo o se realiza auto-save de ese panel
  - Entonces se lanza una petición PATCH con la actualización específica (partial update) al backend

## Evaluación INVEST

| Criterio | ✓ | Justificación |
|---|---|---|
| Independent | ✅ | Es la vista más robusta del frontend, pero su lógica está autocontenida a las ubicaciones del folio. |
| Negotiable | ✅ | La UI para editar (modal vs panel vs acordeón) es flexible. |
| Valuable | ✅ | Core de la captura de riesgo individual. |
| Estimable | ✅ | 6 horas |
| Small | ✅ | Aunque es grande, su alcance es claro y está limitado al modelo de dominios CRUD. |
| Testable | ✅ | UI states y rendering condicional de StatusBadge. |

**Veredicto:** APROBADA

## Análisis técnico

### QUÉ implementar
1. Lista iterativa de cards con estado visual (Badge: VÁLIDO/INCOMPLETO).
2. Interfaz principal para ver el panorama general, y vista de detalle (modal/panel) para el formulario de ubicación.
3. Formulario amplio para capturar campos como CP, Tipo Constructivo, Valor de Contenidos.
4. Hook para usar método PATCH con edición parcial de cada ubicación individual.

### DÓNDE en la arquitectura
`src/app/cotizador/[folio]/ubicaciones/page.tsx` + componentes compartidos como Modals, Cards, o Drawers. 

### POR QUÉ
Manejar "N" ubicaciones exige UX donde el usuario sepa cuáles le falta por llenar sin que un formulario sea abrumador. PATCH evita mandar el payload inmenso de 100 ubicaciones solo por cambiar un código postal.

## Contrato API consumido
- GET /api/v1/folios/{folio}/ubicaciones
- POST /api/v1/folios/{folio}/ubicaciones (para nuevo)
- PATCH /api/v1/folios/{folio}/ubicaciones/{ubicacionId}

## Componentes usados
- LocationCard
- StatusBadge
- SidePanel / Modal
- Form elements

## Reglas de negocio aplicadas
- Estado INCOMPLETO se asigna a ubicaciones registradas pero que carecen de campos críticos.
- Control de If-Match en el root folio.

## Trazabilidad
- **HU backend requeridas:** HU-004, HU-005
- **Páginas:** `/cotizador/[folio]/ubicaciones`
- **Test cases:** TC-F04-a a TC-F04-c

## Definition of Done
- [x] UI de la lista de Cards que muestran VÁLIDO/INCOMPLETO.
- [x] Panel de edición implementado.
- [x] Hook de mutación configurado para PATCH parcial.
- [x] Tests unitarios (ej. React Testing Library verificando renderizado de la badge correcta según datos).

## Estado
- [x] Spec aprobado
- [x] Implementación
- [x] Tests unitarios
- [x] Integrado en flujo E2E
