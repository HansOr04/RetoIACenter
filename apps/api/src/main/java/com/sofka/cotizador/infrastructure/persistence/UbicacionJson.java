package com.sofka.cotizador.infrastructure.persistence;

import java.util.List;

public class UbicacionJson {

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
    private GiroJson giro;
    private List<String> garantias;
    private ZonaCatastroficaJson zonaCatastrofica;
    private String estadoValidacion;
    private List<AlertaBloquenanteJson> alertasBloqueantes;

    public int getIndice() { return indice; }
    public void setIndice(int indice) { this.indice = indice; }

    public String getNombreUbicacion() { return nombreUbicacion; }
    public void setNombreUbicacion(String nombreUbicacion) { this.nombreUbicacion = nombreUbicacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getColonia() { return colonia; }
    public void setColonia(String colonia) { this.colonia = colonia; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTipoConstructivo() { return tipoConstructivo; }
    public void setTipoConstructivo(String tipoConstructivo) { this.tipoConstructivo = tipoConstructivo; }

    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }

    public Integer getAnioConstruccion() { return anioConstruccion; }
    public void setAnioConstruccion(Integer anioConstruccion) { this.anioConstruccion = anioConstruccion; }

    public GiroJson getGiro() { return giro; }
    public void setGiro(GiroJson giro) { this.giro = giro; }

    public List<String> getGarantias() { return garantias; }
    public void setGarantias(List<String> garantias) { this.garantias = garantias; }

    public ZonaCatastroficaJson getZonaCatastrofica() { return zonaCatastrofica; }
    public void setZonaCatastrofica(ZonaCatastroficaJson zonaCatastrofica) { this.zonaCatastrofica = zonaCatastrofica; }

    public String getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(String estadoValidacion) { this.estadoValidacion = estadoValidacion; }

    public List<AlertaBloquenanteJson> getAlertasBloqueantes() { return alertasBloqueantes; }
    public void setAlertasBloqueantes(List<AlertaBloquenanteJson> alertasBloqueantes) { this.alertasBloqueantes = alertasBloqueantes; }

    public static class GiroJson {
        private String codigo;
        private String descripcion;
        private String claveIncendio;

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getClaveIncendio() { return claveIncendio; }
        public void setClaveIncendio(String claveIncendio) { this.claveIncendio = claveIncendio; }
    }

    public static class ZonaCatastroficaJson {
        private String zonaTev;
        private String zonaFhm;

        public String getZonaTev() { return zonaTev; }
        public void setZonaTev(String zonaTev) { this.zonaTev = zonaTev; }
        public String getZonaFhm() { return zonaFhm; }
        public void setZonaFhm(String zonaFhm) { this.zonaFhm = zonaFhm; }
    }

    public static class AlertaBloquenanteJson {
        private String codigo;
        private String mensaje;
        private String campoAfectado;

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public String getCampoAfectado() { return campoAfectado; }
        public void setCampoAfectado(String campoAfectado) { this.campoAfectado = campoAfectado; }
    }
}
