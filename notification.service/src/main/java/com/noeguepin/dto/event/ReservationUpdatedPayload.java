package com.noeguepin.dto.event;

public record ReservationUpdatedPayload(
		String bookingCode,  
		String flightCode,
		int newPassengersNumber, 
		UserInfo user) {

}
