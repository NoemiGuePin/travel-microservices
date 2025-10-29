package com.noeguepin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;

import com.noeguepin.dto.AirlineRequest;
import com.noeguepin.dto.AirlineResponse;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.model.Airline;
import com.noeguepin.repository.AirlineRepository;

@Service
public class AirlineService {
	
	@Autowired
	private AirlineRepository airlineRepository;

	public List<AirlineResponse> getAirlineByFilters(String name) {
		return airlineRepository.findAirlinesByFilters(name).stream()
				.map(AirlineResponse::new)
				.toList();
	}

	public AirlineResponse getAirlineByCodeIATA(String codeIATA) {
		return airlineRepository.findByCodeIATA(codeIATA)
				.map(AirlineResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("Airline", "codeIATA", codeIATA));
	}
	
	public AirlineResponse saveNewAirline(AirlineRequest newAirlinerRequest) {
		validateCodeIATAAirlineUniqueness(newAirlinerRequest.codeIATA());
		Airline newAirline = new Airline(newAirlinerRequest.codeIATA(), newAirlinerRequest.name());
		return new AirlineResponse(airlineRepository.save(newAirline));
	}

	public AirlineResponse updateAirline(String codeIATA, AirlineRequest airlineRequest) {
	    Airline existingAirline = findAirlineByCodeIATAOrThrow(codeIATA);
	    existingAirline.setName(airlineRequest.name());
	    Airline updatedAirline = airlineRepository.save(existingAirline);
		return new AirlineResponse(updatedAirline);
	}

	public void deleteAirlineByCodeIATA(String codeIATA) {
	    Airline existingAirline = findAirlineByCodeIATAOrThrow(codeIATA);
	    airlineRepository.delete(existingAirline);	    		
	}
	
	private void validateCodeIATAAirlineUniqueness(String codeIATAAirline) {
        if (airlineRepository.findByCodeIATA(codeIATAAirline).isPresent()) {
            throw new ResourceAlreadyExistsException("Airline", "codeIATA", codeIATAAirline);
        }
    }
	
	private Airline findAirlineByCodeIATAOrThrow(String codeIATA) {
	    return airlineRepository.findByCodeIATA(codeIATA)
	            .orElseThrow(() -> new ResourceNotFoundException("Airline", "codeIATA", codeIATA));
	}

}
