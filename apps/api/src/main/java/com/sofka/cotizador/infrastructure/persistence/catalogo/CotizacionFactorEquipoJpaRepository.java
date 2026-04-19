package com.sofka.cotizador.infrastructure.persistence.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CotizacionFactorEquipoJpaRepository
        extends JpaRepository<CotizacionFactorEquipoEntity, Integer> {

    Optional<CotizacionFactorEquipoEntity> findByClaseAndNivel(String clase, Integer nivel);
}
