package com.noeguepin.dto.events;

public record ReservationCancelledPayload(
		String bookingCode,
		UserInfo user) {

}
