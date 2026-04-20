Feature: HU-006 · Opciones de cobertura

  Background:
    * url baseUrl
    * def setup = call read('helpers/folio-con-layout.feature')
    * def folio = setup.folio
    * def version = setup.version

  Scenario: Configurar coberturas válidas
    Given path 'quotes', folio, 'coverage-options'
    And header If-Match = version
    And request
      """
      {
        incendioEdificios: true,
        incendioContenidos: true,
        catTev: true,
        remocionEscombros: true,
        bi: true,
        catFhm: false,
        extensionCobertura: false,
        gastosExtraordinarios: false,
        perdidaRentas: false,
        equipoElectronico: false,
        robo: false,
        dineroValores: false,
        vidrios: false,
        anunciosLuminosos: false
      }
      """
    When method PUT
    Then status 200
    And match response.version == '#number'
    And match response.incendioEdificios == true

  Scenario: Ninguna cobertura activa retorna 422
    Given path 'quotes', folio, 'coverage-options'
    And header If-Match = version
    And request
      """
      {
        incendioEdificios: false, incendioContenidos: false, catTev: false,
        catFhm: false, extensionCobertura: false, remocionEscombros: false,
        gastosExtraordinarios: false, perdidaRentas: false, bi: false,
        equipoElectronico: false, robo: false, dineroValores: false,
        vidrios: false, anunciosLuminosos: false
      }
      """
    When method PUT
    Then status 422

  Scenario: GET coverage-options retorna estado actual
    Given path 'quotes', folio, 'coverage-options'
    When method GET
    Then status 200
