package com.noeguepin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

import com.noeguepin.dto.AirportRequest;
import com.noeguepin.dto.AirportResponse;
import com.noeguepin.service.AirportService;

@RestController
@RequestMapping("/airports")
@CrossOrigin(origins = "*")
public class AirportController {
	
	@Autowired
	AirportService airportService;
	
	@GetMapping
	public ResponseEntity<List<AirportResponse>> getAirportsByFilters(
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String country) {
	    city = StringUtils.hasText(city) ? city.trim() : null;
	    country = StringUtils.hasText(country) ? country.trim() : null;
		return ResponseEntity.ok(airportService.getAirportsByFilters(city, country));
	}
	
	@GetMapping("/{codeIATA}")
	public ResponseEntity<AirportResponse> getAirportByCodeIATA(@PathVariable String codeIATA) {
		return ResponseEntity.ok(airportService.getAirportByCodeIATA(codeIATA));
	}
	
	@PostMapping
	public ResponseEntity<AirportResponse> saveNewAirport(@RequestBody AirportRequest newAirport) {
		AirportResponse newAirportSaved = airportService.saveNewAirport(newAirport);
		URI location = URI.create("/airports/" + newAirportSaved.codeIATA());
		return ResponseEntity.created(location).body(newAirportSaved);	
	}
	
	@PutMapping("/{codeIATA}")
	public ResponseEntity<AirportResponse> updateAirport(@PathVariable String codeIATA, @RequestBody AirportRequest airportRequest) {
		return ResponseEntity.ok(airportService.updateAirport(codeIATA, airportRequest));
	}

	@DeleteMapping("/{codeIATA}")
	public ResponseEntity<Void> deleteAirport(@PathVariable String codeIATA) {
	    airportService.deleteAirportByCodeIATA(codeIATA);
	    return ResponseEntity.noContent().build();
	}
}
