package com.sofka.cotizador.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record DatosGeneralesRequest(
        @NotBlank String nombreTomador,
        @NotBlank String rucCedula,
        @NotBlank String correoElectronico,
        String telefonoContacto,
        @NotBlank String tipoInmueble,
        @NotBlank String usoPrincipal,
        Integer anoConstruccion,
        Integer numeroPisos,
        String descripcion
) {}
