package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.configuration.Constants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom deserializer for {@Link LocalDateTime} objects.
 */
public class CustomDateSerializer extends StdSerializer<LocalDateTime> {

	private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT);

	public CustomDateSerializer() {
		super(LocalDateTime.class);
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		if (value != null) {
			final String formattedDateTime = value.format(formatter);
			gen.writeString(formattedDateTime);
		}
		else {
			gen.writeNull();
		}

	}

}
