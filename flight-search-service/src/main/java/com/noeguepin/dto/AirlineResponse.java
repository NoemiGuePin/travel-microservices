package com.noeguepin.dto;

import com.noeguepin.model.Airline;

public record AirlineResponse( 
		Long id,
		String codeIATA, 
		String name) {
	
    public AirlineResponse(Airline airline) { 
    	this(airline.getId(), airline.getCodeIATA(), airline.getName()); }
}
