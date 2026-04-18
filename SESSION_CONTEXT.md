# Contexto de sesión para Claude Code

Este proyecto sigue la metodología ASDD (Agentic Spec-Driven Development) documentada en `/specs/`.

## Antes de generar cualquier código

1. Lee `/specs/config/config.yaml` para conocer el perfil del desarrollador y el stack.
2. Lee el agent prompt relevante en `/specs/agents/` según la tarea:
   - Tareas de análisis de requerimientos → `agent_spec.prompt.md`
   - Tareas de backend → `agent_backend.prompt.md`
   - Tareas de frontend → `agent_frontend.prompt.md`
   - Tareas de QA → `agent_qa.prompt.md`
3. Lee las guidelines relevantes en `/specs/guidelines/`:
   - `dev-guidelines.md` — siempre
   - `architecture-standards.md` — para backend
   - `tech-stack-constraints.md` — siempre
   - `qa-guidelines.md` — para QA
4. Lee la HU correspondiente en `/specs/features/HU-XXX.md` si la tarea la referencia.

## Reglas de generación

- **Código en la capa correcta** (domain → application → infrastructure → interfaces). Las dependencias apuntan hacia adentro.
- **Tests unitarios en el mismo commit** que el código de producción.
- **Reporte del agente** en `/specs/output/{agente}/HU-XXX-report.md` para cada HU implementada.
- **Commits convencionales** con scope (`feat(api):`, `test(web):`, `docs(specs):`, etc.).
- **Nada de lógica en controllers** — solo recepción, delegación al use case, y mapeo de respuesta.
- **Dominio sin anotaciones de framework** — sin `@Entity`, `@Column`, etc. Usar puertos e implementarlos en infrastructure.
- **Manejo de errores con ProblemDetail** (RFC 7807).
- **Versionado optimista** en toda edición de agregado (incrementar `version`, actualizar `fechaUltimaActualizacion`).

## Trazabilidad obligatoria

Cada HU implementada debe dejar rastro verificable en:
- Código de producción (clases con el comentario `// HU-XXX`)
- Tests (clases con el comentario `// HU-XXX`)
- Reporte en `/specs/output/{agente}/HU-XXX-report.md`
- Actualización del estado en la HU (checklist de Definition of Done marcado)

## Criterios de evaluación del reto

El evaluador del Reto IA Center revisa:
1. Claridad del modelado del dominio
2. Separación entre capas y responsabilidades
3. Calidad del código
4. Consistencia de APIs y manejo de errores
5. Experiencia de usuario en frontend
6. Cobertura y calidad de pruebas
7. Argumentación de los flujos automatizados
8. Trazabilidad del cálculo
9. Calidad de la documentación
10. Facilidad de ejecución local

Cada decisión técnica debe poder mapearse a uno o más de estos criterios.
