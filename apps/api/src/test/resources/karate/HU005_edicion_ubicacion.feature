Feature: HU-005 Edición puntual de ubicación

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu005-test-' + java.util.UUID.randomUUID()
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
          "direccion": true, "datosTecnicos": true,
          "giroComercial": true, "garantias": true
        }
      }
      """
    When method PUT
    Then status 200

    Given path '/api/v1/quotes/' + numeroFolio + '/locations'
    And header If-Match = '1'
    And request
      """
      {
        "nombreUbicacion": "Bodega Original",
        "direccion": "Av. Original 100",
        "codigoPostal": "090001",
        "tipoConstructivo": "MAMPOSTERIA",
        "giro": { "codigo": "G001", "descripcion": "Comercio", "claveIncendio": "INC-01" },
        "garantias": ["INCENDIO"]
      }
      """
    When method PUT
    Then status 200
    * def etagTrasRegistro = responseHeaders['ETag'][0]

  Scenario: CA-05-01 PATCH nombreUbicacion actualiza campo correctamente
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/0'
    And header If-Match = etagTrasRegistro
    And request { nombreUbicacion: 'Bodega Editada' }
    When method PATCH
    Then status 200
    And match response.nombreUbicacion == 'Bodega Editada'
    And match response.indice == 0
    And match responseHeaders['ETag'][0] != null

  Scenario: CA-05-02 PATCH actualiza ETag con nueva versión
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/0'
    And header If-Match = etagTrasRegistro
    And request { nombreUbicacion: 'Bodega v2' }
    When method PATCH
    Then status 200
    * def nuevaVersion = response.version
    And match nuevaVersion > 1

  Scenario: CA-05-03 PATCH con version incorrecta en If-Match retorna 409
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/0'
    And header If-Match = '999'
    And request { nombreUbicacion: 'Intento fallido' }
    When method PATCH
    Then status 409
    And match response.type contains 'version-conflict'
    And match response.currentVersion != null
    And match response.receivedVersion == 999

  Scenario: CA-05-04 PATCH índice inexistente retorna 404
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/99'
    And header If-Match = etagTrasRegistro
    And request { nombreUbicacion: 'Sin índice' }
    When method PATCH
    Then status 404
    And match response.title == 'Ubicación no encontrada'

  Scenario: CA-05-05 PATCH campos inmutables — indice no cambia
    Given path '/api/v1/quotes/' + numeroFolio + '/locations/0'
    And header If-Match = etagTrasRegistro
    And request { nombreUbicacion: 'Nombre Nuevo', direccion: 'Nueva dirección 200' }
    When method PATCH
    Then status 200
    And match response.indice == 0
    And match response.direccion == 'Nueva dirección 200'

  Scenario: CA-05-06 PATCH folio inexistente retorna 404
    Given path '/api/v1/quotes/FOLIO-9999/locations/0'
    And header If-Match = '1'
    And request { nombreUbicacion: 'Test' }
    When method PATCH
    Then status 404
