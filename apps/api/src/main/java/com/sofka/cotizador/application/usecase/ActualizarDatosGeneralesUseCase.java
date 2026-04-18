package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ActualizarDatosGeneralesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ActualizarDatosGeneralesUseCase.class);

    private final FolioRepository repository;

    public ActualizarDatosGeneralesUseCase(FolioRepository repository) {
        this.repository = repository;
    }

    public Folio ejecutar(ActualizarDatosGeneralesCommand command) {
        log.info("Actualizando datos generales folio={}", command.numeroFolio());

        Folio folio = repository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));

        Folio folioActualizado = folio.actualizarDatosGenerales(command.datosGenerales());
        Folio persistido = repository.save(folioActualizado);

        log.info("Datos generales actualizados folio={} version={}", persistido.getNumeroFolio(), persistido.getVersion());
        return persistido;
    }
}
