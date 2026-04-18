package com.sofka.cotizador.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// HU-001
public interface FolioJpaRepository extends JpaRepository<FolioJpaEntity, String> {

    Optional<FolioJpaEntity> findByIdempotencyKey(String idempotencyKey);

    Optional<FolioJpaEntity> findByNumeroFolio(String numeroFolio);
}
