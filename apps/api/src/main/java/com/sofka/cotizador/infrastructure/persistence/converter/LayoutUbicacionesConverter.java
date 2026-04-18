package com.sofka.cotizador.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.cotizador.domain.model.LayoutUbicaciones;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LayoutUbicacionesConverter implements AttributeConverter<LayoutUbicaciones, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final String EMPTY_JSON = "{}";

    @Override
    public String convertToDatabaseColumn(LayoutUbicaciones attribute) {
        if (attribute == null) {
            return EMPTY_JSON;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando LayoutUbicaciones", e);
        }
    }

    @Override
    public LayoutUbicaciones convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || EMPTY_JSON.equals(dbData.trim())) {
            return null;
        }
        try {
            return MAPPER.readValue(dbData, LayoutUbicaciones.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializando LayoutUbicaciones", e);
        }
    }
}
