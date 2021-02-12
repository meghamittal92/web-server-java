package com.client.calorieserver;

import com.client.calorieserver.accessor.CalorieAccessor;
import com.client.calorieserver.accessor.NutritionixCalorieAccessorImpl;
import com.client.calorieserver.configuration.Constants;
import com.client.calorieserver.repository.CustomRepositoryImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableSpringDataWebSupport
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class AppConfig {

	@Value("${app.nutritionix.apiKey}")
	private String apikey;

	@Value("${app.nutritionix.appId}")
	private String appId;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		final LocalDateTimeSerializer localDateTimeDeserializer = new LocalDateTimeSerializer(
				DateTimeFormatter.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT));
		module.addSerializer(LocalDateTime.class, localDateTimeDeserializer);
		objectMapper.registerModule(module);
		return objectMapper;

	}

	@Bean
	public ConversionService conversionService() {
		return new DefaultConversionService();
	}

	@Bean
	public CalorieAccessor calorieAccessor() {
		return new NutritionixCalorieAccessorImpl(apikey, appId);
	}

}
