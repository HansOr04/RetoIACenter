package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.model.EstadoCotizacion;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.port.CoreFolioService;
import com.sofka.cotizador.domain.port.FolioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

// HU-001 — crear folio con idempotencia
@Service
public class CrearFolioUseCase {

    private final FolioRepository folioRepository;
    private final CoreFolioService coreFolioService;

    public CrearFolioUseCase(FolioRepository folioRepository, CoreFolioService coreFolioService) {
        this.folioRepository = folioRepository;
        this.coreFolioService = coreFolioService;
    }

    public CrearFolioResult ejecutar(CrearFolioCommand command) {
        return folioRepository.findByIdempotencyKey(command.idempotencyKey())
                .map(existing -> new CrearFolioResult(existing, false))
                .orElseGet(() -> crearNuevo(command));
    }

    private CrearFolioResult crearNuevo(CrearFolioCommand command) {
        String numeroFolio = coreFolioService.generarNumeroFolio(command.codigoAgente());
        LocalDateTime ahora = LocalDateTime.now();

        Folio folio = Folio.builder()
                .id(UUID.randomUUID().toString())
                .numeroFolio(numeroFolio)
                .idempotencyKey(command.idempotencyKey())
                .estado(EstadoCotizacion.BORRADOR)
                .tipoNegocio(command.tipoNegocio())
                .codigoAgente(command.codigoAgente())
                .version(1)
                .fechaCreacion(ahora)
                .fechaUltimaActualizacion(ahora)
                .build();

        Folio guardado = folioRepository.save(folio);
        return new CrearFolioResult(guardado, true);
    }
}
