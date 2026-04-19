package com.sofka.cotizador.interfaces.rest;

import com.sofka.cotizador.application.usecase.EjecutarCalculoCommand;
import com.sofka.cotizador.application.usecase.EjecutarCalculoResult;
import com.sofka.cotizador.application.usecase.EjecutarCalculoUseCase;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.calculo.DesgloseComponentes;
import com.sofka.cotizador.domain.model.calculo.PrimaPorUbicacion;
import com.sofka.cotizador.domain.model.calculo.ResultadoCalculo;
import com.sofka.cotizador.domain.model.ubicacion.AlertaBloqueante;
import com.sofka.cotizador.interfaces.rest.dto.CalculoResponse;
import com.sofka.cotizador.interfaces.rest.dto.DesgloseData;
import com.sofka.cotizador.interfaces.rest.dto.PrimaPorUbicacionData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotes")
public class CalculateController {

    private final EjecutarCalculoUseCase ejecutarCalculoUseCase;

    public CalculateController(EjecutarCalculoUseCase ejecutarCalculoUseCase) {
        this.ejecutarCalculoUseCase = ejecutarCalculoUseCase;
    }

    @PostMapping("/{folio}/calculate")
    public ResponseEntity<CalculoResponse> calcular(
            @PathVariable String folio,
            @RequestHeader("If-Match") String ifMatch) {

        int version = Integer.parseInt(ifMatch.replace("\"", ""));
        EjecutarCalculoResult result =
                ejecutarCalculoUseCase.ejecutar(new EjecutarCalculoCommand(folio, version));

        ResultadoCalculo resultado = result.resultado();
        Cotizacion cotizacion = result.cotizacionActualizada();

        List<PrimaPorUbicacionData> primasData = resultado.primasPorUbicacion().stream()
                .map(this::toPrimaPorUbicacionData)
                .toList();

        String estadoNombre = cotizacion.getEstado() != null
                ? cotizacion.getEstado().name()
                : null;

        CalculoResponse response = new CalculoResponse(
                resultado.primaNeta(),
                resultado.primaComercial(),
                resultado.factorComercial(),
                primasData,
                resultado.fechaCalculo().toString(),
                estadoNombre,
                cotizacion.getVersion()
        );

        return ResponseEntity.ok()
                .header("ETag", String.valueOf(cotizacion.getVersion()))
                .body(response);
    }

    private PrimaPorUbicacionData toPrimaPorUbicacionData(PrimaPorUbicacion p) {
        DesgloseData desgloseData = null;
        if (p.desglose() != null) {
            DesgloseComponentes dc = p.desglose();
            desgloseData = new DesgloseData(
                    dc.incendioEdificios(), dc.incendioContenidos(), dc.extensionCobertura(),
                    dc.catTev(), dc.catFhm(), dc.remocionEscombros(), dc.gastosExtraordinarios(),
                    dc.perdidaRentas(), dc.bi(), dc.equipoElectronico(), dc.robo(),
                    dc.dineroValores(), dc.vidrios(), dc.anunciosLuminosos(), dc.total()
            );
        }

        List<String> alertasMensajes = p.alertas() != null
                ? p.alertas().stream().map(AlertaBloqueante::mensaje).toList()
                : List.of();

        return new PrimaPorUbicacionData(p.indice(), p.calculada(), p.total(), desgloseData, alertasMensajes);
    }
}
