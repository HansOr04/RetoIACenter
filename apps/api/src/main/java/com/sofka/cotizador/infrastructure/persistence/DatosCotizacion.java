package com.sofka.cotizador.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatosCotizacion {

    private List<UbicacionJson> ubicaciones = new ArrayList<>();
    private OpcionesCoberturaDatos opcionesCobertura;
    private ResultadoCalculoDatos resultadoCalculo;
    private String estado;

    public List<UbicacionJson> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<UbicacionJson> ubicaciones) {
        this.ubicaciones = ubicaciones != null ? ubicaciones : new ArrayList<>();
    }

    public OpcionesCoberturaDatos getOpcionesCobertura() {
        return opcionesCobertura;
    }

    public void setOpcionesCobertura(OpcionesCoberturaDatos opcionesCobertura) {
        this.opcionesCobertura = opcionesCobertura;
    }

    public ResultadoCalculoDatos getResultadoCalculo() {
        return resultadoCalculo;
    }

    public void setResultadoCalculo(ResultadoCalculoDatos resultadoCalculo) {
        this.resultadoCalculo = resultadoCalculo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // ── Inner POJOs for Jackson serialization ──────────────────────────────────

    public static class OpcionesCoberturaDatos {
        private boolean incendioEdificios;
        private boolean incendioContenidos;
        private boolean extensionCobertura;
        private boolean catTev;
        private boolean catFhm;
        private boolean remocionEscombros;
        private boolean gastosExtraordinarios;
        private boolean perdidaRentas;
        private boolean bi;
        private boolean equipoElectronico;
        private boolean robo;
        private boolean dineroValores;
        private boolean vidrios;
        private boolean anunciosLuminosos;

        public boolean isIncendioEdificios() { return incendioEdificios; }
        public void setIncendioEdificios(boolean incendioEdificios) { this.incendioEdificios = incendioEdificios; }
        public boolean isIncendioContenidos() { return incendioContenidos; }
        public void setIncendioContenidos(boolean incendioContenidos) { this.incendioContenidos = incendioContenidos; }
        public boolean isExtensionCobertura() { return extensionCobertura; }
        public void setExtensionCobertura(boolean extensionCobertura) { this.extensionCobertura = extensionCobertura; }
        public boolean isCatTev() { return catTev; }
        public void setCatTev(boolean catTev) { this.catTev = catTev; }
        public boolean isCatFhm() { return catFhm; }
        public void setCatFhm(boolean catFhm) { this.catFhm = catFhm; }
        public boolean isRemocionEscombros() { return remocionEscombros; }
        public void setRemocionEscombros(boolean remocionEscombros) { this.remocionEscombros = remocionEscombros; }
        public boolean isGastosExtraordinarios() { return gastosExtraordinarios; }
        public void setGastosExtraordinarios(boolean gastosExtraordinarios) { this.gastosExtraordinarios = gastosExtraordinarios; }
        public boolean isPerdidaRentas() { return perdidaRentas; }
        public void setPerdidaRentas(boolean perdidaRentas) { this.perdidaRentas = perdidaRentas; }
        public boolean isBi() { return bi; }
        public void setBi(boolean bi) { this.bi = bi; }
        public boolean isEquipoElectronico() { return equipoElectronico; }
        public void setEquipoElectronico(boolean equipoElectronico) { this.equipoElectronico = equipoElectronico; }
        public boolean isRobo() { return robo; }
        public void setRobo(boolean robo) { this.robo = robo; }
        public boolean isDineroValores() { return dineroValores; }
        public void setDineroValores(boolean dineroValores) { this.dineroValores = dineroValores; }
        public boolean isVidrios() { return vidrios; }
        public void setVidrios(boolean vidrios) { this.vidrios = vidrios; }
        public boolean isAnunciosLuminosos() { return anunciosLuminosos; }
        public void setAnunciosLuminosos(boolean anunciosLuminosos) { this.anunciosLuminosos = anunciosLuminosos; }
    }

    public static class ResultadoCalculoDatos {
        private BigDecimal primaNeta;
        private BigDecimal primaComercial;
        private BigDecimal factorComercial;
        private List<PrimaPorUbicacionDatos> primasPorUbicacion;
        private LocalDateTime fechaCalculo;

        public BigDecimal getPrimaNeta() { return primaNeta; }
        public void setPrimaNeta(BigDecimal primaNeta) { this.primaNeta = primaNeta; }
        public BigDecimal getPrimaComercial() { return primaComercial; }
        public void setPrimaComercial(BigDecimal primaComercial) { this.primaComercial = primaComercial; }
        public BigDecimal getFactorComercial() { return factorComercial; }
        public void setFactorComercial(BigDecimal factorComercial) { this.factorComercial = factorComercial; }
        public List<PrimaPorUbicacionDatos> getPrimasPorUbicacion() { return primasPorUbicacion; }
        public void setPrimasPorUbicacion(List<PrimaPorUbicacionDatos> primasPorUbicacion) { this.primasPorUbicacion = primasPorUbicacion; }
        public LocalDateTime getFechaCalculo() { return fechaCalculo; }
        public void setFechaCalculo(LocalDateTime fechaCalculo) { this.fechaCalculo = fechaCalculo; }
    }

    public static class PrimaPorUbicacionDatos {
        private int indice;
        private boolean calculada;
        private BigDecimal total;
        private DesgloseComponentesDatos desglose;
        private List<AlertaBloqueanteDatos> alertas;

        public int getIndice() { return indice; }
        public void setIndice(int indice) { this.indice = indice; }
        public boolean isCalculada() { return calculada; }
        public void setCalculada(boolean calculada) { this.calculada = calculada; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        public DesgloseComponentesDatos getDesglose() { return desglose; }
        public void setDesglose(DesgloseComponentesDatos desglose) { this.desglose = desglose; }
        public List<AlertaBloqueanteDatos> getAlertas() { return alertas; }
        public void setAlertas(List<AlertaBloqueanteDatos> alertas) { this.alertas = alertas; }
    }

    public static class DesgloseComponentesDatos {
        private BigDecimal incendioEdificios;
        private BigDecimal incendioContenidos;
        private BigDecimal extensionCobertura;
        private BigDecimal catTev;
        private BigDecimal catFhm;
        private BigDecimal remocionEscombros;
        private BigDecimal gastosExtraordinarios;
        private BigDecimal perdidaRentas;
        private BigDecimal bi;
        private BigDecimal equipoElectronico;
        private BigDecimal robo;
        private BigDecimal dineroValores;
        private BigDecimal vidrios;
        private BigDecimal anunciosLuminosos;
        private BigDecimal total;

        public BigDecimal getIncendioEdificios() { return incendioEdificios; }
        public void setIncendioEdificios(BigDecimal v) { this.incendioEdificios = v; }
        public BigDecimal getIncendioContenidos() { return incendioContenidos; }
        public void setIncendioContenidos(BigDecimal v) { this.incendioContenidos = v; }
        public BigDecimal getExtensionCobertura() { return extensionCobertura; }
        public void setExtensionCobertura(BigDecimal v) { this.extensionCobertura = v; }
        public BigDecimal getCatTev() { return catTev; }
        public void setCatTev(BigDecimal v) { this.catTev = v; }
        public BigDecimal getCatFhm() { return catFhm; }
        public void setCatFhm(BigDecimal v) { this.catFhm = v; }
        public BigDecimal getRemocionEscombros() { return remocionEscombros; }
        public void setRemocionEscombros(BigDecimal v) { this.remocionEscombros = v; }
        public BigDecimal getGastosExtraordinarios() { return gastosExtraordinarios; }
        public void setGastosExtraordinarios(BigDecimal v) { this.gastosExtraordinarios = v; }
        public BigDecimal getPerdidaRentas() { return perdidaRentas; }
        public void setPerdidaRentas(BigDecimal v) { this.perdidaRentas = v; }
        public BigDecimal getBi() { return bi; }
        public void setBi(BigDecimal v) { this.bi = v; }
        public BigDecimal getEquipoElectronico() { return equipoElectronico; }
        public void setEquipoElectronico(BigDecimal v) { this.equipoElectronico = v; }
        public BigDecimal getRobo() { return robo; }
        public void setRobo(BigDecimal v) { this.robo = v; }
        public BigDecimal getDineroValores() { return dineroValores; }
        public void setDineroValores(BigDecimal v) { this.dineroValores = v; }
        public BigDecimal getVidrios() { return vidrios; }
        public void setVidrios(BigDecimal v) { this.vidrios = v; }
        public BigDecimal getAnunciosLuminosos() { return anunciosLuminosos; }
        public void setAnunciosLuminosos(BigDecimal v) { this.anunciosLuminosos = v; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal v) { this.total = v; }
    }

    public static class AlertaBloqueanteDatos {
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
