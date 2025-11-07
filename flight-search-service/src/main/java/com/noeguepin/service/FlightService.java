package com.noeguepin.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noeguepin.dto.FlightRequest;
import com.noeguepin.dto.FlightResponse;
import com.noeguepin.dto.FlightSearchFilters;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.exception.SeatsException;
import com.noeguepin.exception.SeatsException.SeatsErrorCode;
import com.noeguepin.model.Airline;
import com.noeguepin.model.Airport;
import com.noeguepin.model.Flight;
import com.noeguepin.repository.AirlineRepository;
import com.noeguepin.repository.AirportRepository;
import com.noeguepin.repository.FlightRepository;
import com.noeguepin.specification.FlightSpecification;

import jakarta.ws.rs.BadRequestException;

@Service
public class FlightService {
	
	@Autowired
	FlightRepository flightRepository;
	@Autowired
	AirlineRepository airlineRepository;
	@Autowired
	AirportRepository airportRepository;

	private record FlightReferences(Airline airline, Airport departureAirport, Airport arrivalAirpot) {}
	
	@Transactional(readOnly = true)
	public List<FlightResponse> getFlightsByFilters(FlightSearchFilters flightSearchFilters, String sortBy, String direction) {
		flightSearchFilters = flightSearchFilters.normalize();
		validateRanges(flightSearchFilters);
	    Specification<Flight> specification = buildSpecification(flightSearchFilters);
	    Sort sort = buildSort(sortBy, direction);   
	    List<Flight> flightsByFilters = flightRepository.findAll(specification, sort);
	    return flightsByFilters.stream()
	    		.map(FlightResponse::new)
	            .toList();   
	}

	public FlightResponse getFlightByCodeFlight(String codeFlight) {
		return flightRepository.findByCodeFlight(codeFlight)
				.map(FlightResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("Flight", "codeFlight", codeFlight));
	}

	@Transactional
	public FlightResponse updateFlight(String codeFlight, FlightRequest flightRequest) {
		validateTimesOrder(flightRequest);
		Flight existingFlight = findFlightByCodeOrThrow(codeFlight);
		existingFlight.setDepartureTime(flightRequest.departureTime());
		existingFlight.setArrivalTime(flightRequest.arrivalTime());
		existingFlight.setPrice(flightRequest.price());
		existingFlight.setTotalSeats(flightRequest.totalSeats());
		return new FlightResponse(flightRepository.save(existingFlight));
	}
	
	@Transactional
	public void deleteFlightByCodeFlight(String codeFlight) {
		Flight existingFlight = findFlightByCodeOrThrow(codeFlight);
		flightRepository.delete(existingFlight);	
	}

	@Transactional
	public FlightResponse reserveSeats(String codeFlight, int seatsToReserve) {
		if (seatsToReserve <= 0) throw new BadRequestException("Quantity must be greater than 0");
		Flight flight = findFlightByCodeOrThrow(codeFlight);
		if (flight.getAvailableSeats() < seatsToReserve) throw new SeatsException("Not enough available seats", SeatsErrorCode.SEATS_UNAVAILABLE);
		flight.setAvailableSeats(flight.getAvailableSeats() - seatsToReserve);
		return new FlightResponse(flightRepository.save(flight));
	}
	
	@Transactional
	public FlightResponse releaseSeats(String codeFlight, int seatsToRelease) {
		if (seatsToRelease <= 0) throw new BadRequestException("Quantity must be greater than 0");
		Flight flight = findFlightByCodeOrThrow(codeFlight);
		int reservedSeats = flight.getTotalSeats() - flight.getAvailableSeats();
		if(reservedSeats < seatsToRelease) throw new SeatsException("Cannot release more seats than total capacity", SeatsErrorCode.RELEASE_EXCEEDS_RESERVED);
		flight.setAvailableSeats(flight.getAvailableSeats() + seatsToRelease);
		return new FlightResponse(flightRepository.save(flight));
	}
	
	@Transactional
	public FlightResponse saveNewFlight(FlightRequest flightRequest) {
		validateCodeFlightUniqueness(flightRequest.codeFlight());
		FlightReferences flightReferences = resolveReferences(flightRequest);
		validateAirportsDifferent(flightReferences.departureAirport, flightReferences.arrivalAirpot);
		validateTimesOrder(flightRequest);
		Flight newFlight = buildFlight(flightRequest, flightReferences);
		return new FlightResponse(flightRepository.save(newFlight));		
	}
	
