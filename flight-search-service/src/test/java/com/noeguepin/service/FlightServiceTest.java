package com.noeguepin.service;

import com.noeguepin.dto.*;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.exception.SeatsException;
import com.noeguepin.model.Airline;
import com.noeguepin.model.Flight;
import com.noeguepin.repository.AirlineRepository;
import com.noeguepin.repository.AirportRepository;
import com.noeguepin.repository.FlightRepository;
import com.noeguepin.testdata.AirlineTestData;
import com.noeguepin.testdata.AirportTestData;
import com.noeguepin.testdata.FlightTestData;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private FlightService flightService;

    @Test
    void testGetFlightsByFilters() {
        FlightSearchFilters filters = FlightTestData.filters();
        when(flightRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(FlightTestData.ib237()));

        List<FlightResponse> listFlightResponse =  flightService.getFlightsByFilters(filters, "price", "asc");

        assertNotNull(listFlightResponse);
        assertEquals(1, listFlightResponse.size());
        FlightResponse flightResponse = listFlightResponse.get(0);
        assertEquals(FlightTestData.ib237().getCodeFlight(), flightResponse.codeFlight());
        assertEquals(FlightTestData.ib237().getDepartureTime(), flightResponse.departureTime());
        assertEquals(FlightTestData.ib237().getArrivalTime(), flightResponse.arrivalTime());
        assertEquals(FlightTestData.ib237().getPrice(), flightResponse.price(), 0.001);
        assertEquals(FlightTestData.ib237().getTotalSeats(), flightResponse.totalSeats());

        ArgumentCaptor<Specification<Flight>> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(flightRepository, only()).findAll(specificationCaptor.capture(), sortCaptor.capture());
        Sort usedSort = sortCaptor.getValue();
        assertNotNull(usedSort.getOrderFor("price"));
        assertEquals(Sort.Direction.ASC, usedSort.getOrderFor("price").getDirection());
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testGetFlightsByFiltersNotAllowedSortFields() {
        FlightSearchFilters filters = FlightTestData.filters();

        assertThrows(IllegalArgumentException.class, () ->
                flightService.getFlightsByFilters(filters, "xxx", "asc")
        );
    }

    @Test
    void testGetFlightsByFiltersNormalize() {
        FlightSearchFilters filters = FlightTestData.filtersWithSpacesAndLowercase();

        FlightSearchFilters normalized = filters.normalize();
        assertEquals("IB", normalized.airlineCode());
        assertEquals("MAD", normalized.departureAirportCode());
        assertEquals("BCN", normalized.arrivalAirportCode());
    }

    @Test
    void testGetFlightsByFilters_InvalidDateRange() {
        FlightSearchFilters filters = new FlightSearchFilters(
                "IB",
                "MAD",
                "BCN",
                OffsetDateTime.parse("2025-12-12T10:00:00Z"),
                OffsetDateTime.parse("2025-12-10T10:00:00Z"),
                100.00,
                400.00
        );

        assertThrows(IllegalArgumentException.class, () ->
                flightService.getFlightsByFilters(filters, "price", "asc")
        );
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testGetFlightsByFilters_InvalidPriceRange() {
        FlightSearchFilters filters = new FlightSearchFilters(
                "IB",
                "MAD",
                "BCN",
                OffsetDateTime.parse("2025-12-10T10:00:00Z"),
                OffsetDateTime.parse("2025-12-12T10:00:00Z"),
                500.00,
                300.00
        );

        assertThrows(IllegalArgumentException.class, () ->
                flightService.getFlightsByFilters(filters, "price", "asc")
        );
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testGetFlightByCodeFlight() {
        when(flightRepository.findByCodeFlight("AF237")).thenReturn(Optional.of(FlightTestData.af237()));

        FlightResponse flightResponse = flightService.getFlightByCodeFlight("AF237");

        assertNotNull(flightResponse);
        assertEquals("AF237", flightResponse.codeFlight());
        assertEquals(260.16, flightResponse.price());
        assertEquals(155, flightResponse.availableSeats());
        verify(flightRepository, only()).findByCodeFlight("AF237");
    }

    @Test
    void testGetFlightByCodeFlightEmpty() {
        when(flightRepository.findByCodeFlight("GT854")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.getFlightByCodeFlight("GT854"));
        verify(flightRepository, only()).findByCodeFlight("GT854");
    }

    @Test
    void testUpdateFlight() {
        String codeFlight = "IB237";
        OffsetDateTime departureTime = OffsetDateTime.parse("2025-12-11T08:00:00+00:00");
        OffsetDateTime arrivalTime = OffsetDateTime.parse("2025-12-11T18:00:00+00:00");
        Double price = 280.00;
        FlightRequest flightRequest = new FlightRequest(codeFlight, "IB", "MAD", "CDG",
                departureTime, arrivalTime, price,500,50);
        when(flightRepository.findByCodeFlight(codeFlight)).thenReturn(Optional.of(FlightTestData.ib237()));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse flightResponse = flightService.updateFlight(codeFlight, flightRequest );

        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository, times(1)).findByCodeFlight(codeFlight);
        verify(flightRepository).save(captor.capture());
        Flight flightSaved = captor.getValue();
        assertAll("Validation of flight fields",
                () -> assertEquals(codeFlight, flightSaved.getCodeFlight()),
                () -> assertEquals(departureTime, flightSaved.getDepartureTime()),
                () -> assertEquals(arrivalTime, flightSaved.getArrivalTime()),
                () -> assertEquals(price, flightSaved.getPrice())
        );
        assertNotNull(flightResponse);
        assertAll("Validation of flight response fields",
                () -> assertEquals(codeFlight, flightResponse.codeFlight()),
                () -> assertEquals(departureTime, flightResponse.departureTime()),
                () -> assertEquals(arrivalTime, flightResponse.arrivalTime()),
                () -> assertEquals(price, flightResponse.price())
        );
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testUpdateFlightInvalidHours() {
        FlightRequest flightRequest = new FlightRequest("AF237", "IB", "MAD", "BCN",
                OffsetDateTime.parse("2025-12-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                280.00,200,155);

        assertThrows(IllegalArgumentException.class, () -> flightService.updateFlight("AF237", flightRequest));
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testUpdateFlightCodeFlightNotFound() {
        FlightRequest flightRequest = new FlightRequest("TD854", "IB", "MAD", "BCN",
                OffsetDateTime.parse("2025-12-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-12-11T18:00:00+00:00"),
                280.00,200,155);
        when(flightRepository.findByCodeFlight("TD854")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.updateFlight("TD854", flightRequest));
        verify(flightRepository, only()).findByCodeFlight("TD854");
        verify(flightRepository, never()).save(any(Flight.class));
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testDeleteFlightByCodeFlight() {
        Flight flight = FlightTestData.af237();
        when(flightRepository.findByCodeFlight("AF237")).thenReturn(Optional.of(flight));

        flightService.deleteFlightByCodeFlight("AF237");

        verify(flightRepository, times(1)).findByCodeFlight("AF237");
        verify(flightRepository, times(1)).delete(flight);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testDeleteFlightByCodeFlightNotFound() {
        when(flightRepository.findByCodeFlight("TD854")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.deleteFlightByCodeFlight("TD854"));
        verify(flightRepository, only()).findByCodeFlight("TD854");
        verify(flightRepository, never()).delete(any(Flight.class));
    }

    @Test
    void testReserveSeats() {
        Flight flight = FlightTestData.ib237();
        when(flightRepository.findByCodeFlight("IB237")).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse flightResponse = flightService.reserveSeats("IB237", 30);
        verify(flightRepository, times(1)).save(any(Flight.class));
        assertNotNull(flightResponse);
        assertEquals(flight.getCodeFlight(), flightResponse.codeFlight());
        assertEquals(flight.getDepartureAirport().getCodeIATA(), flightResponse.departureAirportCode());
        assertEquals(flight.getArrivalAirport().getCodeIATA(), flightResponse.arrivalAirportCode());

        assertEquals(FlightTestData.ib237().getAvailableSeats() - 30, flightResponse.availableSeats());

        verify(flightRepository, times(1)).findByCodeFlight("IB237");
        verify(flightRepository, times(1)).save(any(Flight.class));
        verifyNoMoreInteractions(flightRepository);

    }

    @Test
    void testReserveSeatsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> flightService.reserveSeats("IB237", 0));
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testReserveSeatsFlightNotFound() {
        when(flightRepository.findByCodeFlight("IB854")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.reserveSeats("IB854", 20));
        verify(flightRepository, only()).findByCodeFlight("IB854");
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testReserveSeatsNotAvailable() {
        when(flightRepository.findByCodeFlight("IB237")).thenReturn(Optional.of(FlightTestData.ib237()));

        assertThrows(SeatsException.class, () -> flightService.reserveSeats("IB237", 100));
        verify(flightRepository, only()).findByCodeFlight("IB237");
        verify(flightRepository, never()).save((any(Flight.class)));
        verifyNoMoreInteractions(flightRepository);
    }


    @Test
    void testReleaseSeats() {
        Flight flight = FlightTestData.ib237();
        when(flightRepository.findByCodeFlight("IB237")).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse flightResponse = flightService.releaseSeats("IB237", 30);
        verify(flightRepository, times(1)).save(any(Flight.class));
        assertNotNull(flightResponse);
        assertEquals(flight.getCodeFlight(), flightResponse.codeFlight());
        assertEquals(flight.getDepartureAirport().getCodeIATA(), flightResponse.departureAirportCode());
        assertEquals(flight.getArrivalAirport().getCodeIATA(), flightResponse.arrivalAirportCode());

        assertEquals(FlightTestData.ib237().getAvailableSeats() + 30, flightResponse.availableSeats());

        verify(flightRepository, times(1)).findByCodeFlight("IB237");
        verify(flightRepository, times(1)).save(any(Flight.class));
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void testReleaseSeatsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> flightService.releaseSeats("IB237", 0));
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testReleaseSeatsFlightNotFound() {
        when(flightRepository.findByCodeFlight("IB854")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.releaseSeats("IB854", 20));
        verify(flightRepository, only()).findByCodeFlight("IB854");
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testReleaseSeatsNotAvailable() {
        when(flightRepository.findByCodeFlight("IB237")).thenReturn(Optional.of(FlightTestData.ib237()));

        assertThrows(SeatsException.class, () -> flightService.releaseSeats("IB237", 480));
        verify(flightRepository, only()).findByCodeFlight("IB237");
        verify(flightRepository, never()).save(any(Flight.class));
    }


    @Test
    void testSaveNewFlight() {
        String codeFlight = "HT763";
        String airlineCode = "IB";
        String departureAirportCode = "BCN";
        String arrivalAirportCode = "MAD";
        OffsetDateTime departureTime = OffsetDateTime.parse("2025-11-11T08:00:00+00:00");
        OffsetDateTime arrivalTime = OffsetDateTime.parse("2025-11-11T18:00:00+00:00");
        Double price = 260.00;
        Integer totalSeats = 200;
        Integer availableSeats = 200;
        FlightRequest flightRequest= new FlightRequest(codeFlight, airlineCode, departureAirportCode, arrivalAirportCode,
                departureTime, arrivalTime, price, totalSeats, availableSeats);
        when(flightRepository.findByCodeFlight(codeFlight)).thenReturn(Optional.empty());
        when(airlineRepository.findByCodeIATA(airlineCode)).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airportRepository.findByCodeIATA(departureAirportCode)).thenReturn(Optional.of(AirportTestData.barcelonaElPrat()));
        when(airportRepository.findByCodeIATA(arrivalAirportCode)).thenReturn(Optional.of(AirportTestData.madridBarajas()));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse flightResponse = flightService.saveNewFlight(flightRequest);

        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository, times(1)).findByCodeFlight(codeFlight);
        verify(flightRepository).save(captor.capture());
        Flight flightSaved = captor.getValue();
        assertAll("Validation of flight fields",
                () -> assertEquals(codeFlight, flightSaved.getCodeFlight()),
                () -> assertEquals(airlineCode, flightSaved.getAirline().getCodeIATA()),
                () -> assertEquals(price, flightSaved.getPrice()),
                () -> assertEquals(totalSeats, flightSaved.getTotalSeats()),
                () -> assertEquals(availableSeats, flightSaved.getAvailableSeats()),
                () -> assertEquals(departureAirportCode, flightSaved.getDepartureAirport().getCodeIATA()),
                () -> assertEquals(arrivalAirportCode, flightSaved.getArrivalAirport().getCodeIATA()),
                () -> assertEquals(departureTime, flightSaved.getDepartureTime()),
                () -> assertEquals(arrivalTime, flightSaved.getArrivalTime())
        );
        verify(airportRepository, times(1)).findByCodeIATA("BCN");
        verify(airportRepository, times(1)).findByCodeIATA("MAD");
        verify(airlineRepository, times(1)).findByCodeIATA("IB");

        assertAll("Validation of flight response fields",
                () -> assertNotNull(flightResponse, "La respuesta no debe ser nula"),
                () -> assertEquals(codeFlight, flightResponse.codeFlight()),
                () -> assertEquals(airlineCode, flightResponse.airlineCode()),
                () -> assertEquals(price, flightResponse.price()),
                () -> assertEquals(totalSeats, flightResponse.totalSeats()),
                () -> assertEquals(availableSeats, flightResponse.availableSeats()),
                () -> assertEquals(departureAirportCode, flightResponse.departureAirportCode()),
                () -> assertEquals(arrivalAirportCode, flightResponse.arrivalAirportCode()),
                () -> assertEquals(departureTime, flightResponse.departureTime()),
                () -> assertEquals(arrivalTime, flightResponse.arrivalTime())
        );
    }

    @Test
    void testSaveNewFlightAlreadyExistingCode() {
        FlightRequest flightRequest= new FlightRequest("AF237", "AF", "MAD", "CDG",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),260.00,200,200);
        when(flightRepository.findByCodeFlight("AF237")).thenReturn(Optional.of(FlightTestData.af237()));

        assertThrows(ResourceAlreadyExistsException.class, () -> flightService.saveNewFlight(flightRequest));
        verify(flightRepository, only()).findByCodeFlight("AF237");
        verify(flightRepository, never()).save(any());
    }

    @Test
    void testSaveNewFlightAirlineNotFound() {
        FlightRequest flightRequest= new FlightRequest("HT763", "XX", "BCN", "MAD",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                260.00,200,200);

        when(flightRepository.findByCodeFlight("HT763")).thenReturn(Optional.empty());
        when(airlineRepository.findByCodeIATA("XX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,() -> flightService.saveNewFlight(flightRequest));
        verify(flightRepository, times(1)).findByCodeFlight("HT763");
        verify(airlineRepository, times(1)).findByCodeIATA("XX");
        verify(flightRepository, never()).save(any());
    }

    @Test
    void testSaveNewFlightDepartureAirportNotFound() {
        FlightRequest flightRequest = new FlightRequest("HT763", "IB", "XXX", "MAD",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                260.00, 200, 200);

        when(flightRepository.findByCodeFlight("HT763")).thenReturn(Optional.empty());
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airportRepository.findByCodeIATA("XXX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.saveNewFlight(flightRequest));
        verify(flightRepository, times(1)).findByCodeFlight("HT763");
        verify(airlineRepository, times(1)).findByCodeIATA("IB");
        verify(airportRepository, times(1)).findByCodeIATA("XXX");
        verify(flightRepository, never()).save(any());
    }

    @Test
    void testSaveNewFlightArrivalAirportNotFound() {
        FlightRequest flightRequest = new FlightRequest("HT763", "IB", "BCN", "XXX",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                260.00, 200, 200);
        when(flightRepository.findByCodeFlight("HT763")).thenReturn(Optional.empty());
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airportRepository.findByCodeIATA("BCN")).thenReturn(Optional.of(AirportTestData.barcelonaElPrat()));
        when(airportRepository.findByCodeIATA("XXX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.saveNewFlight(flightRequest));
        verify(flightRepository, times(1)).findByCodeFlight("HT763");
        verify(airlineRepository, times(1)).findByCodeIATA("IB");
        verify(airportRepository, times(1)).findByCodeIATA("BCN");
        verify(airportRepository, times(1)).findByCodeIATA("XXX");
        verify(flightRepository, never()).save(any());
    }

    @Test
    void testSaveNewFlightSameAirportsShouldThrow() {
        FlightRequest flightRequest = new FlightRequest("HT763", "IB", "MAD", "MAD",
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                260.00, 200, 200
        );
        when(flightRepository.findByCodeFlight("HT763")).thenReturn(Optional.empty());
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.of(AirportTestData.madridBarajas()));

        assertThrows(IllegalArgumentException.class,() -> flightService.saveNewFlight(flightRequest));
        verify(flightRepository, times(1)).findByCodeFlight("HT763");
        verify(airportRepository, times(2)).findByCodeIATA("MAD");
        verify(airlineRepository, times(1)).findByCodeIATA("IB");
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testSaveNewFlightInvalidTimesShouldThrow() {
        FlightRequest flightRequest = new FlightRequest("HT763", "IB", "BCN", "MAD",
                OffsetDateTime.parse("2025-11-11T18:00:00+00:00"),
                OffsetDateTime.parse("2025-11-11T08:00:00+00:00"),
                260.00, 200, 200
        );
        when(flightRepository.findByCodeFlight("HT763")).thenReturn(Optional.empty());
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airportRepository.findByCodeIATA("BCN")).thenReturn(Optional.of(AirportTestData.barcelonaElPrat()));
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.of(AirportTestData.madridBarajas()));

        assertThrows(IllegalArgumentException.class, () -> flightService.saveNewFlight(flightRequest));
        verify(flightRepository, times(1)).findByCodeFlight("HT763");
        verify(airportRepository, times(1)).findByCodeIATA("BCN");
        verify(airportRepository, times(1)).findByCodeIATA("MAD");
        verify(airlineRepository, times(1)).findByCodeIATA("IB");
        verify(flightRepository, never()).save(any(Flight.class));
    }
}