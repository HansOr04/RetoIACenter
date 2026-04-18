package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsultarEstadoFolioUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsultarEstadoFolioUseCase.class);

    private final FolioRepository repository;

    public ConsultarEstadoFolioUseCase(FolioRepository repository) {
        this.repository = repository;
    }

    public EstadoFolioResult ejecutar(ConsultarEstadoFolioCommand command) {
        log.info("Consultando estado de folio {}", command.numeroFolio());
        Folio folio = repository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));
        return new EstadoFolioResult(folio, folio.calcularProgreso());
    }
}
