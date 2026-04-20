Feature: Helper · Folio con datos generales y layout configurado

  Background:
    * url baseUrl
    * def folioResp = call read('crear-folio.feature')
    * def folio = folioResp.numeroFolio

  Scenario: Setup completo hasta layout
    Given path 'folios', folio, 'datos-generales'
    And header If-Match = '1'
    And request { razonSocial: 'Helper SA', ruc: '1234567890', clasificacionRiesgo: 'BAJO', tipoNegocio: 'COMERCIAL' }
    When method PUT
    Then status 200

    Given path 'folios', folio, 'ubicaciones/layout'
    And header If-Match = '2'
    And request { cantidadUbicaciones: 2 }
    When method PUT
    Then status 200

    * def version = response.version
