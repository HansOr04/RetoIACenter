# Skill: Frontend Component Reviewer

## Propósito

Revisar componentes React/Next.js generados contra los estándares del proyecto: tipado TypeScript correcto, uso apropiado de shadcn/ui, sin anti-patrones de React.

## Inputs

- Código fuente de componentes React a revisar
- `specs/guidelines/dev-guidelines.md`
- `specs/guidelines/tech-stack-constraints.md`

## Outputs

- Lista de observaciones clasificadas: BLOQUEANTE / ADVERTENCIA / SUGERENCIA
- Código corregido para observaciones BLOQUEANTE
- Checklist de conformidad

## Pasos de ejecución

### 1. Verificar TypeScript
- ¿Ningún `any` sin justificación comentada? → BLOQUEANTE
- ¿Todas las props tienen interfaces definidas? → BLOQUEANTE
- ¿Los handlers de eventos tienen tipos correctos (`React.ChangeEvent<HTMLInputElement>`, etc.)? → ADVERTENCIA

### 2. Verificar patrones React
- ¿No hay `useEffect` para fetching? (usar TanStack Query) → BLOQUEANTE
- ¿El estado global está en Zustand, no en Context para estado compartido entre rutas? → ADVERTENCIA
- ¿Los formularios usan React Hook Form + Zod, no estado manual? → ADVERTENCIA
- ¿Los componentes no superan 150 líneas? → ADVERTENCIA si supera, BLOQUEANTE si supera 300

### 3. Verificar UI/UX
- ¿Se usan componentes de `shadcn/ui` para inputs, botones, modals? → SUGERENCIA si no
- ¿Hay manejo de estados de carga (skeleton/spinner)? → ADVERTENCIA si no
- ¿Hay manejo de estados de error visible al usuario? → BLOQUEANTE si no

### 4. Verificar estructura de archivos
- ¿El componente vive en la carpeta correcta según su tipo? → SUGERENCIA
- ¿Se exporta con nombre (no default export) si es un componente de librería? → SUGERENCIA

## Formato del reporte

```markdown
## Component Review — HU-FXX

### Observaciones BLOQUEANTES
- [archivo:línea] Descripción y corrección

### Advertencias
- [archivo:línea] Descripción

### Checklist de conformidad
- [x] Sin `any`
- [x] Props tipadas
- [x] Sin `useEffect` para fetching
- [ ] Estados de carga implementados
```