	private FlightReferences resolveReferences(FlightRequest req) {
	    Airline airline = findAirlineByCodeIATAOrThrow(req.airlineCode());
	    Airport departureAirport = findAirportByCodeIATAOrThrow(req.departureAirportCode());
	    Airport arrivalAirport = findAirportByCodeIATAOrThrow(req.arrivalAirportCode());
	    return new FlightReferences(airline, departureAirport, arrivalAirport);
	}
	
	private Flight buildFlight(FlightRequest flightRequest, FlightReferences flightReferences) {
	    Flight flight = new Flight();
	    flight.setCodeFlight(flightRequest.codeFlight());
	    flight.setAirline(flightReferences.airline);
	    flight.setDepartureAirport(flightReferences.departureAirport);
	    flight.setArrivalAirport(flightReferences.arrivalAirpot);
	    flight.setDepartureTime(flightRequest.departureTime());
	    flight.setArrivalTime(flightRequest.arrivalTime());
	    flight.setPrice(flightRequest.price());
	    flight.setTotalSeats(flightRequest.totalSeats());
	    flight.setAvailableSeats(flightRequest.totalSeats());
	    return flight;
	}
	
	private void validateAirportsDifferent(Airport departureAirport, Airport arrivalAirport) {
		if (departureAirport.getCodeIATA().equals(arrivalAirport.getCodeIATA())) 
			throw new BadRequestException("Departure and arrival airports cannot be the same");
	}

	private void validateTimesOrder(FlightRequest flightRequest) {
		if (!flightRequest.arrivalTime().isAfter(flightRequest.departureTime()))
			throw new BadRequestException("Arrival time must be after departure time");
	}

	private void validateCodeFlightUniqueness(String codeFlight) {
        if (flightRepository.findByCodeFlight(codeFlight).isPresent()) {
            throw new ResourceAlreadyExistsException("Flight", "codeFlight", codeFlight);
        }
    }
	
	private Sort buildSort(String sortBy, String direction) {
	    List<String> allowedSortFields = List.of("departureTime", "price");
	    if (!allowedSortFields.contains(sortBy)) throw new BadRequestException("Invalid sort field: " + sortBy);
	    Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
	            ? Sort.Direction.DESC
	            : Sort.Direction.ASC;
	    return Sort.by(sortDirection, sortBy);

	}
	
	private Specification<Flight> buildSpecification(FlightSearchFilters flightSearchFilters) {
	    return Specification.allOf(
	    		FlightSpecification.hasEqualJoin("airline", "codeIATA", flightSearchFilters.airlineCode()),
	    	    FlightSpecification.hasEqualJoin("departureAirport", "codeIATA", flightSearchFilters.departureAirportCode()),
	    	    FlightSpecification.hasEqualJoin("arrivalAirport", "codeIATA", flightSearchFilters.arrivalAirportCode()),
	    	    FlightSpecification.departureFrom(flightSearchFilters.departureFrom()),
	    	    FlightSpecification.departureTo(flightSearchFilters.departureTo()),
	    	    FlightSpecification.minimunPrice(flightSearchFilters.minimumPrice()),
	    	    FlightSpecification.maximunPrice(flightSearchFilters.maximumPrice())
	    	    );		
	}
	
	private void validateRanges(FlightSearchFilters flightSearchFilters) {
		if (flightSearchFilters.departureFrom() != null && flightSearchFilters.departureTo() != null &&
				flightSearchFilters.departureFrom().isAfter(flightSearchFilters.departureTo())) 
			throw new BadRequestException("The start date cannot be after the end date");
	
	    if (flightSearchFilters.minimumPrice() != null && flightSearchFilters.maximumPrice() != null && 
	    		flightSearchFilters.minimumPrice() > flightSearchFilters.maximumPrice())
	        throw new BadRequestException("The minimum price cannot be greater than the maximum price");		
		
	}

	private Flight findFlightByCodeOrThrow(String codeFlight) {
	    return flightRepository.findByCodeFlight(codeFlight)
	            .orElseThrow(() -> new ResourceNotFoundException("Flight", "codeFlight", codeFlight));
	}
	
	private Airline findAirlineByCodeIATAOrThrow(String codeIATA) {
	    return airlineRepository.findByCodeIATA(codeIATA)
	            .orElseThrow(() -> new ResourceNotFoundException("Airline", "codeIATA", codeIATA));
	}
	
	private Airport findAirportByCodeIATAOrThrow(String codeIATA) {
	    return airportRepository.findByCodeIATA(codeIATA)
	            .orElseThrow(() -> new ResourceNotFoundException("Airport", "codeIATA", codeIATA));
	}

	
}