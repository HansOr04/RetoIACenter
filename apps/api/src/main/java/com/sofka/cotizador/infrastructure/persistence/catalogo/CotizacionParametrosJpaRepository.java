package com.sofka.cotizador.infrastructure.persistence.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionParametrosJpaRepository
        extends JpaRepository<CotizacionParametrosEntity, Integer> {}
