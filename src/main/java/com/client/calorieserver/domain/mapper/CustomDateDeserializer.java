package com.client.calorieserver.domain.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateDeserializer
        extends StdDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public CustomDateDeserializer() {
        super(LocalDateTime.class);
    }

    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(
            final JsonParser jsonparser, final DeserializationContext context)
            throws IOException {

        final String dateString = jsonparser.getText();
        return LocalDateTime.parse(dateString, formatter);

    }
}
