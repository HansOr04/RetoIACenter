---
name: Cotizador web stack
description: Actual tech stack in apps/web — differs from .claude/rules/frontend.md which describes a different project
type: project
---

apps/web uses Next.js 15 App Router + TypeScript + Tailwind CSS v4 + React 19 + pnpm monorepo.
NOT React+Vite+CSS Modules as described in .claude/rules/frontend.md (that rule is for a different project).

**Why:** The scaffold was generated for this specific insurance quoter project with a more modern stack.

**How to apply:** Always use Tailwind v4 syntax (`@import "tailwindcss"`, `@theme {}` block, no tailwind.config.js plugins array). Use `style` inline for custom color tokens when Tailwind JIT may not pick them up. Keep next.config.ts (TypeScript), not .js.
