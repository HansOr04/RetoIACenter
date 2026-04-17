# cotizador-danos-web

Frontend del Cotizador de Seguros de Daños · Next.js 15 + TypeScript + TailwindCSS 4 + shadcn/ui.

## Comandos

```bash
pnpm dev          # Servidor de desarrollo en http://localhost:3000
pnpm build        # Build de producción
pnpm test         # Tests unitarios con Jest
pnpm test:coverage # Tests con reporte de cobertura
pnpm lint         # ESLint
```

## Estructura de src/

```
src/
├── app/                     ← Rutas Next.js (App Router)
│   ├── layout.tsx           ← Layout raíz con TanStack Query provider
│   ├── page.tsx             ← Pantalla de inicio
│   └── cotizador/           ← Rutas del cotizador (TODO: HU-F01 a HU-F06)
├── components/
│   ├── ui/                  ← Primitivos shadcn/ui
│   ├── forms/               ← Formularios React Hook Form + Zod
│   ├── layout/              ← Layouts reutilizables
│   └── cotizacion/          ← Componentes del dominio
├── hooks/                   ← Custom hooks
├── stores/                  ← Zustand stores
├── services/
│   ├── api/                 ← TanStack Query hooks para el backend
│   └── core/                ← Llamadas al core-stub
└── lib/
    ├── schemas/             ← Zod schemas de validación
    └── utils/               ← Utilidades puras
```
