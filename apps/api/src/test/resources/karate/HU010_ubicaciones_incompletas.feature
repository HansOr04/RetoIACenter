Feature: HU-010 Validación y alertas de ubicaciones incompletas

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu010-test-' + java.util.UUID.randomUUID()
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
        "numeroUbicaciones": 5,
        "seccionesAplican": {
          "direccion": true, "datosTecnicos": true,
          "giroComercial": true, "garantias": true
        }
      }
      """
    When method PUT
    Then status 200

  Scenario: CA-10-01 Ubicacion con CP invalido genera alerta CODIGO_POSTAL_INVALIDO
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request
      """
      {
        "nombreUbicacion": "Test CP Invalido",
        "direccion": "Calle 1",
        "codigoPostal": "000000",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio", "claveIncendio": "INC-01" },
        "garantias": ["INCENDIO"]
      }
      """
    When method PUT
    Then status 200
    And match response.estadoValidacion == 'INCOMPLETO'
    And match response.alertasBloqueantes[*].codigo contains 'CODIGO_POSTAL_INVALIDO'

  Scenario: CA-10-02 Ubicacion sin garantias genera alerta SIN_GARANTIAS_TARIFABLES
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request
      """
      {
        "nombreUbicacion": "Test Sin Garantias",
        "direccion": "Calle 2",
        "codigoPostal": "090001",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio", "claveIncendio": "INC-01" },
        "garantias": []
      }
      """
    When method PUT
    Then status 200
    And match response.alertasBloqueantes[*].codigo contains 'SIN_GARANTIAS_TARIFABLES'

  Scenario: CA-10-03 Ubicacion completa retorna VALIDO sin alertas
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request
      """
      {
        "nombreUbicacion": "Ubicacion Completa",
        "direccion": "Av. Principal 500",
        "codigoPostal": "090001",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio General", "claveIncendio": "INC-01" },
        "garantias": ["INCENDIO", "ROBO"]
      }
      """
    When method PUT
    Then status 200
    And match response.estadoValidacion == 'VALIDO'
    And match response.alertasBloqueantes == '[]'

  Scenario: CA-10-04 GET summary muestra conteo correcto de incompletas
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request
      """
      {
        "nombreUbicacion": "Incompleta",
        "direccion": "Calle X",
        "codigoPostal": "000000",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio", "claveIncendio": "INC-01" },
        "garantias": []
      }
      """
    When method PUT
    Then status 200
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/summary'
    When method GET
    Then status 200
    And match response.total == 1
    And match response.incompletas == 1
    And match response.completas == 0
    And match response.indicesIncompletos contains 0
    And match response.detalleIncompletas[0].alertas != null

  Scenario: CA-10-05 GET summary para folio sin ubicaciones retorna totales en cero
    * def folioKey2 = 'hu010-empty-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey2
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    * def folioVacio = response.numeroFolio
    Given path '/api/v1/folios/' + folioVacio + '/ubicaciones/layout'
    And request { numeroUbicaciones: 2, seccionesAplican: { direccion: true, datosTecnicos: false, giroComercial: false, garantias: false } }
    When method PUT
    Then status 200
    Given path '/api/v1/quotes/' + folioVacio + '/locations/summary'
    When method GET
    Then status 200
    And match response.total == 0
    And match response.completas == 0
    And match response.incompletas == 0
    And match response.calculables == 0

  Scenario: CA-10-06 Alerta FALTA_CLAVE_INCENDIO generada cuando giro sin claveIncendio
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request
      """
      {
        "nombreUbicacion": "Sin Clave Incendio",
        "direccion": "Calle Z",
        "codigoPostal": "090001",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio", "claveIncendio": "" },
        "garantias": ["INCENDIO"]
      }
      """
    When method PUT
    Then status 200
    And match response.alertasBloqueantes[*].codigo contains 'FALTA_CLAVE_INCENDIO'
