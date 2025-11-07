package com.noeguepin.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "booking_code", nullable = false, unique = true)
	private String bookingCode;
	@Column(name = "flight_id", nullable = false)
	private String flightCode;
	@Column(name = "user_id", nullable
			= false)
	private String userId;
	@Column(name = "passengers_number", nullable = false)
	private Integer passengersNumber;
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;
	@UpdateTimestamp
	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;
	
	public Reservation() {
		
	}

	public Reservation(String bookingCode, String flightCode, String userId, Integer passengersNumber) {
		this.bookingCode = bookingCode;
		this.flightCode = flightCode;
		this.userId = userId;
		this.passengersNumber = passengersNumber;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBookingCode() {
		return bookingCode;
	}

	public void setBookingCode(String bookingCode) {
		this.bookingCode = bookingCode;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getPassengersNumber() {
		return passengersNumber;
	}

	public void setPassengersNumber(Integer passengersNumber) {
		this.passengersNumber = passengersNumber;
	}
	
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
}
