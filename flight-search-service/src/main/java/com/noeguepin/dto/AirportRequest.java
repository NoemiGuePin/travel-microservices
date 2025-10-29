package com.noeguepin.dto;

import jakarta.validation.constraints.NotBlank;

public record AirportRequest(
		@NotBlank(message = "The IATA flight code cannot be empty")
		String codeIATA, 
		@NotBlank(message = "The name cannot be empty")
		String name,
		@NotBlank(message = "The city cannot be empty")
		String city,
		@NotBlank(message = "The country cannot be empty")
		String country
		) {

}
