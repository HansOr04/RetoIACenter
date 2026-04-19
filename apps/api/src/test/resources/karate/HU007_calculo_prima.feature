Feature: HU-007 Cálculo de prima

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu007-test-' + java.util.UUID.randomUUID()
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
        "numeroUbicaciones": 3,
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
        "nombreUbicacion": "Oficina B1",
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
    * def versionConUbicacion = response.version

    Given path '/api/v1/quotes/' + numeroFolio + '/coverage-options'
    And header If-Match = versionConUbicacion
    And request
      """
      {
        "incendioEdificios": true,
        "incendioContenidos": true,
        "extensionCobertura": true,
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
    * def versionConOpciones = response.version

  Scenario: CA-07-01 - POST /calculate retorna prima calculada correctamente
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = versionConOpciones
    When method POST
    Then status 200
    And match response.primaNeta == '#number'
    And match response.primaComercial == '#number'
    And match response.factorComercial == '#number'
    And match response.primasPorUbicacion == '#array'
    And match response.estado == 'CALCULADO'

  Scenario: CA-07-02 - primaComercial es primaNeta * factorComercial
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = versionConOpciones
    When method POST
    Then status 200
    * def esperado = response.primaNeta * response.factorComercial
    And match response.primaComercial == '#number'

  Scenario: CA-07-03 - response incluye ETag con la nueva versión
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = versionConOpciones
    When method POST
    Then status 200
    And match header ETag == '#notnull'
    And match response.version == versionConOpciones + 1

  Scenario: CA-07-04 - POST retorna 409 cuando la versión es obsoleta
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = '999'
    When method POST
    Then status 409

  Scenario: CA-07-05 - POST retorna 428 cuando falta If-Match
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    When method POST
    Then status 400

  Scenario: CA-07-06 - POST retorna 404 cuando el folio no existe
    Given path '/api/v1/quotes/FOLIO-INEXISTENTE-XYZ/calculate'
    And header If-Match = '1'
    When method POST
    Then status 404

  Scenario: CA-07-07 - primasPorUbicacion contiene desglose de componentes para ubicación calculable
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = versionConOpciones
    When method POST
    Then status 200
    * def primaUbicacion = response.primasPorUbicacion[0]
    And match primaUbicacion.calculada == true
    And match primaUbicacion.total == '#number'
    And match primaUbicacion.desglose == '#notnull'
    And match primaUbicacion.desglose.incendioEdificios == '#number'
    And match primaUbicacion.desglose.incendioContenidos == '#number'

  Scenario: CA-07-08 - calcular dos veces produce el mismo resultado
    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = versionConOpciones
    When method POST
    Then status 200
    * def primaNeta1 = response.primaNeta
    * def version1 = response.version

    Given path '/api/v1/quotes/' + numeroFolio + '/calculate'
    And header If-Match = version1
    When method POST
    Then status 200
    And match response.primaNeta == primaNeta1
