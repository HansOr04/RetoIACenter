package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ActualizarLayoutUseCase {

    private static final Logger log = LoggerFactory.getLogger(ActualizarLayoutUseCase.class);

    private final FolioRepository repository;

    public ActualizarLayoutUseCase(FolioRepository repository) {
        this.repository = repository;
    }

    public Folio ejecutar(ActualizarLayoutCommand command) {
        log.info("Actualizando layout de folio {}", command.numeroFolio());
        Folio folio = repository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));
        Folio actualizado = folio.actualizarLayoutUbicaciones(command.layout());
        Folio persistido = repository.save(actualizado);
        log.info("Layout actualizado para folio {}, version={}", persistido.getNumeroFolio(), persistido.getVersion());
        return persistido;
    }
}
