package com.sofka.cotizador.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CotizacionJpaRepository extends JpaRepository<CotizacionJpaEntity, String> {
}
