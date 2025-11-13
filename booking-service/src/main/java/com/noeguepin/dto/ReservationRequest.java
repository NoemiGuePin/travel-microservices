package com.noeguepin.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
		String flightCode,
		@Min(1) @NotNull(message = "The number of passengers cannot be empty")
		Integer passengersNumber) {

}
