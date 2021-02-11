package com.client.calorieserver.configuration;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@TestConfiguration
public class TestPersistenceConfiguration {

    @Bean(destroyMethod = "close")
    @Primary
    DataSource dataSource(Environment env) {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty("spring.datasource.url"));
        dataSourceConfig.setUsername(env.getRequiredProperty("spring.datasource.username"));
        dataSourceConfig.setPassword(env.getRequiredProperty("spring.datasource.password"));

        return new HikariDataSource(dataSourceConfig);
    }
}
