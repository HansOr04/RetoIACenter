Feature: HU-002 Captura y consulta de datos generales

  Background:
    * url 'http://localhost:8080'
    * def folioKey = 'hu002-test-' + java.util.UUID.randomUUID()
    Given path '/api/v1/folios'
    And header X-Idempotency-Key = folioKey
    And request { tipoNegocio: 'COMERCIAL', codigoAgente: 'AGT-001' }
    When method POST
    Then status 201
    * def numeroFolio = response.numeroFolio
    * def versionInicial = response.version

    * def datosValidos =
      """
      {
        "nombreTomador": "Empresa Test S.A.",
        "rucCedula": "1792345678001",
        "correoElectronico": "test@empresa.com",
        "telefonoContacto": "0991234567",
        "tipoInmueble": "LOCAL_COMERCIAL",
        "usoPrincipal": "COMERCIAL",
        "anoConstruccion": 2010,
        "numeroPisos": 3,
        "descripcion": "Local comercial en planta baja"
      }
      """

  Scenario: TC-002-a PUT datos generales exitoso
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request datosValidos
    When method PUT
    Then status 200
    And match response.version == versionInicial + 1
    And match response.datosGenerales.nombreTomador == 'Empresa Test S.A.'
    And match response.datosGenerales.tipoInmueble == 'LOCAL_COMERCIAL'

  Scenario: TC-002-b GET después de guardar datos
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request datosValidos
    When method PUT
    Then status 200
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    When method GET
    Then status 200
    And match response.datosGenerales.rucCedula == '1792345678001'

  Scenario: TC-002-c GET sin datos previos
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    When method GET
    Then status 200
    And match response.datosGenerales == '#null'

  Scenario: TC-002-d PUT folio inexistente
    Given path '/api/v1/folios/FOLIO-NO-EXISTE/datos-generales'
    And request datosValidos
    When method PUT
    Then status 404
    And match response.title == 'Folio no encontrado'

  Scenario: TC-002-e GET folio inexistente
    Given path '/api/v1/folios/FOLIO-NO-EXISTE/datos-generales'
    When method GET
    Then status 404

  Scenario: TC-002-f PUT body inválido - campos requeridos ausentes
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request { telefonoContacto: '0991234567' }
    When method PUT
    Then status 400

  Scenario: TC-002-g PUT idempotente - mismos datos dos veces
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request datosValidos
    When method PUT
    Then status 200
    * def versionTras1er = response.version
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request datosValidos
    When method PUT
    Then status 200
    And match response.version == versionTras1er

  Scenario: TC-002-h Version siempre refleja valor persistido
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request datosValidos
    When method PUT
    Then status 200
    * def v1 = response.version
    Given path '/api/v1/folios/' + numeroFolio + '/datos-generales'
    And request ({ ...datosValidos, descripcion: 'Descripcion modificada' })
    When method PUT
    Then status 200
    And match response.version == v1 + 1
