Feature: Helper · Crear un folio para reusar

  Background:
    * url baseUrl

  Scenario: Crear folio y retornar datos
    Given path 'folios'
    And header X-Idempotency-Key = 'helper-' + java.util.UUID.randomUUID()
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AG-001' }
    When method POST
    Then status 201
