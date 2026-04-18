Feature: HU-003 Configuración de layout de ubicaciones

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu003-test-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    * def versionInicial = response.version

    * def layoutValido =
      """
      {
        "numeroUbicaciones": 5,
        "seccionesAplican": {
          "direccion": true,
          "datosTecnicos": true,
          "giroComercial": false,
          "garantias": false
        }
      }
      """

  Scenario: TC-003-a PUT layout exitoso incrementa version
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request layoutValido
    When method PUT
    Then status 200
    And match response.version == versionInicial + 1
    And match response.layoutUbicaciones.numeroUbicaciones == 5
    And match response.layoutUbicaciones.seccionesAplican.direccion == true
    And match response.layoutUbicaciones.seccionesAplican.datosTecnicos == true

  Scenario: TC-003-b GET después de guardar layout retorna los datos persistidos
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request layoutValido
    When method PUT
    Then status 200
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    When method GET
    Then status 200
    And match response.layoutUbicaciones.numeroUbicaciones == 5
    And match response.layoutUbicaciones.seccionesAplican.direccion == true

  Scenario: TC-003-c GET sin layout previo retorna layoutUbicaciones null
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    When method GET
    Then status 200
    And match response.layoutUbicaciones == '#null'

  Scenario: TC-003-d PUT folio inexistente retorna 404
    Given path '/api/v1/folios/FOLIO-NO-EXISTE/ubicaciones/layout'
    And request layoutValido
    When method PUT
    Then status 404
    And match response.title == 'Folio no encontrado'

  Scenario: TC-003-e GET folio inexistente retorna 404
    Given path '/api/v1/folios/FOLIO-NO-EXISTE/ubicaciones/layout'
    When method GET
    Then status 404
    And match response.title == 'Folio no encontrado'

  Scenario: TC-003-f PUT con direccion=false retorna 400 por regla de negocio
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request
      """
      {
        "numeroUbicaciones": 3,
        "seccionesAplican": {
          "direccion": false,
          "datosTecnicos": true,
          "giroComercial": false,
          "garantias": false
        }
      }
      """
    When method PUT
    Then status 400
    And match response.title == 'Regla de negocio violada'

  Scenario: TC-003-g PUT numeroUbicaciones fuera de rango retorna 400
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request
      """
      {
        "numeroUbicaciones": 25,
        "seccionesAplican": {
          "direccion": true,
          "datosTecnicos": false,
          "giroComercial": false,
          "garantias": false
        }
      }
      """
    When method PUT
    Then status 400

  Scenario: TC-003-h PUT idempotente - mismos datos dos veces no incrementa version
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request layoutValido
    When method PUT
    Then status 200
    * def versionTras1er = response.version
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request layoutValido
    When method PUT
    Then status 200
    And match response.version == versionTras1er
