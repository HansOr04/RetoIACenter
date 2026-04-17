# Skill: Backend Integration Test Generator

## Propósito

Generar pruebas de integración para endpoints REST del backend usando Karate DSL. Los tests cubren el happy path y al menos un escenario de error por endpoint.

## Inputs

- Especificación del endpoint (URL, método, body, respuesta esperada)
- `specs/guidelines/qa-guidelines.md`
- Fixtures disponibles en `apps/core-stub/src/fixtures/`

## Outputs

- Archivos `.feature` en `tests/integration/src/test/java/features/`
- IDs de casos asignados (TC-I-XXX)
- Actualización de `specs/output/backend/HU-XXX-report.md` con los casos generados

## Pasos de ejecución

### 1. Identificar endpoints a probar

Por cada endpoint implementado en la HU:
- Método HTTP + ruta (ej. `POST /api/v1/folios`)
- Request body schema
- Response body schema
- Códigos de error esperados

### 2. Generar escenarios Gherkin en Karate

Para cada endpoint generar al menos:
- Escenario happy path (201/200)
- Escenario de validación fallida (400)
- Escenario de recurso no encontrado (404) si aplica

### 3. Configurar fixtures

Si el endpoint llama al core-stub, configurar el mock en Karate usando `karate.configure('ssl', true)` o un mock server de Karate.

### 4. Verificar cobertura

Asegurar que los casos generados cubran los criterios de aceptación de la HU.

## Plantilla de feature file

```gherkin
Feature: HU-XXX — [Nombre del endpoint]

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'

  Scenario: TC-I-001 — Crear folio exitosamente
    Given path '/api/v1/folios'
    And request { "rfc": "SOFK900101XXX", "agentId": "AGT-001" }
    When method POST
    Then status 201
    And match response.folioId == '#uuid'

  Scenario: TC-I-002 — Error al crear folio con RFC inválido
    Given path '/api/v1/folios'
    And request { "rfc": "", "agentId": "AGT-001" }
    When method POST
    Then status 400
    And match response.type == '#notnull'
```
