package com.noeguepin.dto;

import com.noeguepin.model.Airport;

public record AirportResponse(
		Long id,
		String codeIATA, 
		String name,
		String city,
		String country) {
	
	public AirportResponse(Airport airport) { 
		this(airport.getId(), airport.getCodeIATA(), airport.getName(), airport.getCity(), airport.getCountry()); }

}
