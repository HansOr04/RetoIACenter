# Skill: QA Gherkin Case Generator

## Propósito

Transformar los criterios de aceptación de una HU en escenarios Gherkin con sintaxis Karate DSL, listos para ser implementados en `tests/integration/`.

## Inputs

- Criterios de aceptación de la HU (formato Dado/Cuando/Entonces)
- Análisis técnico del Spec Agent (endpoints, datos)
- Fixtures disponibles en `apps/core-stub/src/fixtures/`

## Outputs

- Escenarios Gherkin completos en sintaxis Karate
- IDs de casos (TC-I-XXX)
- Datos de prueba inline o referenciados a fixtures

## Pasos de ejecución

### 1. Mapear criterios a escenarios

Por cada criterio de aceptación "Dado X, cuando Y, entonces Z":
- Identificar el endpoint HTTP correspondiente
- Identificar el dato de entrada y el resultado esperado
- Asignar ID único TC-I-XXX

### 2. Determinar tipo de escenario

- **Happy path**: todos los datos válidos → código 2XX, respuesta con estructura esperada
- **Error de validación**: campo requerido vacío o formato incorrecto → 400 con ProblemDetail
- **No encontrado**: ID inexistente → 404 con ProblemDetail
- **Conflicto**: operación inválida según estado actual → 409

### 3. Escribir escenario Karate

Usar Karate DSL con:
- `Background` para configuración común (baseUrl, headers, auth si aplica)
- `Scenario Outline` + `Examples` para múltiples variantes de datos
- `match` para validaciones estructurales

### 4. Generar datos de prueba

Usar fixtures de `apps/core-stub/src/fixtures/` cuando el escenario requiera datos maestros (suscriptores, agentes, códigos postales).

## Formato de output Karate

```gherkin
Feature: HU-XXX — [Título]
  # TC-I-001, TC-I-002, TC-I-003

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * def folioRequest = read('classpath:fixtures/folio-request.json')

  Scenario: TC-I-001 — [Happy path]
    Given path '/api/v1/[recurso]'
    And request folioRequest
    When method POST
    Then status 201
    And match response == { id: '#uuid', status: 'BORRADOR' }

  Scenario Outline: TC-I-002 — Error de validación
    Given path '/api/v1/[recurso]'
    And request { field: '<valor>' }
    When method POST
    Then status 400
    And match response.type == '#notnull'

    Examples:
      | valor |
      |       |
      | null  |
```

## Convención de IDs

Los IDs son secuenciales globales entre todas las HUs:
- TC-I-001 a TC-I-010: HU-001
- TC-I-011 a TC-I-020: HU-002
- etc.
