package com.sofka.cotizador.karate;

import com.intuit.karate.junit5.Karate;

// HU-001 — runner de integración Karate (requiere API levantada en localhost:8080)
class KarateRunnerIT {

    @Karate.Test
    Karate testHU001() {
        return Karate.run("karate/HU001_crear_folio").relativeTo(getClass());
    }
}
