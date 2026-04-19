package com.sofka.cotizador.karate;

import com.intuit.karate.junit5.Karate;

// Runner de integración Karate (requiere API levantada en localhost:8080)
class KarateRunnerIT {

    @Karate.Test
    Karate testHU001() {
        return Karate.run("karate/HU001_crear_folio").relativeTo(getClass());
    }

    @Karate.Test
    Karate testHU006() {
        return Karate.run("karate/HU006_coverage_options").relativeTo(getClass());
    }

    @Karate.Test
    Karate testHU007() {
        return Karate.run("karate/HU007_calculo_prima").relativeTo(getClass());
    }
}
