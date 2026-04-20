package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.application.usecase.*;
import com.sofka.cotizador.domain.model.DatosGenerales;
import com.sofka.cotizador.domain.model.Folio;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import com.sofka.cotizador.domain.model.ProgresoCotizacion;
import com.sofka.cotizador.domain.model.SeccionesAplican;
import com.sofka.cotizador.interfaces.rest.dto.*;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class FolioController {

    private static final Logger log = LoggerFactory.getLogger(FolioController.class);
    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    private final CrearFolioUseCase crearFolioUseCase;
    private final ActualizarDatosGeneralesUseCase actualizarDatosGeneralesUseCase;
    private final ConsultarDatosGeneralesUseCase consultarDatosGeneralesUseCase;
    private final ActualizarLayoutUseCase actualizarLayoutUseCase;
    private final ConsultarLayoutUseCase consultarLayoutUseCase;
    private final ConsultarEstadoFolioUseCase consultarEstadoFolioUseCase;

    public FolioController(CrearFolioUseCase crearFolioUseCase,
                           ActualizarDatosGeneralesUseCase actualizarDatosGeneralesUseCase,
                           ConsultarDatosGeneralesUseCase consultarDatosGeneralesUseCase,
                           ActualizarLayoutUseCase actualizarLayoutUseCase,
                           ConsultarLayoutUseCase consultarLayoutUseCase,
                           ConsultarEstadoFolioUseCase consultarEstadoFolioUseCase) {
        this.crearFolioUseCase = crearFolioUseCase;
        this.actualizarDatosGeneralesUseCase = actualizarDatosGeneralesUseCase;
        this.consultarDatosGeneralesUseCase = consultarDatosGeneralesUseCase;
        this.actualizarLayoutUseCase = actualizarLayoutUseCase;
        this.consultarLayoutUseCase = consultarLayoutUseCase;
        this.consultarEstadoFolioUseCase = consultarEstadoFolioUseCase;
    }

    @PostMapping("/folios")
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
        FolioResponse response = toFolioResponse(result.folio());

        HttpStatus status = result.creado() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/quotes/{numeroFolio}/general-info")
    public ResponseEntity<DatosGeneralesResponse> actualizarDatosGenerales(
            @PathVariable String numeroFolio,
            @Valid @RequestBody DatosGeneralesRequest request) {

        log.info("PUT /api/v1/quotes/{}/general-info", numeroFolio);

        DatosGenerales datos = toDomain(request);
        ActualizarDatosGeneralesCommand command = new ActualizarDatosGeneralesCommand(numeroFolio, datos);
        Folio folio = actualizarDatosGeneralesUseCase.ejecutar(command);

        return ResponseEntity.ok(toDatosGeneralesResponse(folio));
    }

    @GetMapping("/quotes/{numeroFolio}/general-info")
    public ResponseEntity<DatosGeneralesResponse> consultarDatosGenerales(
            @PathVariable String numeroFolio) {

        log.info("GET /api/v1/quotes/{}/general-info", numeroFolio);

        ConsultarDatosGeneralesCommand command = new ConsultarDatosGeneralesCommand(numeroFolio);
        Folio folio = consultarDatosGeneralesUseCase.ejecutar(command);

        return ResponseEntity.ok(toDatosGeneralesResponse(folio));
    }

    @PutMapping("/quotes/{numeroFolio}/locations/layout")
    public ResponseEntity<LayoutUbicacionesResponse> actualizarLayout(
            @PathVariable String numeroFolio,
            @Valid @RequestBody LayoutUbicacionesRequest request) {

        log.info("PUT /api/v1/quotes/{}/locations/layout", numeroFolio);

        LayoutUbicaciones layout = new LayoutUbicaciones(
                request.numeroUbicaciones(),
                new SeccionesAplican(
                        request.seccionesAplican().direccion(),
                        request.seccionesAplican().datosTecnicos(),
                        request.seccionesAplican().giroComercial(),
                        request.seccionesAplican().garantias()
                )
        );
        ActualizarLayoutCommand command = new ActualizarLayoutCommand(numeroFolio, layout);
        Folio folio = actualizarLayoutUseCase.ejecutar(command);
        return ResponseEntity.ok(toLayoutResponse(folio));
    }

    @GetMapping("/quotes/{numeroFolio}/locations/layout")
    public ResponseEntity<LayoutUbicacionesResponse> consultarLayout(
            @PathVariable String numeroFolio) {

        log.info("GET /api/v1/quotes/{}/locations/layout", numeroFolio);

        ConsultarLayoutCommand command = new ConsultarLayoutCommand(numeroFolio);
        Folio folio = consultarLayoutUseCase.ejecutar(command);
        return ResponseEntity.ok(toLayoutResponse(folio));
    }

    @GetMapping("/quotes/{numeroFolio}/state")
    public ResponseEntity<EstadoFolioResponse> consultarEstado(
            @PathVariable String numeroFolio) {

        log.info("GET /api/v1/quotes/{}/state", numeroFolio);

        EstadoFolioResult result = consultarEstadoFolioUseCase.ejecutar(
                new ConsultarEstadoFolioCommand(numeroFolio));

        return ResponseEntity.ok(toEstadoFolioResponse(result));
    }

    private EstadoFolioResponse toEstadoFolioResponse(EstadoFolioResult result) {
        Folio folio = result.folio();
        ProgresoCotizacion progreso = result.progreso();

        List<EstadoFolioResponse.AlertaData> alertas = progreso.alertas().stream()
                .map(a -> new EstadoFolioResponse.AlertaData(a.seccion(), a.mensaje()))
                .toList();

        return new EstadoFolioResponse(
                folio.getNumeroFolio(),
                folio.getEstado().name(),
                folio.getVersion(),
                folio.getFechaCreacion().toString(),
                folio.getFechaUltimaActualizacion().toString(),
                progreso.porcentajeProgreso(),
                progreso.esCalculable(),
                alertas,
                progreso.seccionesCompletadas()
        );
    }

    private LayoutUbicacionesResponse toLayoutResponse(Folio folio) {
        LayoutUbicacionesResponse.LayoutUbicacionesData data = folio.getLayoutUbicaciones() != null
                ? new LayoutUbicacionesResponse.LayoutUbicacionesData(
                        folio.getLayoutUbicaciones().numeroUbicaciones(),
                        new LayoutUbicacionesResponse.SeccionesAplicanData(
                                folio.getLayoutUbicaciones().seccionesAplican().direccion(),
                                folio.getLayoutUbicaciones().seccionesAplican().datosTecnicos(),
                                folio.getLayoutUbicaciones().seccionesAplican().giroComercial(),
                                folio.getLayoutUbicaciones().seccionesAplican().garantias()
                        ))
                : null;
        return new LayoutUbicacionesResponse(
                folio.getNumeroFolio(),
                folio.getEstado().name(),
                folio.getVersion(),
                folio.getFechaUltimaActualizacion().toString(),
                data
        );
    }

    private FolioResponse toFolioResponse(Folio folio) {
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

    private DatosGeneralesResponse toDatosGeneralesResponse(Folio folio) {
        DatosGeneralesData data = folio.getDatosGenerales() != null
                ? new DatosGeneralesData(
                        folio.getDatosGenerales().nombreTomador(),
                        folio.getDatosGenerales().rucCedula(),
                        folio.getDatosGenerales().correoElectronico(),
                        folio.getDatosGenerales().telefonoContacto(),
                        folio.getDatosGenerales().tipoInmueble(),
                        folio.getDatosGenerales().usoPrincipal(),
                        folio.getDatosGenerales().anoConstruccion(),
                        folio.getDatosGenerales().numeroPisos(),
                        folio.getDatosGenerales().descripcion())
                : null;

        return new DatosGeneralesResponse(
                folio.getNumeroFolio(),
                folio.getEstado().name(),
                folio.getVersion(),
                folio.getFechaUltimaActualizacion().toString(),
                data
        );
    }

    private DatosGenerales toDomain(DatosGeneralesRequest request) {
        return new DatosGenerales(
                request.nombreTomador(),
                request.rucCedula(),
                request.correoElectronico(),
                request.telefonoContacto(),
                request.tipoInmueble(),
                request.usoPrincipal(),
                request.anoConstruccion(),
                request.numeroPisos(),
                request.descripcion()
        );
    }
}
