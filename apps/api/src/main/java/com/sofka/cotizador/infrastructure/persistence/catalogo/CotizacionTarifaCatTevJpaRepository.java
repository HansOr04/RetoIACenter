package com.sofka.cotizador.infrastructure.persistence.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CotizacionTarifaCatTevJpaRepository
        extends JpaRepository<CotizacionTarifaCatTevEntity, Integer> {

    Optional<CotizacionTarifaCatTevEntity> findByZonaTev(String zonaTev);
}
