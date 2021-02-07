package com.client.calorieserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class CalorieserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalorieserverApplication.class, args);
	}

}
