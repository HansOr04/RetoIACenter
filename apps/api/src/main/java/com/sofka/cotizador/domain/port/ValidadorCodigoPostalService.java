package com.sofka.cotizador.domain.port;

import com.sofka.cotizador.domain.model.ubicacion.ZonaCatastrofica;

import java.util.Optional;

public interface ValidadorCodigoPostalService {

    Optional<ZonaCatastrofica> validarCodigoPostal(String codigoPostal);
}
