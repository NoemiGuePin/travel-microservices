package com.noeguepin.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.*;

public record FlightRequest(
		@NotBlank(message = "The flight code cannot be empty")
		String codeFlight, 
		@NotBlank(message = "The airline cannot be empty")
		String airlineCode,
		@NotBlank(message = "The departure airport cannot be empty")
		String departureAirportCode,
		@NotBlank(message = "The arrival airport cannot be empty")
		String arrivalAirportCode,
		@NotNull(message = "The departure time cannot be empty") @FutureOrPresent
		OffsetDateTime departureTime,
		@NotNull(message = "The arrival time cannot be empty") @Future
		OffsetDateTime arrivalTime,
		Double price,
		@Positive Integer totalSeats,
		@Positive Integer availableSeats
		) {

}
