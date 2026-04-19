---
name: TypeScript strictness rules (SonarQube)
description: SonarQube TS rules enforced via IDE diagnostics in this project
type: feedback
---

The IDE runs SonarQube-style TypeScript checks on every file write. Rules that fire:

- S6759: Props must be `Readonly<{...}>` — always wrap component prop types in Readonly
- S6754: useState must be typed explicitly: `useState<boolean>(false)` not `useState(false)`
- S6479: No Array index as React key — use a semantic string key derived from the item
- S6438: JSX comments inside children must use braces: `{`// comment`}` not bare `// comment`

**Why:** Hooks are configured project-wide and block on warnings.

**How to apply:** Apply all four rules proactively in every new component or page before submitting.
