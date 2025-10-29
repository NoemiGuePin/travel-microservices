package com.noeguepin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.noeguepin.dto.AirlineRequest;
import com.noeguepin.dto.AirlineResponse;
import com.noeguepin.model.Airline;
import com.noeguepin.service.AirlineService;

@RestController
@RequestMapping("/api/airlines")
@CrossOrigin(origins = "*")
public class AirlineController {
	
	@Autowired
	AirlineService airlineService;
	
	@GetMapping
	public ResponseEntity<List<AirlineResponse>> getAirlineByFilter(@RequestParam(required = false) String name) {
		name = StringUtils.hasText(name) ? name.trim() : null;
		return ResponseEntity.ok(airlineService.getAirlineByFilters(name)); 	
	}
	
	@GetMapping("/{codeIATA}")
	public ResponseEntity<AirlineResponse> getAirlineByCodeIATA(@PathVariable String codeIATA) {
		return ResponseEntity.ok(airlineService.getAirlineByCodeIATA(codeIATA)); 	
	}
	
	@PostMapping
	public ResponseEntity<AirlineResponse> saveNewAirline(@RequestBody AirlineRequest newAirline) {
		AirlineResponse newAirlineSaved = airlineService.saveNewAirline(newAirline);
		URI location = URI.create("/api/airlines/" + newAirlineSaved.codeIATA());
		return ResponseEntity.created(location).body(newAirlineSaved);	
	}
	
	@PutMapping("/{codeIATA}")
	public ResponseEntity<AirlineResponse> updateAirline(@PathVariable String codeIATA, @RequestBody AirlineRequest airlineRequest) {
	    return ResponseEntity.ok(airlineService.updateAirline(codeIATA, airlineRequest));
	}
	
	@DeleteMapping("/{codeIATA}")
	public ResponseEntity<Void> deleteAirline(@PathVariable String codeIATA) {
	    airlineService.deleteAirlineByCodeIATA(codeIATA);
	    return ResponseEntity.noContent().build();
	}
}
