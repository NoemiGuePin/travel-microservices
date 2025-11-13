package com.noeguepin.dto.event;

public record ReservationCancelledPayload(
		String bookingCode,
		UserInfo user) {

}
