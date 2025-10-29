package com.noeguepin.dto;

import java.time.OffsetDateTime;

public record FlightSearchFilters(
        String airlineCode,
        String departureAirportCode,
        String arrivalAirportCode,
        OffsetDateTime departureFrom,
        OffsetDateTime departureTo,
        Double minimumPrice,
        Double maximumPrice) {
	
    public FlightSearchFilters normalize() {
        return new FlightSearchFilters(
            normalizeString(airlineCode),
            normalizeString(departureAirportCode),
            normalizeString(arrivalAirportCode),
            departureFrom,
            departureTo,
            minimumPrice,
            maximumPrice
        );
    }	
	
    private static String normalizeString(String value) {
        if (value == null) return null;
        String normalized = value.trim().toUpperCase();
        return normalized.isEmpty() ? null : normalized;
    }

}
