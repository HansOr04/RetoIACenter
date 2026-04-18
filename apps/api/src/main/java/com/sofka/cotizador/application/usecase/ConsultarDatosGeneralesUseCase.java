package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsultarDatosGeneralesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsultarDatosGeneralesUseCase.class);

    private final FolioRepository repository;

    public ConsultarDatosGeneralesUseCase(FolioRepository repository) {
        this.repository = repository;
    }

    public Folio ejecutar(ConsultarDatosGeneralesCommand command) {
        log.info("Consultando datos generales folio={}", command.numeroFolio());

        return repository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));
    }
}
