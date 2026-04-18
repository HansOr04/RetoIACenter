package com.sofka.cotizador.domain.model.ubicacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ubicacion {

    private final int indice;
    private final String nombreUbicacion;
    private final String direccion;
    private final String codigoPostal;
    private final String estado;
    private final String municipio;
    private final String colonia;
    private final String ciudad;
    private final String tipoConstructivo;
    private final Integer nivel;
    private final Integer anioConstruccion;
    private final Giro giro;
    private final List<String> garantias;
    private final ZonaCatastrofica zonaCatastrofica;
    private final EstadoValidacionUbicacion estadoValidacion;
    private final List<AlertaBloqueante> alertasBloqueantes;

    private Ubicacion(Builder builder) {
        this.indice = builder.indice;
        this.nombreUbicacion = builder.nombreUbicacion;
        this.direccion = builder.direccion;
        this.codigoPostal = builder.codigoPostal;
        this.estado = builder.estado;
        this.municipio = builder.municipio;
        this.colonia = builder.colonia;
        this.ciudad = builder.ciudad;
        this.tipoConstructivo = builder.tipoConstructivo;
        this.nivel = builder.nivel;
        this.anioConstruccion = builder.anioConstruccion;
        this.giro = builder.giro;
        this.garantias = builder.garantias != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.garantias))
                : Collections.emptyList();
        this.zonaCatastrofica = builder.zonaCatastrofica;
        this.estadoValidacion = builder.estadoValidacion != null
                ? builder.estadoValidacion
                : EstadoValidacionUbicacion.INCOMPLETO;
        this.alertasBloqueantes = builder.alertasBloqueantes != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.alertasBloqueantes))
                : Collections.emptyList();
    }

    public int getIndice() { return indice; }
    public String getNombreUbicacion() { return nombreUbicacion; }
    public String getDireccion() { return direccion; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getEstado() { return estado; }
    public String getMunicipio() { return municipio; }
    public String getColonia() { return colonia; }
    public String getCiudad() { return ciudad; }
    public String getTipoConstructivo() { return tipoConstructivo; }
    public Integer getNivel() { return nivel; }
    public Integer getAnioConstruccion() { return anioConstruccion; }
    public Giro getGiro() { return giro; }
    public List<String> getGarantias() { return garantias; }
    public ZonaCatastrofica getZonaCatastrofica() { return zonaCatastrofica; }
    public EstadoValidacionUbicacion getEstadoValidacion() { return estadoValidacion; }
    public List<AlertaBloqueante> getAlertasBloqueantes() { return alertasBloqueantes; }

    public boolean esCalculable() {
        return alertasBloqueantes.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .indice(this.indice)
                .nombreUbicacion(this.nombreUbicacion)
                .direccion(this.direccion)
                .codigoPostal(this.codigoPostal)
                .estado(this.estado)
                .municipio(this.municipio)
                .colonia(this.colonia)
                .ciudad(this.ciudad)
                .tipoConstructivo(this.tipoConstructivo)
                .nivel(this.nivel)
                .anioConstruccion(this.anioConstruccion)
                .giro(this.giro)
                .garantias(new ArrayList<>(this.garantias))
                .zonaCatastrofica(this.zonaCatastrofica)
                .estadoValidacion(this.estadoValidacion)
                .alertasBloqueantes(new ArrayList<>(this.alertasBloqueantes));
    }

    public static class Builder {
        private int indice;
        private String nombreUbicacion;
        private String direccion;
        private String codigoPostal;
        private String estado;
        private String municipio;
        private String colonia;
        private String ciudad;
        private String tipoConstructivo;
        private Integer nivel;
        private Integer anioConstruccion;
        private Giro giro;
        private List<String> garantias;
        private ZonaCatastrofica zonaCatastrofica;
        private EstadoValidacionUbicacion estadoValidacion;
        private List<AlertaBloqueante> alertasBloqueantes;

        public Builder indice(int indice) { this.indice = indice; return this; }
        public Builder nombreUbicacion(String nombreUbicacion) { this.nombreUbicacion = nombreUbicacion; return this; }
        public Builder direccion(String direccion) { this.direccion = direccion; return this; }
        public Builder codigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; return this; }
        public Builder estado(String estado) { this.estado = estado; return this; }
        public Builder municipio(String municipio) { this.municipio = municipio; return this; }
        public Builder colonia(String colonia) { this.colonia = colonia; return this; }
        public Builder ciudad(String ciudad) { this.ciudad = ciudad; return this; }
        public Builder tipoConstructivo(String tipoConstructivo) { this.tipoConstructivo = tipoConstructivo; return this; }
        public Builder nivel(Integer nivel) { this.nivel = nivel; return this; }
        public Builder anioConstruccion(Integer anioConstruccion) { this.anioConstruccion = anioConstruccion; return this; }
        public Builder giro(Giro giro) { this.giro = giro; return this; }
        public Builder garantias(List<String> garantias) { this.garantias = garantias; return this; }
        public Builder zonaCatastrofica(ZonaCatastrofica zonaCatastrofica) { this.zonaCatastrofica = zonaCatastrofica; return this; }
        public Builder estadoValidacion(EstadoValidacionUbicacion estadoValidacion) { this.estadoValidacion = estadoValidacion; return this; }
        public Builder alertasBloqueantes(List<AlertaBloqueante> alertasBloqueantes) { this.alertasBloqueantes = alertasBloqueantes; return this; }

        public Ubicacion build() {
            return new Ubicacion(this);
        }
    }
}
