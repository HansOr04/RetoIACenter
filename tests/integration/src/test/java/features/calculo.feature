Feature: HU-007 · Cálculo de prima

  Background:
    * url baseUrl
    * def setup = call read('helpers/folio-listo-para-calcular.feature')
    * def folio = setup.folio
    * def version = setup.version

  Scenario: Cálculo con ubicación válida retorna prima > 0
    Given path 'quotes', folio, 'calculate'
    And header If-Match = version
    When method POST
    Then status 200
    And match response.estadoCotizacion == 'CALCULADA'
    And assert response.primaNeta > 0
    And assert response.primaComercial > 0
    And assert response.primaComercial > response.primaNeta
    And match response.primasPorUbicacion == '#[] #object'
    And match response.primasPorUbicacion[0].calculada == true
    And assert response.primasPorUbicacion[0].total > 0

  Scenario: Cálculo consecutivo es idempotente (mismos valores)
    # Primer cálculo
    Given path 'quotes', folio, 'calculate'
    And header If-Match = version
    When method POST
    Then status 200
    * def primaNeta1 = response.primaNeta
    * def versionNueva = response.version

    # Segundo cálculo (mismos datos, nueva version)
    Given path 'quotes', folio, 'calculate'
    And header If-Match = versionNueva
    When method POST
    Then status 200
    And match response.primaNeta == primaNeta1

  Scenario: Prima comercial = prima neta × factor comercial (1.25)
    Given path 'quotes', folio, 'calculate'
    And header If-Match = version
    When method POST
    Then status 200
    * def esperada = response.primaNeta * 1.25
    * def delta = Math.abs(response.primaComercial - esperada)
    And assert delta < 0.01
