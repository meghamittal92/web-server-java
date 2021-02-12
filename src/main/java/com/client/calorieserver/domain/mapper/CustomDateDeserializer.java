package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.configuration.Constants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom serializer for {@Link LocalDateTime} objects.
 */
public class CustomDateDeserializer
        extends StdDeserializer<LocalDateTime> {


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT);

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
