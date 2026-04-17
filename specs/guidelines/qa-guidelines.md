# Lineamientos de QA

> Cargado obligatoriamente por el agente QA antes de generar estrategias de prueba o casos.

## 1. Cobertura mínima

- **Backend**: 80% de cobertura de líneas reportada por JaCoCo. El build falla por debajo de ese umbral.
- **Frontend**: 80% de cobertura de statements reportada por Jest. Configurado en `jest.config.ts`.
- La cobertura mide calidad del scaffold de pruebas, no garantiza correctitud — los casos deben ser significativos.

## 2. Patrón AAA (Arrange-Act-Assert)

Toda prueba unitaria sigue la estructura:
```
// Arrange — preparar datos y mocks
// Act — ejecutar la unidad bajo prueba
// Assert — verificar resultado
```
Sin pasos combinados ni múltiples asserts que prueben cosas distintas en un mismo test.

## 3. Naming convention

```
should_<acción esperada>_when_<condición>_given_<contexto>
```

Ejemplos:
- `should_returnFolioId_when_folioIsCreated_given_validRequest`
- `should_throwNotFoundException_when_folioNotFound_given_nonExistentId`

## 4. Mocks y dependencias externas

- Toda dependencia externa (base de datos, HTTP al core-stub, mensajería) se mockea en pruebas unitarias.
- Para pruebas de integración backend se usa **Testcontainers** para Postgres real.
- El core-stub actúa como mock real para pruebas de integración del backend.

## 5. Fixtures versionados

- Los fixtures del core-stub viven en `apps/core-stub/src/fixtures/`.
- Los fixtures de Playwright viven en `tests/e2e/fixtures/`.
- Los fixtures de Karate viven inline en los feature files o en `tests/integration/src/test/resources/`.
- Versionar fixtures junto con el código — no datos efímeros.

## 6. Definition of Done (DoD)

Una historia de usuario se considera terminada cuando:
- [ ] Pruebas unitarias backend pasan con ≥80% cobertura
- [ ] Pruebas unitarias frontend pasan con ≥80% cobertura
- [ ] Prueba de integración Karate cubre el happy path y al menos un caso de error
- [ ] Prueba E2E Playwright cubre el flujo principal
- [ ] CI pasa en verde

## 7. Los 3 flujos automatizados obligatorios

Los flujos seleccionados para automatización E2E deben justificarse con criterios de:
1. **Frecuencia de uso** — flujos que el usuario ejecuta en cada sesión
2. **Impacto en el negocio** — errores en este flujo generan pérdidas directas
3. **Fragilidad** — flujos que históricamente fallan con cambios

Los 3 flujos propuestos para este reto:
1. **Creación de folio y captura de datos generales** (HU-001 + HU-002)
2. **Agregar ubicación y seleccionar coberturas** (HU-004 + HU-006)
3. **Calcular prima y cambiar estado de folio** (HU-007 + HU-008)
