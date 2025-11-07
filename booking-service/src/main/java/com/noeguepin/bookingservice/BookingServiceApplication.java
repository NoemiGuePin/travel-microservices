package com.noeguepin.bookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"com.noeguepin.controller","com.noeguepin.service","com.noeguepin.exception","com.noeguepin.config", "com.noeguepin.security"})
@EnableJpaRepositories(basePackages = "com.noeguepin.repository")
@EntityScan(basePackages = "com.noeguepin.model")
@SpringBootApplication
public class BookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingServiceApplication.class, args);
	}

}
