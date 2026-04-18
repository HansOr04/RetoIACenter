package com.sofka.cotizador.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

public class DatosCotizacion {

    private List<UbicacionJson> ubicaciones = new ArrayList<>();

    public List<UbicacionJson> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<UbicacionJson> ubicaciones) {
        this.ubicaciones = ubicaciones != null ? ubicaciones : new ArrayList<>();
    }
}
