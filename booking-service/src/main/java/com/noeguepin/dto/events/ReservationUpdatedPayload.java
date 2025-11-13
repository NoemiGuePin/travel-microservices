package com.noeguepin.dto.events;

public record ReservationUpdatedPayload(
		String bookingCode,  
		String flightCode,
		int newPassengersNumber, 
		UserInfo user) {

}
