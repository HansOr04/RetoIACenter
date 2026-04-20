Feature: HU-002 · Captura y consulta de datos generales

  Background:
    * url baseUrl
    * def folioResp = call read('helpers/crear-folio.feature')
    * def folio = folioResp.numeroFolio
    * def versionInicial = folioResp.version

  Scenario: PUT con datos válidos actualiza y retorna version incrementada
    Given path 'quotes', folio, 'general-info'
    And header If-Match = versionInicial
    And request
      """
      {
        razonSocial: 'Empresa Integracion SA',
        ruc: '1792146739001',
        clasificacionRiesgo: 'BAJO',
        tipoNegocio: 'COMERCIAL'
      }
      """
    When method PUT
    Then status 200
    And match response.version == versionInicial + 1
    And match response.fechaUltimaActualizacion == '#string'

  Scenario: GET retorna los datos guardados
    * def version = versionInicial

    # Primero PUT
    Given path 'quotes', folio, 'general-info'
    And header If-Match = version
    And request { razonSocial: 'Test SA', ruc: '1234567890', clasificacionRiesgo: 'MEDIO' }
    When method PUT
    Then status 200

    # Luego GET
    Given path 'quotes', folio, 'general-info'
    When method GET
    Then status 200
    And match response.razonSocial == 'Test SA'
    And match response.ruc == '1234567890'

  Scenario: PUT con version obsoleta retorna 409
    Given path 'quotes', folio, 'general-info'
    And header If-Match = '0'
    And request { razonSocial: 'Test', ruc: '123', clasificacionRiesgo: 'BAJO' }
    When method PUT
    Then status 409
    And match response.type contains 'version'

  Scenario: PUT sin If-Match retorna 428
    Given path 'quotes', folio, 'general-info'
    And request { razonSocial: 'Test', ruc: '123', clasificacionRiesgo: 'BAJO' }
    When method PUT
    Then status 428

  Scenario: Folio inexistente retorna 404
    Given path 'quotes', 'F9999-99999', 'general-info'
    When method GET
    Then status 404
