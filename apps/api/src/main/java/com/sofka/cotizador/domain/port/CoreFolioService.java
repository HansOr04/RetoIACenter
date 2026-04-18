package com.sofka.cotizador.domain.port;

// HU-001 — contrato del servicio externo de generación de número de folio
public interface CoreFolioService {

    /**
     * Solicita al sistema core que genere o confirme un número de folio.
     * Lanza CoreServiceUnavailableException si el sistema core no responde.
     *
     * @param codigoAgente agente que origina la cotización (puede ser null)
     * @return número de folio único en formato F{año}-{secuencia}
     */
    String generarNumeroFolio(String codigoAgente);
}
