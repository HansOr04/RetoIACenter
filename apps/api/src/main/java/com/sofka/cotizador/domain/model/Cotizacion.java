package com.sofka.cotizador.domain.model;

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

    public Cotizacion(String numeroFolio, List<Ubicacion> ubicaciones, int version,
                      LocalDateTime fechaUltimaActualizacion) {
        this.numeroFolio = numeroFolio;
        this.ubicaciones = ubicaciones != null
                ? Collections.unmodifiableList(new ArrayList<>(ubicaciones))
                : Collections.emptyList();
        this.version = version;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public String getNumeroFolio() { return numeroFolio; }

    public List<Ubicacion> getUbicaciones() { return ubicaciones; }

    public int getVersion() { return version; }

    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }

    public Cotizacion agregarUbicacion(Ubicacion ubicacion) {
        List<Ubicacion> nuevas = new ArrayList<>(this.ubicaciones);
        nuevas.add(ubicacion);
        return new Cotizacion(this.numeroFolio, nuevas, this.version + 1, LocalDateTime.now());
    }

    public Cotizacion editarUbicacion(int indice, UnaryOperator<Ubicacion> patch) {
        List<Ubicacion> nuevas = new ArrayList<>(this.ubicaciones);
        Ubicacion original = nuevas.get(indice);
        Ubicacion actualizada = patch.apply(original);
        nuevas.set(indice, actualizada);
        return new Cotizacion(this.numeroFolio, nuevas, this.version + 1, LocalDateTime.now());
    }

    public boolean capacidadExcedida(int maxUbicaciones) {
        return ubicaciones.size() >= maxUbicaciones;
    }
}
