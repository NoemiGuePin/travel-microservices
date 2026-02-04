package com.noeguepin.testdata;

import com.noeguepin.dto.FlightRequest;
import com.noeguepin.dto.FlightResponse;
import com.noeguepin.dto.FlightSearchFilters;
import com.noeguepin.model.Flight;

import java.time.OffsetDateTime;

public class FlightTestData {

    public static Flight af237() {
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setCodeFlight("AF237");
        flight.setDepartureAirport(AirportTestData.madridBarajas());
        flight.setArrivalAirport(AirportTestData.barcelonaElPrat());
        flight.setAirline(AirlineTestData.airFrance());
        flight.setDepartureTime(OffsetDateTime.parse("2025-11-11T08:00:00+00:00"));
        flight.setArrivalTime(OffsetDateTime.parse("2025-11-11T18:00:00+00:00"));
        flight.setPrice(260.16);
        flight.setTotalSeats(200);
        flight.setAvailableSeats(155);
        return flight;
    }

    public static Flight ib237() {
        Flight flight = new Flight();
        flight.setId(2L);
        flight.setCodeFlight("IB237");
        flight.setDepartureAirport(AirportTestData.madridBarajas());
        flight.setArrivalAirport(AirportTestData.charlesDeGaulle());
        flight.setAirline(AirlineTestData.iberia());
        flight.setDepartureTime(OffsetDateTime.parse("2025-11-11T08:00:00+00:00"));
        flight.setArrivalTime(OffsetDateTime.parse("2025-11-11T18:00:00+00:00"));
        flight.setPrice(510.00);
        flight.setTotalSeats(500);
        flight.setAvailableSeats(50);
        return flight;
    }

    public static FlightSearchFilters filtersWithSpacesAndLowercase() {
        return new FlightSearchFilters(
                " ib ",
                " mad ",
                " bcn ",
                OffsetDateTime.parse("2025-12-11T08:00:00Z"),
                OffsetDateTime.parse("2025-12-11T20:00:00Z"),
                100.00,
                400.00
        );
    }

    public static FlightSearchFilters filters() {
        return new FlightSearchFilters(
                "IB",
                "MAD",
                "BCN",
                OffsetDateTime.parse("2025-12-11T08:00:00Z"),
                OffsetDateTime.parse("2025-12-11T20:00:00Z"),
                100.00,
                400.00
        );
    }

    public static FlightRequest af237Request() {
        return new FlightRequest(
                "AF237",
                "AF",
                "MAD",
                "BCN",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                260.16,
                200,
                155
        );
    }

    public static FlightRequest af237RequestNoValid() {
        return new FlightRequest(
                "",
                "AF",
                "MAD",
                "BCN",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                260.16,
                200,
                155
        );
    }

    public static FlightResponse af237Response() {
        return new FlightResponse(af237());
    }

}
