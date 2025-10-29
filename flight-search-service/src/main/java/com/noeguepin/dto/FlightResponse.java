package com.noeguepin.dto;

import java.time.OffsetDateTime;

import com.noeguepin.model.Flight;

public record FlightResponse(
	Long id,
	String codeFlight, 
	
	String airlineCode,
    String airlineName,
    
    String departureAirportCode,
    String departureAirportName,
    String departureAirportCity,
    String departureAirportCountry,
    
    String arrivalAirportCode,
    String arrivalAirportName,
    String arrivalAirportCity,
    String arrivalAirportCountry,
    
    OffsetDateTime departureTime,
    OffsetDateTime arrivalTime,

    Double price,
    Integer totalSeats,
    Integer availableSeats) {
	
    public FlightResponse(Flight flight) {
        this(
            flight.getId(),
            flight.getCodeFlight(),
            flight.getAirline().getCodeIATA(),
            flight.getAirline().getName(),
            flight.getDepartureAirport().getCodeIATA(),
            flight.getDepartureAirport().getName(),
            flight.getDepartureAirport().getCity(),
            flight.getDepartureAirport().getCountry(),
            flight.getArrivalAirport().getCodeIATA(),
            flight.getArrivalAirport().getName(),
            flight.getArrivalAirport().getCity(),
            flight.getArrivalAirport().getCountry(),
            flight.getDepartureTime(),
            flight.getArrivalTime(),
            flight.getPrice(),
            flight.getTotalSeats(),
            flight.getAvailableSeats()
        );
    }

}
