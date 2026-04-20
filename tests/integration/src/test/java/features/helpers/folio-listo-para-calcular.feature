Feature: Helper · Folio con ubicación válida y coberturas listo para calcular

  Background:
    * url baseUrl
    * def setup = call read('folio-con-layout.feature')
    * def folio = setup.folio
    * def version = setup.version

  Scenario: Agregar ubicación + cobertura
    Given path 'quotes', folio, 'locations'
    And header If-Match = version
    And request
      """
      {
        nombreUbicacion: 'Bodega',
        direccion: 'Test',
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
    * def version = response.version

    Given path 'quotes', folio, 'coverage-options'
    And header If-Match = version
    And request { incendioEdificios: true, incendioContenidos: true, catTev: true, remocionEscombros: true, bi: true, catFhm: false, extensionCobertura: true, gastosExtraordinarios: false, perdidaRentas: true, equipoElectronico: false, robo: false, dineroValores: false, vidrios: false, anunciosLuminosos: false }
    When method PUT
    Then status 200
    * def version = response.version
