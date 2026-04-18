package com.sofka.cotizador.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.cotizador.infrastructure.persistence.DatosCotizacion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CotizacionDatosConverter implements AttributeConverter<DatosCotizacion, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final String EMPTY_JSON = "{}";

    @Override
    public String convertToDatabaseColumn(DatosCotizacion attribute) {
        if (attribute == null) {
            return EMPTY_JSON;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando DatosCotizacion", e);
        }
    }

    @Override
    public DatosCotizacion convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || EMPTY_JSON.equals(dbData.trim())) {
            return new DatosCotizacion();
        }
        try {
            return MAPPER.readValue(dbData, DatosCotizacion.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializando DatosCotizacion", e);
        }
    }
}
