package com.noeguepin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.noeguepin.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	boolean existsByBookingCode(String bookingCode);
	Optional<Reservation> findByBookingCode(String bookingCode);
	List<Reservation> findAllByUserId(String userId);

}
