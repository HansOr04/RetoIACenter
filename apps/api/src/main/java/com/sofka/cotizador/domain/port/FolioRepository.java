package com.sofka.cotizador.domain.port;

import com.sofka.cotizador.domain.model.Folio;

import java.util.Optional;

// HU-001 — contrato de persistencia del agregado Folio; sin anotaciones de framework
public interface FolioRepository {

    Folio save(Folio folio);

    Optional<Folio> findByIdempotencyKey(String idempotencyKey);

    Optional<Folio> findByNumeroFolio(String numeroFolio);
}
