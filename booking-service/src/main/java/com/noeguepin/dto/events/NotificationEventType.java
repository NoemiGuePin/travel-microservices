package com.noeguepin.dto.events;

public enum NotificationEventType {
	
    RESERVATION_CREATED("ReservationCreated"),
    RESERVATION_UPDATED("ReservationUpdated"),
    RESERVATION_CANCELLED("ReservationCancelled");

    private final String value;

    NotificationEventType(String v) { this.value = v; }

    public String value() { return value; }

}
