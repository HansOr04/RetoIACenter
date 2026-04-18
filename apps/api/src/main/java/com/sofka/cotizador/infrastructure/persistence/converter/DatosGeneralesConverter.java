package com.sofka.cotizador.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.cotizador.domain.model.DatosGenerales;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DatosGeneralesConverter implements AttributeConverter<DatosGenerales, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final String EMPTY_JSON = "{}";

    @Override
    public String convertToDatabaseColumn(DatosGenerales attribute) {
        if (attribute == null) {
            return EMPTY_JSON;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando DatosGenerales", e);
        }
    }

    @Override
    public DatosGenerales convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || EMPTY_JSON.equals(dbData.trim())) {
            return null;
        }
        try {
            return MAPPER.readValue(dbData, DatosGenerales.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializando DatosGenerales", e);
        }
    }
}
