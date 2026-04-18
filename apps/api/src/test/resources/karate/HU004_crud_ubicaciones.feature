Feature: HU-004 CRUD de ubicaciones en cotización

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu004-test-' + java.util.UUID.randomUUID()
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
          "garantias": true
        }
      }
      """
    When method PUT
    Then status 200

    * def ubicacionValida =
      """
      {
        "nombreUbicacion": "Bodega Central",
        "direccion": "Av. Principal 123",
        "codigoPostal": "090001",
        "estado": "Guayas",
        "municipio": "Guayaquil",
        "colonia": "Centro",
        "ciudad": "Guayaquil",
        "tipoConstructivo": "MAMPOSTERIA",
        "nivel": 1,
        "anioConstruccion": 2000,
        "giro": {
          "codigo": "G001",
          "descripcion": "Comercio General",
          "claveIncendio": "INC-01"
        },
        "garantias": ["INCENDIO", "ROBO"]
      }
      """

  Scenario: CA-04-01 PUT primera ubicacion retorna indice 0 y ETag
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionValida
    When method PUT
    Then status 200
    And match response.indice == 0
    And match response.nombreUbicacion == 'Bodega Central'
    And match response.estadoValidacion != null
    And match responseHeaders['ETag'][0] != null

  Scenario: CA-04-02 PUT segunda ubicacion retorna indice 1
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionValida
    When method PUT
    Then status 200
    * def etag = responseHeaders['ETag'][0]
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = etag
    And request
      """
      {
        "nombreUbicacion": "Sucursal Norte",
        "direccion": "Av. Norte 456",
        "codigoPostal": "090002",
        "tipoConstructivo": "CONCRETO",
        "giro": { "codigo": "G002", "descripcion": "Oficina", "claveIncendio": "INC-02" },
        "garantias": ["INCENDIO"]
      }
      """
    When method PUT
    Then status 200
    And match response.indice == 1

  Scenario: CA-04-03 GET lista todas las ubicaciones registradas
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionValida
    When method PUT
    Then status 200
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    When method GET
    Then status 200
    And match response == '#[1]'
    And match response[0].indice == 0

  Scenario: CA-04-04 GET lista vacía cuando no hay ubicaciones
    * def folioKey2 = 'hu004-empty-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey2
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-002' }
    When method POST
    Then status 201
    * def folioVacio = response.numeroFolio
    Given path '/api/v1/folios/' + folioVacio + '/ubicaciones/layout'
    And request { numeroUbicaciones: 2, seccionesAplican: { direccion: true, datosTecnicos: false, giroComercial: false, garantias: false } }
    When method PUT
    Then status 200
    Given path '/api/v1/quotes/' + folioVacio + '/locations'
    When method GET
    Then status 200
    And match response == '[]'

  Scenario: CA-04-05 PUT cuando capacidad excedida retorna 409
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionValida
    When method PUT
    Then status 200
    * def etag = responseHeaders['ETag'][0]
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = etag
    And request ubicacionValida
    When method PUT
    Then status 200
    * def etag2 = responseHeaders['ETag'][0]
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = etag2
    And request ubicacionValida
    When method PUT
    Then status 200
    * def etag3 = responseHeaders['ETag'][0]
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = etag3
    And request ubicacionValida
    When method PUT
    Then status 409
    And match response.type contains 'capacity-exceeded'

  Scenario: CA-04-06 PUT para folio inexistente retorna 404
    Given path '/api/v1/quotes/FOLIO-INVALIDO-9999/locations'
    And header If-Match = '1'
    And request ubicacionValida
    When method PUT
    Then status 404
    And match response.title == 'Folio no encontrado'

  Scenario: CA-04-07 PUT sin layout configurado retorna 500 o error de estado
    * def folioKey3 = 'hu004-nolayout-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey3
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-003' }
    When method POST
    Then status 201
    * def folioSinLayout = response.numeroFolio
    Given path '/api/v1/quotes/' + folioSinLayout + '/locations'
    And header If-Match = '1'
    And request ubicacionValida
    When method PUT
    Then status >= 400
