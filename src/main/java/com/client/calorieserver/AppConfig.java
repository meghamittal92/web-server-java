package com.client.calorieserver;


import com.client.calorieserver.configuration.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        final LocalDateTimeSerializer localDateTimeDeserializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT));
        module.addSerializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper.registerModule(module);
        return objectMapper;

    }
}
