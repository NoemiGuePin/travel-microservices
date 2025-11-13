package com.noeguepin.dto.events;

public record ReservationCreatedPayload(
		String bookingCode,
		String flightCode,
		int passengersNumber,
		UserInfo user) {

}
