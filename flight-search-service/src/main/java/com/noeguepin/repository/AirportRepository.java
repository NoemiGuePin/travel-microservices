package com.noeguepin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.noeguepin.model.Airport;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
	Optional<Airport> findByCodeIATA(String codeIATA);	
	@Query("""
		SELECT airport FROM Airport airport 
		WHERE (:city IS NULL OR LOWER(airport.city) = LOWER(:city))
		AND (:country IS NULL OR LOWER(airport.country) = LOWER(:country))
	""")	
	List<Airport> findAirportsByFilters(
		@Param("city") String city,
		@Param("country") String country);

}
