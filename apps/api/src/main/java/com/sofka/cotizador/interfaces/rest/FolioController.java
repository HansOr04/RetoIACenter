package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.application.usecase.CrearFolioCommand;
import com.sofka.cotizador.application.usecase.CrearFolioResult;
import com.sofka.cotizador.application.usecase.CrearFolioUseCase;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.interfaces.rest.dto.CrearFolioRequest;
import com.sofka.cotizador.interfaces.rest.dto.FolioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// HU-001 — endpoint de creación de folio
@RestController
@RequestMapping("/api/v1/folios")
public class FolioController {

    private static final Logger log = LoggerFactory.getLogger(FolioController.class);
    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    private final CrearFolioUseCase crearFolioUseCase;

    public FolioController(CrearFolioUseCase crearFolioUseCase) {
        this.crearFolioUseCase = crearFolioUseCase;
    }

    @PostMapping
    public ResponseEntity<FolioResponse> crearFolio(
            @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
            @RequestBody(required = false) CrearFolioRequest request) {

        log.info("POST /api/v1/folios idempotencyKey={}", idempotencyKey);

        CrearFolioCommand command = new CrearFolioCommand(
                idempotencyKey,
                request != null ? request.tipoNegocio() : null,
                request != null ? request.codigoAgente() : null
        );

        CrearFolioResult result = crearFolioUseCase.ejecutar(command);
        FolioResponse response = toResponse(result.folio());

        HttpStatus status = result.creado() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    private FolioResponse toResponse(Folio folio) {
        return new FolioResponse(
                folio.getNumeroFolio(),
                folio.getEstado().name(),
                folio.getTipoNegocio(),
                folio.getCodigoAgente(),
                folio.getVersion(),
                folio.getFechaCreacion(),
                folio.getFechaUltimaActualizacion()
        );
    }
}
