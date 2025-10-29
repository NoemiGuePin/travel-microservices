package com.noeguepin.dto;

import jakarta.validation.constraints.NotBlank;

public record AirlineRequest( 
		@NotBlank(message = "The IATA flight code cannot be empty")
		String codeIATA, 
		@NotBlank(message = "The name cannot be empty")
		String name) {

}
