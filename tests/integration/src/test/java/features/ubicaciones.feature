Feature: HU-004 · CRUD de ubicaciones

  Background:
    * url baseUrl
    * def setup = call read('helpers/folio-con-layout.feature')
    * def folio = setup.folio
    * def version = setup.version

  Scenario: Registrar ubicación completa retorna VÁLIDO
    Given path 'quotes', folio, 'locations'
    And header If-Match = version
    And request
      """
      {
        nombreUbicacion: 'Bodega Test',
        direccion: 'Av. 6 de Diciembre',
        codigoPostal: '170103',
        estado: 'Pichincha',
        municipio: 'Quito',
        ciudad: 'Quito',
        tipoConstructivo: 'CONCRETO_ARMADO',
        nivel: 3,
        giro: { codigo: 'G-4521', claveIncendio: 'B1' },
        garantias: ['EXTINTORES', 'DETECTORES_HUMO']
      }
      """
    When method PUT
    Then status 200
    And match response.indice == '#number'
    And match response.estadoValidacion == 'VALIDO'
    And match response.alertasBloqueantes == '#[0]'

  Scenario: Ubicación sin giro.claveIncendio queda INCOMPLETA con alerta
    Given path 'quotes', folio, 'locations'
    And header If-Match = version
    And request
      """
      {
        nombreUbicacion: 'Incompleta',
        direccion: 'Av. Colon',
        codigoPostal: '170109',
        estado: 'Pichincha',
        municipio: 'Quito',
        ciudad: 'Quito',
        tipoConstructivo: 'CONCRETO_ARMADO',
        nivel: 2,
        giro: { codigo: 'G-4522' }
      }
      """
    When method PUT
    Then status 200
    And match response.estadoValidacion == 'INCOMPLETO'
    And match response.alertasBloqueantes == '#notnull'
    And match response.alertasBloqueantes[*].codigo contains 'FALTA_CLAVE_INCENDIO'

  Scenario: GET locations retorna array
    Given path 'quotes', folio, 'locations'
    When method GET
    Then status 200
    And match response == '#[] #object'

  Scenario: GET summary retorna conteos
    Given path 'quotes', folio, 'locations/summary'
    When method GET
    Then status 200
    And match response.total == '#number'
    And match response.completas == '#number'
    And match response.incompletas == '#number'
