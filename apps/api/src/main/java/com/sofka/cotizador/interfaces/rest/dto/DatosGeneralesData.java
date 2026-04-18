package com.sofka.cotizador.interfaces.rest.dto;

public record DatosGeneralesData(
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
