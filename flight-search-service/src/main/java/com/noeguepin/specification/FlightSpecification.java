package com.noeguepin.specification;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.noeguepin.model.Flight;

public class FlightSpecification {
	
	 private FlightSpecification() {}
	 
	    public static Specification<Flight> hasEqualJoin(String relationAttribute, String field , String value) {
	        return value == null || value.isBlank() ? null :
	        		(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(relationAttribute).get(field), value);
	    }
	    
	    public static Specification<Flight> departureFrom(OffsetDateTime departureFrom) {
	        return (departureFrom == null) ? null :
	                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("departureTime"), departureFrom);
	    }

	    public static Specification<Flight> departureTo(OffsetDateTime departureTo) {
	        return (departureTo == null) ? null :
	                (root, query, cb) -> cb.lessThanOrEqualTo(root.get("departureTime"), departureTo);
	    }

	    public static Specification<Flight> minimunPrice(Double minimunPrice) {
	        return (minimunPrice == null) ? null :
	                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minimunPrice);
	    }

	    public static Specification<Flight> maximunPrice(Double maximumPrice) {
	        return (maximumPrice == null) ? null :
	                (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maximumPrice);
	    }
}