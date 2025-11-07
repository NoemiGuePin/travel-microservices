package com.noeguepin.controller;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noeguepin.dto.ReservationRequest;
import com.noeguepin.dto.ReservationResponse;
import com.noeguepin.service.ReservationService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class ReservationController {

	@Autowired
	ReservationService reservationService;
	
	@GetMapping("/{bookingCode}")
	public ResponseEntity<ReservationResponse> findVisibleBookingByCode(@PathVariable String bookingCode) throws AccessDeniedException{
		return ResponseEntity.ok(reservationService.findVisibleBookingByCode(bookingCode));
	}
	
	@GetMapping
	public ResponseEntity<List<ReservationResponse>> findVisibleBookings() {
	    return ResponseEntity.ok(reservationService.findVisibleBookings());
	}
	
	@PostMapping
	public ResponseEntity<ReservationResponse> saveNewReservation(@Valid @RequestBody ReservationRequest newReservation){
		ReservationResponse newReservationSaved = reservationService.saveNewReservation(newReservation);
		URI location = URI.create("/bookings/" + newReservationSaved.bookingCode());
		return ResponseEntity.created(location).body(newReservationSaved);
	}
	
	@PutMapping("/{bookingCode}")
	public ResponseEntity<ReservationResponse> updateBooking(@PathVariable String bookingCode, @Valid @RequestBody ReservationRequest reservationRequest) throws AccessDeniedException {
	    return ResponseEntity.ok(reservationService.updateBooking(bookingCode, reservationRequest.passengersNumber()));
	}
	
	@DeleteMapping("/{bookingCode}")
	public ResponseEntity<Void> deleteBooking(@PathVariable String bookingCode) throws AccessDeniedException {
		reservationService.deleteReservationByBookingCode(bookingCode);
	    return ResponseEntity.noContent().build();
	}	
	
}
