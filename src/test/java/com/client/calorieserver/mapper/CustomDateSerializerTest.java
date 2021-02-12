package com.client.calorieserver.mapper;

import com.client.calorieserver.configuration.Constants;
import com.client.calorieserver.domain.mapper.CustomDateSerializer;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CustomDateSerializerTest {

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT);

	private CustomDateSerializer customDateSerializer;

	@Mock
	JsonGenerator jsonGenerator;

	@Mock
	SerializerProvider serializerProvider;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
		customDateSerializer = new CustomDateSerializer();
	}

	@Test
	void serialize() throws Exception {
		LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		customDateSerializer.serialize(null, jsonGenerator, serializerProvider);
		Mockito.verify(jsonGenerator, Mockito.times(1)).writeNull();

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
		customDateSerializer.serialize(time, jsonGenerator, serializerProvider);
		Mockito.verify(jsonGenerator, Mockito.times(1)).writeString(argumentCaptor.capture());
		assert argumentCaptor.getValue().equalsIgnoreCase(formatter.format(time));
	}

}
