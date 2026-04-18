package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.application.usecase.EditarUbicacionCommand;
import com.sofka.cotizador.application.usecase.EditarUbicacionPuntualUseCase;
import com.sofka.cotizador.application.usecase.ListarUbicacionesUseCase;
import com.sofka.cotizador.application.usecase.ObtenerResumenUbicacionesUseCase;
import com.sofka.cotizador.application.usecase.RegistrarUbicacionCommand;
import com.sofka.cotizador.application.usecase.RegistrarUbicacionUseCase;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;
import com.sofka.cotizador.interfaces.rest.dto.GiroData;
import com.sofka.cotizador.interfaces.rest.dto.ResumenUbicacionesResponse;
import com.sofka.cotizador.interfaces.rest.dto.UbicacionPatchRequest;
import com.sofka.cotizador.interfaces.rest.dto.UbicacionRequest;
import com.sofka.cotizador.interfaces.rest.dto.UbicacionResponse;
import com.sofka.cotizador.interfaces.rest.dto.ZonaCatastroficaData;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotes")
public class LocationsController {

    private static final Logger log = LoggerFactory.getLogger(LocationsController.class);

    private final RegistrarUbicacionUseCase registrarUbicacionUseCase;
    private final ListarUbicacionesUseCase listarUbicacionesUseCase;
    private final ObtenerResumenUbicacionesUseCase obtenerResumenUseCase;
    private final EditarUbicacionPuntualUseCase editarUbicacionPuntualUseCase;

    public LocationsController(RegistrarUbicacionUseCase registrarUbicacionUseCase,
                               ListarUbicacionesUseCase listarUbicacionesUseCase,
                               ObtenerResumenUbicacionesUseCase obtenerResumenUseCase,
                               EditarUbicacionPuntualUseCase editarUbicacionPuntualUseCase) {
        this.registrarUbicacionUseCase = registrarUbicacionUseCase;
        this.listarUbicacionesUseCase = listarUbicacionesUseCase;
        this.obtenerResumenUseCase = obtenerResumenUseCase;
        this.editarUbicacionPuntualUseCase = editarUbicacionPuntualUseCase;
    }

    @PutMapping("/{folio}/locations")
    public ResponseEntity<UbicacionResponse> registrarUbicacion(
            @PathVariable String folio,
            @RequestHeader("If-Match") String ifMatch,
            @Valid @RequestBody UbicacionRequest request) {

        log.info("PUT /api/v1/quotes/{}/locations", folio);

        RegistrarUbicacionCommand command = toRegistrarCommand(folio, request);
        Cotizacion cotizacion = registrarUbicacionUseCase.ejecutar(command);

        int nuevoIndice = cotizacion.getUbicaciones().size() - 1;
        Ubicacion ubicacion = cotizacion.getUbicaciones().get(nuevoIndice);
        UbicacionResponse response = toUbicacionResponse(ubicacion, cotizacion);

        return ResponseEntity.ok()
                .header("ETag", String.valueOf(cotizacion.getVersion()))
                .body(response);
    }

