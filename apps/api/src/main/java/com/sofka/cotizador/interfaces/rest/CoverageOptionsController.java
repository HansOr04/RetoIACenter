package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.application.usecase.ConfigurarOpcionesCoberturaCommand;
import com.sofka.cotizador.application.usecase.ConfigurarOpcionesCoberturaResult;
import com.sofka.cotizador.application.usecase.ConfigurarOpcionesCoberturaUseCase;
import com.sofka.cotizador.application.usecase.ObtenerOpcionesCoberturaCommand;
import com.sofka.cotizador.application.usecase.ObtenerOpcionesCoberturaUseCase;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.interfaces.rest.dto.OpcionesCoberturaRequest;
import com.sofka.cotizador.interfaces.rest.dto.OpcionesCoberturaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotes")
@Tag(name = "Opciones de Cobertura", description = "Selección de coberturas aplicables al folio")
public class CoverageOptionsController {

    private final ConfigurarOpcionesCoberturaUseCase configurarUseCase;
    private final ObtenerOpcionesCoberturaUseCase obtenerUseCase;

    public CoverageOptionsController(ConfigurarOpcionesCoberturaUseCase configurarUseCase,
                                     ObtenerOpcionesCoberturaUseCase obtenerUseCase) {
        this.configurarUseCase = configurarUseCase;
        this.obtenerUseCase = obtenerUseCase;
    }

    @PutMapping("/{folio}/coverage-options")
    @Operation(summary = "Configurar opciones de cobertura", description = "Define qué coberturas aplican para el cálculo de prima")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Coberturas guardadas"),
        @ApiResponse(responseCode = "409", description = "Conflicto de versión (If-Match incorrecto)")
    })
    public ResponseEntity<OpcionesCoberturaResponse> configurar(
            @PathVariable String folio,
            @RequestHeader("If-Match") String ifMatch,
            @RequestBody OpcionesCoberturaRequest request) {

        int version = Integer.parseInt(ifMatch.replace("\"", ""));

        OpcionesCobertura opciones = new OpcionesCobertura(
                Boolean.TRUE.equals(request.incendioEdificios()),
                Boolean.TRUE.equals(request.incendioContenidos()),
                Boolean.TRUE.equals(request.extensionCobertura()),
                Boolean.TRUE.equals(request.catTev()),
                Boolean.TRUE.equals(request.catFhm()),
                Boolean.TRUE.equals(request.remocionEscombros()),
                Boolean.TRUE.equals(request.gastosExtraordinarios()),
                Boolean.TRUE.equals(request.perdidaRentas()),
                Boolean.TRUE.equals(request.bi()),
                Boolean.TRUE.equals(request.equipoElectronico()),
                Boolean.TRUE.equals(request.robo()),
                Boolean.TRUE.equals(request.dineroValores()),
                Boolean.TRUE.equals(request.vidrios()),
                Boolean.TRUE.equals(request.anunciosLuminosos())
        );

        ConfigurarOpcionesCoberturaResult result =
                configurarUseCase.ejecutar(new ConfigurarOpcionesCoberturaCommand(folio, version, opciones));

        Cotizacion cotizacion = result.cotizacion();
        OpcionesCoberturaResponse response = new OpcionesCoberturaResponse(
                opciones.incendioEdificios(), opciones.incendioContenidos(),
                opciones.extensionCobertura(), opciones.catTev(), opciones.catFhm(),
                opciones.remocionEscombros(), opciones.gastosExtraordinarios(),
                opciones.perdidaRentas(), opciones.bi(), opciones.equipoElectronico(),
                opciones.robo(), opciones.dineroValores(), opciones.vidrios(),
                opciones.anunciosLuminosos(), result.warnings(),
                cotizacion.getVersion(),
                cotizacion.getFechaUltimaActualizacion().toString()
        );

        return ResponseEntity.ok()
                .header("ETag", String.valueOf(cotizacion.getVersion()))
                .body(response);
    }

    @GetMapping("/{folio}/coverage-options")
    @Operation(summary = "Consultar opciones de cobertura", description = "Retorna las coberturas actualmente seleccionadas para el folio")
    @ApiResponse(responseCode = "200", description = "Coberturas del folio")
    public ResponseEntity<OpcionesCoberturaResponse> obtener(@PathVariable String folio) {
        ObtenerOpcionesCoberturaUseCase.Result result =
                obtenerUseCase.ejecutar(new ObtenerOpcionesCoberturaCommand(folio));

        OpcionesCobertura opciones = result.opciones();
        OpcionesCoberturaResponse response = new OpcionesCoberturaResponse(
                opciones.incendioEdificios(), opciones.incendioContenidos(),
                opciones.extensionCobertura(), opciones.catTev(), opciones.catFhm(),
                opciones.remocionEscombros(), opciones.gastosExtraordinarios(),
                opciones.perdidaRentas(), opciones.bi(), opciones.equipoElectronico(),
                opciones.robo(), opciones.dineroValores(), opciones.vidrios(),
                opciones.anunciosLuminosos(), List.of(), result.version(), null
        );

        return ResponseEntity.ok()
                .header("ETag", String.valueOf(result.version()))
                .body(response);
    }
}
