package com.sofka.cotizador.domain.port;

import com.sofka.cotizador.domain.model.Cotizacion;

import java.util.Optional;

public interface CotizacionRepository {

    Optional<Cotizacion> findByNumeroFolio(String numeroFolio);

    Cotizacion save(Cotizacion cotizacion);
}
