package com.noeguepin.flightsearchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"com.noeguepin.controller","com.noeguepin.service","com.noeguepin.exception"})
@EnableJpaRepositories(basePackages = "com.noeguepin.repository")
@EntityScan(basePackages = "com.noeguepin.model")
@SpringBootApplication
public class FlightSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightSearchServiceApplication.class, args);
	}

}
