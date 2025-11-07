package com.noeguepin.dto;

import java.time.OffsetDateTime;

import com.noeguepin.model.Reservation;

public record ReservationResponse(
		Long id,
		String bookingCode,
		String flightCode,
		String userId,
		Integer passengersNumber,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt) {
	
	public ReservationResponse(Reservation reservation) {
		this(reservation.getId(),
				reservation.getBookingCode(),
				reservation.getFlightCode(),
				reservation.getUserId(),
				reservation.getPassengersNumber(),
				reservation.getCreatedAt(),
				reservation.getUpdatedAt());}
}
