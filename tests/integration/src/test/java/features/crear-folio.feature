Feature: HU-001 · Crear folio con idempotencia

  Background:
    * url baseUrl

  Scenario: Crear folio nuevo retorna 201 Created
    Given path 'folios'
    And header X-Idempotency-Key = 'test-' + java.util.UUID.randomUUID()
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AG-001' }
    When method POST
    Then status 201
    And match response.numeroFolio == '#regex F\\d{4}-\\d+'
    And match response.estadoCotizacion == 'INICIADA'
    And match response.version == 1
    And match response.fechaCreacion == '#string'

  Scenario: Reintento con misma idempotency key retorna el mismo folio
    * def key = 'test-idem-' + java.util.UUID.randomUUID()

    Given path 'folios'
    And header X-Idempotency-Key = key
    And request { tipoNegocio: 'COMERCIAL' }
    When method POST
    Then status 201
    * def folio1 = response.numeroFolio

    Given path 'folios'
    And header X-Idempotency-Key = key
    And request { tipoNegocio: 'COMERCIAL' }
    When method POST
    Then status 200
    And match response.numeroFolio == folio1

  Scenario: Sin header X-Idempotency-Key retorna 400
    Given path 'folios'
    And request { tipoNegocio: 'COMERCIAL' }
    When method POST
    Then status 400
    And match response.type contains 'idempotency'
    And match response.status == 400
