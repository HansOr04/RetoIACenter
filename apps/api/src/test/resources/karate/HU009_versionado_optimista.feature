Feature: HU-009 Versionado optimista con header If-Match

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu009-test-' + java.util.UUID.randomUUID()
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

    * def ubicacionBase =
      """
      {
        "nombreUbicacion": "Prueba",
        "direccion": "Calle 1",
        "codigoPostal": "090001",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio", "claveIncendio": "INC-01" },
        "garantias": ["INCENDIO"]
      }
      """

  Scenario: CA-09-01 PUT sin If-Match retorna 428 Precondition Required
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And request ubicacionBase
    When method PUT
    Then status 428
    And match response.title == 'Header If-Match requerido'

  Scenario: CA-09-02 PATCH sin If-Match retorna 428 Precondition Required
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/0'
    And request { nombreUbicacion: 'Sin header' }
    When method PATCH
    Then status 428
    And match response.title == 'Header If-Match requerido'

  Scenario: CA-09-03 PUT con If-Match correcto pasa el interceptor
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionBase
    When method PUT
    Then status 200
    And match responseHeaders['ETag'][0] != null

  Scenario: CA-09-04 ETag en respuesta PUT coincide con nueva version
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionBase
    When method PUT
    Then status 200
    * def version = response.version
    * def etag = responseHeaders['ETag'][0]
    And match etag == '' + version

  Scenario: CA-09-05 PATCH con If-Match de versión correcta actualiza exitosamente
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request ubicacionBase
    When method PUT
    Then status 200
    * def etag = responseHeaders['ETag'][0]
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/0'
    And header If-Match = etag
    And request { nombreUbicacion: 'Actualizado' }
    When method PATCH
    Then status 200
    And match response.nombreUbicacion == 'Actualizado'

  Scenario: CA-09-06 GET locations no requiere If-Match
    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    When method GET
    Then status 200
