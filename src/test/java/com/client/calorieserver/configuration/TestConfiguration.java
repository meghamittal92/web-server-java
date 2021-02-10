package com.client.calorieserver.configuration;

import org.hibernate.metamodel.internal.MetamodelImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @MockBean
    EntityManagerFactory entityManagerFactory;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return entityManagerFactory;
    }
}
