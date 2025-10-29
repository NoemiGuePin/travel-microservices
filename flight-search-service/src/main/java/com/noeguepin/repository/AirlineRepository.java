package com.noeguepin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.noeguepin.model.Airline;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
	Optional<Airline> findByCodeIATA(String codeIATA);
	List<Airline> findAirlinesByNameContainingIgnoreCase(String name);
	@Query("""
        SELECT airline FROM Airline airline
        WHERE (:name IS NULL OR LOWER(airline.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    List<Airline> findAirlinesByFilters(
    	@Param("name") String name);
	
}
