package com.sofka.cotizador.domain.model;

import com.sofka.cotizador.domain.model.calculo.ResultadoCalculo;
import com.sofka.cotizador.domain.model.ubicacion.Ubicacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class Cotizacion {

    private final String numeroFolio;
    private final List<Ubicacion> ubicaciones;
    private final int version;
    private final LocalDateTime fechaUltimaActualizacion;
    private final OpcionesCobertura opcionesCobertura;
    private final ResultadoCalculo resultadoCalculo;
    private final EstadoCotizacion estado;

    // Full 7-param constructor
    public Cotizacion(String numeroFolio, List<Ubicacion> ubicaciones, int version,
                      LocalDateTime fechaUltimaActualizacion,
                      OpcionesCobertura opcionesCobertura,
                      ResultadoCalculo resultadoCalculo,
                      EstadoCotizacion estado) {
        this.numeroFolio = numeroFolio;
        this.ubicaciones = ubicaciones != null
                ? Collections.unmodifiableList(new ArrayList<>(ubicaciones))
                : Collections.emptyList();
        this.version = version;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
        this.opcionesCobertura = opcionesCobertura;
        this.resultadoCalculo = resultadoCalculo;
        this.estado = estado;
    }

    // Backward-compatible 4-param constructor — delegates with nulls
    public Cotizacion(String numeroFolio, List<Ubicacion> ubicaciones, int version,
                      LocalDateTime fechaUltimaActualizacion) {
        this(numeroFolio, ubicaciones, version, fechaUltimaActualizacion, null, null, null);
    }

    public String getNumeroFolio() { return numeroFolio; }

    public List<Ubicacion> getUbicaciones() { return ubicaciones; }

    public int getVersion() { return version; }

    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }

    public OpcionesCobertura getOpcionesCobertura() { return opcionesCobertura; }

    public ResultadoCalculo getResultadoCalculo() { return resultadoCalculo; }

    public EstadoCotizacion getEstado() { return estado; }

    public Cotizacion agregarUbicacion(Ubicacion ubicacion) {
        List<Ubicacion> nuevas = new ArrayList<>(this.ubicaciones);
        nuevas.add(ubicacion);
        return new Cotizacion(this.numeroFolio, nuevas, this.version + 1, LocalDateTime.now(),
                this.opcionesCobertura, this.resultadoCalculo, this.estado);
    }

    public Cotizacion editarUbicacion(int indice, UnaryOperator<Ubicacion> patch) {
        List<Ubicacion> nuevas = new ArrayList<>(this.ubicaciones);
        Ubicacion original = nuevas.get(indice);
        Ubicacion actualizada = patch.apply(original);
        nuevas.set(indice, actualizada);
        return new Cotizacion(this.numeroFolio, nuevas, this.version + 1, LocalDateTime.now(),
                this.opcionesCobertura, this.resultadoCalculo, this.estado);
    }

    public boolean capacidadExcedida(int maxUbicaciones) {
        return ubicaciones.size() >= maxUbicaciones;
    }

    public Cotizacion actualizarOpcionesCobertura(OpcionesCobertura opciones) {
        return new Cotizacion(this.numeroFolio, this.ubicaciones, this.version + 1,
                LocalDateTime.now(), opciones, this.resultadoCalculo, this.estado);
    }

    public Cotizacion actualizarResultadoCalculo(ResultadoCalculo resultado) {
        return new Cotizacion(this.numeroFolio, this.ubicaciones, this.version + 1,
                LocalDateTime.now(), this.opcionesCobertura, resultado, EstadoCotizacion.CALCULADO);
    }

    public Cotizacion actualizarEstado(EstadoCotizacion nuevoEstado) {
        return new Cotizacion(this.numeroFolio, this.ubicaciones, this.version + 1,
                LocalDateTime.now(), this.opcionesCobertura, this.resultadoCalculo, nuevoEstado);
    }
}
