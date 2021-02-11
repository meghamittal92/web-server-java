package com.client.calorieserver.mapper;

import com.client.calorieserver.configuration.security.JWTAuthorizationFilter;
import com.client.calorieserver.domain.mapper.CustomDateDeserializer;
import com.client.calorieserver.domain.mapper.CustomDateSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.tomcat.util.json.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CustomDateDeserializerTest {
    private static final String date="1986-04-08T12:30:00";

    private CustomDateDeserializer customDateDeserializer;

    @Mock
    JsonParser jsonParser;

    @Mock
    DeserializationContext context;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        customDateDeserializer = new CustomDateDeserializer();
    }

    @Test
    void deserialize() throws Exception{
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(jsonParser.getText()).thenReturn(date);
        LocalDateTime deserialized = customDateDeserializer.deserialize(jsonParser, context);
        assert deserialized != null;
        assert deserialized.getYear() == 1986;
        assert deserialized.getDayOfMonth() == 8;
    }

}
