package com.noeguepin.dto.event;

public record ReservationCreatedPayload(
		String bookingCode,
		String flightCode,
		int passengersNumber,
		UserInfo user) {

}
