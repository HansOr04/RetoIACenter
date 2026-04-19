Feature: HU-006 Opciones de cobertura

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu006-test-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio

    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request
      """
      {
        "numeroUbicaciones": 2,
        "seccionesAplican": {
          "direccion": true,
          "datosTecnicos": true,
          "giroComercial": true,
          "garantias": false
        }
      }
      """
    When method PUT
    Then status 200

    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And request
      """
      {
        "nombreUbicacion": "Oficina Principal",
        "direccion": "Av. Amazonas 123",
        "codigoPostal": "170103",
        "estado": "Pichincha",
        "municipio": "Quito",
        "colonia": "Centro",
        "ciudad": "Quito",
        "tipoConstructivo": "CONCRETO_ARMADO",
        "nivel": 1,
        "anioConstruccion": 2005,
        "giro": { "codigo": "B1", "descripcion": "Oficinas", "claveIncendio": "B1" }
      }
      """
    When method POST
    Then status 201
    * def currentVersion = response.version

  Scenario: CA-06-01 - GET devuelve defaults cuando no hay opciones configuradas
    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    When method GET
    Then status 200
    And match response.incendioEdificios == true
    And match response.incendioContenidos == false

  Scenario: CA-06-02 - PUT configura opciones correctamente y retorna ETag actualizado
    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    And header If-Match = currentVersion
    And request
      """
      {
        "incendioEdificios": true,
        "incendioContenidos": true,
        "extensionCobertura": false,
        "catTev": false,
        "catFhm": false,
        "remocionEscombros": false,
        "gastosExtraordinarios": false,
        "perdidaRentas": false,
        "bi": false,
        "equipoElectronico": false,
        "robo": false,
        "dineroValores": false,
        "vidrios": false,
        "anunciosLuminosos": false
      }
      """
    When method PUT
    Then status 200
    And match response.incendioEdificios == true
    And match response.incendioContenidos == true
    And match header ETag == '#notnull'
    And match response.version == currentVersion + 1

  Scenario: CA-06-03 - PUT retorna 422 cuando todas las coberturas son false
    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    And header If-Match = currentVersion
    And request
      """
      {
        "incendioEdificios": false,
        "incendioContenidos": false,
        "extensionCobertura": false,
        "catTev": false,
        "catFhm": false,
        "remocionEscombros": false,
        "gastosExtraordinarios": false,
        "perdidaRentas": false,
        "bi": false,
        "equipoElectronico": false,
        "robo": false,
        "dineroValores": false,
        "vidrios": false,
        "anunciosLuminosos": false
      }
      """
    When method PUT
    Then status 422

  Scenario: CA-06-04 - PUT retorna 409 cuando la versión es obsoleta
    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    And header If-Match = '999'
    And request { "incendioEdificios": true }
    When method PUT
    Then status 409

  Scenario: CA-06-05 - PUT retorna 428 cuando falta el header If-Match
    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    And request { "incendioEdificios": true }
    When method PUT
    Then status 428

  Scenario: CA-06-06 - PUT incluye warning cuando catTev activo sin zona TEV en ubicaciones
    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    And header If-Match = currentVersion
    And request
      """
      {
        "incendioEdificios": true,
        "incendioContenidos": false,
        "extensionCobertura": false,
        "catTev": true,
        "catFhm": false,
        "remocionEscombros": false,
        "gastosExtraordinarios": false,
        "perdidaRentas": false,
        "bi": false,
        "equipoElectronico": false,
        "robo": false,
        "dineroValores": false,
        "vidrios": false,
        "anunciosLuminosos": false
      }
      """
    When method PUT
    Then status 200
    And match response.warnings != null
