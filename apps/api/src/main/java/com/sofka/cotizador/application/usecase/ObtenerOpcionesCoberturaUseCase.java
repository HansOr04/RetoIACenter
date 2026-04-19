package com.sofka.cotizador.application.usecase;

import com.sofka.cotizador.domain.exception.FolioNotFoundException;
import com.sofka.cotizador.domain.model.Cotizacion;
import com.sofka.cotizador.domain.model.OpcionesCobertura;
import com.sofka.cotizador.domain.port.CotizacionRepository;
import org.springframework.stereotype.Service;

@Service
public class ObtenerOpcionesCoberturaUseCase {

    private final CotizacionRepository cotizacionRepository;

    public ObtenerOpcionesCoberturaUseCase(CotizacionRepository cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    public record Result(OpcionesCobertura opciones, int version) {}

    public Result ejecutar(ObtenerOpcionesCoberturaCommand command) {
        Cotizacion cotizacion = cotizacionRepository.findByNumeroFolio(command.numeroFolio())
                .orElseThrow(() -> new FolioNotFoundException(command.numeroFolio()));

        OpcionesCobertura opciones = cotizacion.getOpcionesCobertura() != null
                ? cotizacion.getOpcionesCobertura()
                : OpcionesCobertura.defaults();

        return new Result(opciones, cotizacion.getVersion());
    }
}
