package com.noeguepin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noeguepin.dto.AirportRequest;
import com.noeguepin.dto.AirportResponse;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.model.Airline;
import com.noeguepin.model.Airport;
import com.noeguepin.repository.AirportRepository;

@Service
public class AirportService {
	
	@Autowired
	AirportRepository airportRepository;

	public List<AirportResponse> getAllAirports() {
		return airportRepository.findAll().stream()
	        .map(AirportResponse::new)
	        .toList();
	}

	public AirportResponse getAirportByCodeIATA(String codeIATA) {
		return airportRepository.findByCodeIATA(codeIATA)
				.map(AirportResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("Airport", "codeIATA", codeIATA)); 
	}

	public List<AirportResponse> getAirportsByFilters(String city, String country) {		
		return airportRepository.findAirportsByFilters(city, country).stream()
				.map(AirportResponse::new)
				.toList();
	}

	public AirportResponse saveNewAirport(AirportRequest newAirportRequest) {
		validateCodeIATAAirportUniqueness(newAirportRequest.codeIATA());
		Airport newAirport = new Airport(newAirportRequest.codeIATA(), 
				newAirportRequest.name(), 
				newAirportRequest.city(), 
				newAirportRequest.country());
		return new AirportResponse(airportRepository.save(newAirport));
	}

	public AirportResponse updateAirport(String codeIATA, AirportRequest airportRequest) {
		Airport existingAirport = findAirportByCodeIATAOrThrow(codeIATA);
		existingAirport.setName(airportRequest.name());
		existingAirport.setCity(airportRequest.city());
		existingAirport.setCountry(airportRequest.country());
		return new AirportResponse(airportRepository.save(existingAirport));
	}

	public void deleteAirportByCodeIATA(String codeIATA) {
		Airport existingAirport = findAirportByCodeIATAOrThrow(codeIATA);
		airportRepository.delete(existingAirport);
	}

	private void validateCodeIATAAirportUniqueness(String codeIATAAirport) {
        if (airportRepository.findByCodeIATA(codeIATAAirport).isPresent()) {
            throw new ResourceAlreadyExistsException("Airport", "codeIATA", codeIATAAirport);
        }
    }
	
	private Airport findAirportByCodeIATAOrThrow(String codeIATA) {
	    return airportRepository.findByCodeIATA(codeIATA)
	            .orElseThrow(() -> new ResourceNotFoundException("Airport", "codeIATA", codeIATA));
	}

}
