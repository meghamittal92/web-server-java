package com.client.calorieserver;

import com.client.calorieserver.repository.CustomRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@Import({ AppConfig.class })
public class CalorieserverApplication {

	public static void main(String[] args) {

		SpringApplication.run(CalorieserverApplication.class, args);
	}

}