    @GetMapping("/{folio}/locations")
    public ResponseEntity<List<UbicacionResponse>> listarUbicaciones(
            @PathVariable String folio) {

        log.info("GET /api/v1/quotes/{}/locations", folio);

        List<Ubicacion> ubicaciones = listarUbicacionesUseCase.ejecutar(folio);
        List<UbicacionResponse> responses = ubicaciones.stream()
                .map(u -> toUbicacionResponseSinVersion(u))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{folio}/locations/summary")
    public ResponseEntity<ResumenUbicacionesResponse> obtenerResumen(
            @PathVariable String folio) {

        log.info("GET /api/v1/quotes/{}/locations/summary", folio);

        ObtenerResumenUbicacionesUseCase.ResumenUbicaciones resumen = obtenerResumenUseCase.ejecutar(folio);

        List<ResumenUbicacionesResponse.DetalleIncompleta> detalles = resumen.detalleIncompletas().stream()
                .map(d -> new ResumenUbicacionesResponse.DetalleIncompleta(
                        d.indice(), d.nombreUbicacion(), d.alertas()))
                .toList();

        ResumenUbicacionesResponse response = new ResumenUbicacionesResponse(
                resumen.total(),
                resumen.completas(),
                resumen.incompletas(),
                resumen.calculables(),
                resumen.indicesIncompletos(),
                detalles
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{folio}/locations/{indice}")
    public ResponseEntity<UbicacionResponse> editarUbicacion(
            @PathVariable String folio,
            @PathVariable int indice,
            @RequestHeader("If-Match") String ifMatch,
            @RequestBody UbicacionPatchRequest request) {

        log.info("PATCH /api/v1/quotes/{}/locations/{}", folio, indice);

        int versionEsperada = parseVersion(ifMatch);

        EditarUbicacionCommand.GiroCommand giroCommand = request.giro() != null
                ? new EditarUbicacionCommand.GiroCommand(
                        request.giro().codigo(),
                        request.giro().descripcion(),
                        request.giro().claveIncendio())
                : null;

        EditarUbicacionCommand command = new EditarUbicacionCommand(
                folio, indice, versionEsperada,
                request.nombreUbicacion(),
                request.direccion(),
                request.codigoPostal(),
                request.tipoConstructivo(),
                request.nivel(),
                request.anioConstruccion(),
                giroCommand,
                request.garantias()
        );

        Cotizacion cotizacion = editarUbicacionPuntualUseCase.ejecutar(command);

        Ubicacion ubicacion = cotizacion.getUbicaciones().stream()
                .filter(u -> u.getIndice() == indice)
                .findFirst()
                .orElseThrow();

        UbicacionResponse response = toUbicacionResponse(ubicacion, cotizacion);

        return ResponseEntity.ok()
                .header("ETag", String.valueOf(cotizacion.getVersion()))
                .body(response);
    }

    private int parseVersion(String ifMatch) {
        try {
            return Integer.parseInt(ifMatch.replace("\"", "").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El header If-Match debe contener un número de versión válido");
        }
    }

    private RegistrarUbicacionCommand toRegistrarCommand(String folio, UbicacionRequest request) {
        RegistrarUbicacionCommand.GiroCommand giroCommand = request.giro() != null
                ? new RegistrarUbicacionCommand.GiroCommand(
                        request.giro().codigo(),
                        request.giro().descripcion(),
                        request.giro().claveIncendio())
                : null;

        return new RegistrarUbicacionCommand(
                folio,
                request.nombreUbicacion(),
                request.direccion(),
                request.codigoPostal(),
                request.estado(),
                request.municipio(),
                request.colonia(),
                request.ciudad(),
                request.tipoConstructivo(),
                request.nivel(),
                request.anioConstruccion(),
                giroCommand,
                request.garantias()
        );
    }

    private UbicacionResponse toUbicacionResponse(Ubicacion ubicacion, Cotizacion cotizacion) {
        ZonaCatastroficaData zonaData = ubicacion.getZonaCatastrofica() != null
                ? new ZonaCatastroficaData(
                        ubicacion.getZonaCatastrofica().zonaTev(),
                        ubicacion.getZonaCatastrofica().zonaFhm())
                : null;

        GiroData giroData = ubicacion.getGiro() != null
                ? new GiroData(
                        ubicacion.getGiro().codigo(),
                        ubicacion.getGiro().descripcion(),
                        ubicacion.getGiro().claveIncendio())
                : null;

        return new UbicacionResponse(
                ubicacion.getIndice(),
                ubicacion.getNombreUbicacion(),
                ubicacion.getDireccion(),
                ubicacion.getCodigoPostal(),
                zonaData,
                ubicacion.getTipoConstructivo(),
                ubicacion.getNivel(),
                ubicacion.getAnioConstruccion(),
                giroData,
                ubicacion.getGarantias(),
                ubicacion.getAlertasBloqueantes(),
                ubicacion.getEstadoValidacion() != null ? ubicacion.getEstadoValidacion().name() : null,
                cotizacion.getVersion(),
                cotizacion.getFechaUltimaActualizacion().toString()
        );
    }

    private UbicacionResponse toUbicacionResponseSinVersion(Ubicacion ubicacion) {
        ZonaCatastroficaData zonaData = ubicacion.getZonaCatastrofica() != null
                ? new ZonaCatastroficaData(
                        ubicacion.getZonaCatastrofica().zonaTev(),
                        ubicacion.getZonaCatastrofica().zonaFhm())
                : null;

        GiroData giroData = ubicacion.getGiro() != null
                ? new GiroData(
                        ubicacion.getGiro().codigo(),
                        ubicacion.getGiro().descripcion(),
                        ubicacion.getGiro().claveIncendio())
                : null;

        return new UbicacionResponse(
                ubicacion.getIndice(),
                ubicacion.getNombreUbicacion(),
                ubicacion.getDireccion(),
                ubicacion.getCodigoPostal(),
                zonaData,
                ubicacion.getTipoConstructivo(),
                ubicacion.getNivel(),
                ubicacion.getAnioConstruccion(),
                giroData,
                ubicacion.getGarantias(),
                ubicacion.getAlertasBloqueantes(),
                ubicacion.getEstadoValidacion() != null ? ubicacion.getEstadoValidacion().name() : null,
                0,
                null
        );
    }
}
