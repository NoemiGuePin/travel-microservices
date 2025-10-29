package com.noeguepin.controller;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noeguepin.dto.FlightRequest;
import com.noeguepin.dto.FlightResponse;
import com.noeguepin.dto.FlightSearchFilters;
import com.noeguepin.service.FlightService;

@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "*")
public class FlightController {
	
	@Autowired
	FlightService flightService;
	
	@GetMapping
	public ResponseEntity<List<FlightResponse>> getFlightsByFilters(
			@RequestParam(required = false) String airline,
			@RequestParam(required = false) String departureAirport,
			@RequestParam(required = false) String arrivalAirport,
			@RequestParam(required = false) OffsetDateTime departureFrom,
			@RequestParam(required = false) OffsetDateTime departureTo,
			@RequestParam(required = false) Double minimumPrice,
			@RequestParam(required = false) Double maximunPrice,
    		@RequestParam(required = false, defaultValue = "departureTime") String sortBy,
    		@RequestParam(required = false, defaultValue = "ASC") String direction){
		
	    FlightSearchFilters flightsSearchFilters = new FlightSearchFilters(
	    		airline, departureAirport, arrivalAirport,
	            departureFrom, departureTo, minimumPrice, maximunPrice
	    );
		
		return ResponseEntity.ok(flightService.getFlightsByFilters(flightsSearchFilters, sortBy, direction));
	}
			
	
	@GetMapping("/{codeFlight}")
	public ResponseEntity<FlightResponse> getFlightByCodeFlight(@PathVariable String codeFlight) {
		return ResponseEntity.ok(flightService.getFlightByCodeFlight(codeFlight)); 	
	}
	
	@PostMapping
	public ResponseEntity<FlightResponse> createFlight(@RequestBody FlightRequest flightRequest) {
	    FlightResponse newFlightSaved = flightService.saveNewFlight(flightRequest);
	    URI location = URI.create("/api/flights/" + newFlightSaved.codeFlight());
	    return ResponseEntity.created(location).body(newFlightSaved);
	}

	@PutMapping("/{codeFlight}")
	public ResponseEntity<FlightResponse> updateFlight(@PathVariable String codeFlight, @RequestBody FlightRequest flightRequest) {
	    return ResponseEntity.ok(flightService.updateFlight(codeFlight, flightRequest));
	}
	
	@PostMapping("/{codeFlight}/seats/reserve")
	public ResponseEntity<FlightResponse> reserveSeats(@PathVariable String codeFlight, @RequestParam int seatsToReserve){
		return ResponseEntity.ok(flightService.reserveSeats(codeFlight, seatsToReserve));
	}
	
	@PostMapping("/{codeFlight}/seats/release")
	public ResponseEntity<FlightResponse> releaseSeats(@PathVariable String codeFlight, @RequestParam int seatsToRelease){
		return ResponseEntity.ok(flightService.releaseSeats(codeFlight, seatsToRelease));
	}
	
	@DeleteMapping("/{codeFlight}")
	public ResponseEntity<Void> deleteFlight(@PathVariable String codeFlight) {
	    flightService.deleteFlightByCodeFlight(codeFlight);
	    return ResponseEntity.noContent().build();
	}
}
