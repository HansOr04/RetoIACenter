package com.sofka.cotizador.infrastructure.persistence.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CotizacionTarifaCatFhmJpaRepository
        extends JpaRepository<CotizacionTarifaCatFhmEntity, Integer> {

    Optional<CotizacionTarifaCatFhmEntity> findByZonaFhm(String zonaFhm);
}
