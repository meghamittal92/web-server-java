package com.client.calorieserver.configuration;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

public class TestConfiguration {
    @MockBean
    EntityManagerFactory entityManagerFactory;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return entityManagerFactory;
    }
}
