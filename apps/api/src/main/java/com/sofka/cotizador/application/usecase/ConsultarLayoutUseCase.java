package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsultarLayoutUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsultarLayoutUseCase.class);

    private final FolioRepository repository;

    public ConsultarLayoutUseCase(FolioRepository repository) {
        this.repository = repository;
    }

    public Folio ejecutar(ConsultarLayoutCommand command) {
        log.info("Consultando layout de folio {}", command.numeroFolio());
        return repository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));
    }
}
