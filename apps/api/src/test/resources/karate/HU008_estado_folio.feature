Feature: HU-008 Consulta de estado y progreso del folio

  Background:
    * url 'http://localhost:8080'

  Scenario: TC-008-a Folio recién creado — 0% progreso
    * def folioKey = 'hu008-a-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    Given path '/api/v1/folios/' + numeroFolio + '/estado'
    When method GET
    Then status 200
    And match response.porcentajeProgreso == 0
    And match response.esCalculable == false
    And match response.alertas == '#[2]'

  Scenario: TC-008-b Folio con datos generales — 50% progreso
    * def folioKey = 'hu008-b-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request {}
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request
      """
      {
        "nombreTomador": "Test SA",
        "rucCedula": "1791234560001",
        "correoElectronico": "test@test.com",
        "tipoInmueble": "CASA",
        "usoPrincipal": "HABITACIONAL"
      }
      """
    When method PUT
    Then status 200
    Given path '/api/v1/folios/' + numeroFolio + '/estado'
    When method GET
    Then status 200
    And match response.porcentajeProgreso == 50
    And match response.esCalculable == false
    And match response.alertas == '#[1]'
    And match response.seccionesCompletadas.datosGenerales == true
    And match response.seccionesCompletadas.layoutUbicaciones == false

  Scenario: TC-008-c Folio completo — 100% y esCalculable true
    * def folioKey = 'hu008-c-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request {}
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    # Datos generales
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request { nombreTomador: 'Test', rucCedula: '1791234560001', correoElectronico: 'a@b.com', tipoInmueble: 'CASA', usoPrincipal: 'HABITACIONAL' }
    When method PUT
    Then status 200
    # Layout
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request { numeroUbicaciones: 2, seccionesAplican: { direccion: true, datosTecnicos: true, giroComercial: false, garantias: false } }
    When method PUT
    Then status 200
    # Estado
    Given path '/api/v1/folios/' + numeroFolio + '/estado'
    When method GET
    Then status 200
    And match response.porcentajeProgreso == 100
    And match response.esCalculable == true
    And match response.alertas == '#[0]'

  Scenario: TC-008-d Folio con layout pero sin datos generales — 50%
    * def folioKey = 'hu008-d-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request {}
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    Given path '/api/v1/folios/' + numeroFolio + '/ubicaciones/layout'
    And request { numeroUbicaciones: 1, seccionesAplican: { direccion: true, datosTecnicos: false, giroComercial: false, garantias: false } }
    When method PUT
    Then status 200
    Given path '/api/v1/folios/' + numeroFolio + '/estado'
    When method GET
    Then status 200
    And match response.porcentajeProgreso == 50
    And match response.seccionesCompletadas.datosGenerales == false
    And match response.seccionesCompletadas.layoutUbicaciones == true

  Scenario: TC-008-e Folio inexistente — 404
    Given path '/api/v1/folios/FOLIO-INEXISTENTE/estado'
    When method GET
    Then status 404
    And match response.title == 'Folio no encontrado'

  Scenario: TC-008-f porcentajeProgreso es entero entre 0 y 100
    * def folioKey = 'hu008-f-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request {}
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    Given path '/api/v1/folios/' + numeroFolio + '/estado'
    When method GET
    Then status 200
    And match response.porcentajeProgreso == '#number'
    And assert response.porcentajeProgreso >= 0
    And assert response.porcentajeProgreso <= 100

  Scenario: TC-008-g Response contiene numeroFolio y estadoCotizacion
    * def folioKey = 'hu008-g-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request {}
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    Given path '/api/v1/folios/' + numeroFolio + '/estado'
    When method GET
    Then status 200
    And match response.numeroFolio == numeroFolio
    And match response.estadoCotizacion == 'BORRADOR'
    And match response.version == '#number'
