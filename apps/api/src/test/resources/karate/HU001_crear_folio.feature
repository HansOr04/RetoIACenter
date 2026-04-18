Feature: HU-001 - Crear folio con idempotencia

  Background:
    * url baseUrl
    * def uniqueKey = 'key-' + java.util.UUID.randomUUID()

  Scenario: CA-01 - Crear folio nuevo retorna 201 y body correcto
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = uniqueKey
    And request { tipoNegocio: 'INCENDIO', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    And match response.estadoCotizacion == 'BORRADOR'
    And match response.numeroFolio == '#regex F\\d{4}-\\d{4}'
    And match response.version == 1

  Scenario: CA-02 - Segundo POST con misma key retorna 200 con mismo folio
    * def iKey = 'idempotent-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = iKey
    And request { tipoNegocio: 'INCENDIO', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    * def folio1 = response

    Given path '/api/v1/folios'
    And header X-Idempotency-Key = iKey
    And request { tipoNegocio: 'INCENDIO', codigoAgente: 'AGT-001' }
    When method POST
    Then status 200
    And match response.numeroFolio == folio1.numeroFolio

  Scenario: CA-03 - Crear folio sin body retorna 201
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = uniqueKey
    When method POST
    Then status 201
    And match response.estadoCotizacion == 'BORRADOR'

  Scenario: CA-04 - Crear folio sin header X-Idempotency-Key retorna 400
    Given path '/api/v1/folios'
    And request { tipoNegocio: 'INCENDIO' }
    When method POST
    Then status 400
    And match response.title == 'Header requerido ausente'

  Scenario: CA-05 - Crear folio con codigoAgente nulo retorna 201
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = uniqueKey
    And request { tipoNegocio: 'INCENDIO' }
    When method POST
    Then status 201
    And match response.estadoCotizacion == 'BORRADOR'
    And match response.codigoAgente == '#null'

  Scenario: CA-06 - Estado del folio creado es BORRADOR
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = uniqueKey
    And request {}
    When method POST
    Then status 201
    And match response.estadoCotizacion == 'BORRADOR'
    And match response.version == 1
    And match response.fechaCreacion == '#notnull'
