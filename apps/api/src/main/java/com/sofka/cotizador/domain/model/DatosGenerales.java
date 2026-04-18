package com.sofka.cotizador.domain.model;

public record DatosGenerales(
        String nombreTomador,
        String rucCedula,
        String correoElectronico,
        String telefonoContacto,
        String tipoInmueble,
        String usoPrincipal,
        Integer anoConstruccion,
        Integer numeroPisos,
        String descripcion
) {}
