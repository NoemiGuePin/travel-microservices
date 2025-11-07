package com.noeguepin.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.noeguepin.dto.ReservationRequest;
import com.noeguepin.dto.ReservationResponse;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.exception.SeatsException;
import com.noeguepin.model.Reservation;
import com.noeguepin.repository.ReservationRepository;
import com.noeguepin.security.AuthUser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Mono;

@Service
public class ReservationService {
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	private final WebClient flightWebClient;
	
	private final AuthUser authUser;

	public ReservationService(ReservationRepository reservationRepository,
            WebClient flightWebClient, AuthUser authUser) {
		this.reservationRepository = reservationRepository;
		this.flightWebClient = flightWebClient;
		this.authUser = authUser;
	}
	
	public List<ReservationResponse> findVisibleBookings() {
		if (isAdmin()) return getAllBookings();
		return getBookingsByUserId();
	}
	
	public ReservationResponse findVisibleBookingByCode(String bookingCode) throws AccessDeniedException {
		Reservation reservation = findReservationByBookingCodeOrThrow(bookingCode);
		assertOwnerOrAdmin(reservation);
		return new ReservationResponse(reservation);
	}

	@Transactional
	public ReservationResponse saveNewReservation(ReservationRequest reservationRequest) {
		reserveSeatOrFail(reservationRequest.flightCode(), reservationRequest.passengersNumber());
		Reservation newReservation = new Reservation(generateBookingCode(),
				reservationRequest.flightCode(), 
				authUser.sub(),
				reservationRequest.passengersNumber());
		return new ReservationResponse(reservationRepository.save(newReservation));
	}
	
	@Transactional
	public ReservationResponse updateBooking(String bookingCode, int newPassengersNumber) throws AccessDeniedException {
		Reservation reservation = findReservationByBookingCodeOrThrow(bookingCode);
		assertOwnerOrAdmin(reservation);
		int passengerDifference = newPassengersNumber - reservation.getPassengersNumber();
		if(passengerDifference > 0) return increaseNumberPassengersBooking(reservation, passengerDifference);
		else if (passengerDifference < 0) return decreaseNumberPassengersBooking(reservation, Math.abs(passengerDifference));
		else return new ReservationResponse(reservation);
	}

	@Transactional
	public void deleteReservationByBookingCode(String bookingCode) throws AccessDeniedException {
		Reservation reservation = findReservationByBookingCodeOrThrow(bookingCode);
		assertOwnerOrAdmin(reservation);
		releaseSeatOrFail(reservation.getFlightCode(), reservation.getPassengersNumber());
		reservationRepository.delete(reservation);		
	}
	
	private String generateBookingCode() {
	    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    int length = 8; 
	    String code = "";
	    
	    do {
	        for (int i = 0; i < length; i++) {
	            int index = (int) (Math.random() * characters.length());
	            code += characters.charAt(index);
	        }

	    } while (reservationRepository.existsByBookingCode(code));

	    return code;
	}
	
	private Reservation findReservationByBookingCodeOrThrow(String bookingCode) {
	    return reservationRepository.findByBookingCode(bookingCode)
	            .orElseThrow(() -> new ResourceNotFoundException("Reservation", "bookingCode", bookingCode));
	}
	
	private void reserveSeatOrFail(String flightCode, int seatsToReserve) {
		flightWebClient.post()
			.uri(uriBuilder -> uriBuilder
					.path("/flights/internal/{codeFlight}/seats/reserve")
					.queryParam("seatsToReserve", seatsToReserve)
					.build(flightCode))
			.retrieve()
	        .onStatus(status -> status.value() == 404,
	        		response -> Mono.error(new ResourceNotFoundException("Flight", "codeFlight", flightCode)))
		    .onStatus(status -> status.value() == 409,
		    		response -> Mono.error(new SeatsException("Not enough seats available", SeatsException.SeatsErrorCode.SEATS_UNAVAILABLE)))
		    .onStatus(status -> status.value() == 400,
		    		response -> Mono.error(new IllegalArgumentException("Invalid seat quantity")))
		    .toBodilessEntity()
		    .block();
	}
	
	private void releaseSeatOrFail(String flightCode, int seatsToRelease) {
		flightWebClient.post()
			.uri(uriBuilder -> uriBuilder
					.path("/flights/internal/{codeFlight}/seats/release")
					.queryParam("seatsToRelease", seatsToRelease)
					.build(flightCode))
			.retrieve()
	        .onStatus(status -> status.value() == 404,
	        		response -> Mono.error(new ResourceNotFoundException("Flight", "codeFlight", flightCode)))
		    .onStatus(status -> status.value() == 409,
		    		response -> Mono.error(new SeatsException("Cannot release more seats than reserved", SeatsException.SeatsErrorCode.RELEASE_EXCEEDS_RESERVED)))
		    .onStatus(status -> status.value() == 400,
		    		response -> Mono.error(new IllegalArgumentException("Invalid seat quantity")))
		    .toBodilessEntity()
		    .block();
	}

	private ReservationResponse decreaseNumberPassengersBooking(Reservation reservation, int passengerDifference ) {
		releaseSeatOrFail(reservation.getFlightCode(), passengerDifference);
		reservation.setPassengersNumber(reservation.getPassengersNumber() - passengerDifference);
		return new ReservationResponse(reservationRepository.save(reservation));
	}

	private ReservationResponse increaseNumberPassengersBooking(Reservation reservation, int passengerDifference ) {
		reserveSeatOrFail(reservation.getFlightCode(), passengerDifference);
		reservation.setPassengersNumber(reservation.getPassengersNumber() + passengerDifference);
		return new ReservationResponse(reservationRepository.save(reservation));
	}

	
	private List<ReservationResponse> getBookingsByUserId(){
		return reservationRepository.findAllByUserId(authUser.sub())
				.stream()
				.map(ReservationResponse::new)
				.toList();		
	}
	
	private List<ReservationResponse> getAllBookings(){
		 return reservationRepository.findAll()
	                .stream().map(ReservationResponse::new).toList();	
	}	
	
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("booking-admin"));
    }

    private void assertOwnerOrAdmin(Reservation reservation) throws AccessDeniedException {
        if (isAdmin()) return;
        if (!authUser.sub().equals(reservation.getUserId())) {
            throw new AccessDeniedException("You need to be the administrator or owner of the reservation");
        }
    }
	
}
