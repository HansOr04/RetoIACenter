package com.sofka.cotizador.infrastructure.persistence.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CotizacionTarifaIncendioJpaRepository
        extends JpaRepository<CotizacionTarifaIncendioEntity, Integer> {

    Optional<CotizacionTarifaIncendioEntity> findByClaveIncendioAndTipoConstructivo(
            String claveIncendio, String tipoConstructivo);
}
