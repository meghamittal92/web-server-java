package com.client.calorieserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@Profile("test")
@EnableJpaAuditing(auditorAwareRef = "testAuditorProvider")
public class TestAuditingConfiguration {

	@Bean
	@Primary
	public AuditorAware<String> testAuditorProvider() {
		return () -> Optional.of("Test");
	}

}
