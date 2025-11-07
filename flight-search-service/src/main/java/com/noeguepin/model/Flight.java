package com.noeguepin.model;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "flights")
public class Flight {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "flight_code", nullable = false, unique = true)
	private String codeFlight;
	@ManyToOne
	@JoinColumn(name = "airline_id", nullable = false)
	private Airline airline;
	@ManyToOne
	@JoinColumn(name = "departure_airport_id", nullable = false)
	private Airport departureAirport;
	@ManyToOne
	@JoinColumn(name = "arrival_airport_id", nullable = false)
	private Airport arrivalAirport;
	@Column(name = "departure_time", nullable = false)
	private OffsetDateTime departureTime;
	@Column(name = "arrival_time", nullable = false)
	private OffsetDateTime arrivalTime;
	private Double price;
	private Integer totalSeats;
	private Integer availableSeats;
	
	@Version
	private long version;  //Optimistic Locking: evitar que dos reservas simultaneas produzcan sobreventa
	
	public Flight() {
	}
	public Flight(String codeFlight, Airline airline, Airport departureAirport, Airport arrivalAirport,
			OffsetDateTime departureTime, OffsetDateTime arrivalTime, Double price, Integer totalSeats,
			Integer availableSeats) {
		this.codeFlight = codeFlight;
		this.airline = airline;
		this.departureAirport = departureAirport;
		this.arrivalAirport = arrivalAirport;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.price = price;
		this.totalSeats = totalSeats;
		this.availableSeats = availableSeats;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodeFlight() {
		return codeFlight;
	}
	public void setCodeFlight(String codeFlight) {
		this.codeFlight = codeFlight;
	}
	public Airline getAirline() {
		return airline;
	}
	public void setAirline(Airline airline) {
		this.airline = airline;
	}
	public Airport getDepartureAirport() {
		return departureAirport;
	}
	public void setDepartureAirport(Airport departureAirport) {
		this.departureAirport = departureAirport;
	}
	public Airport getArrivalAirport() {
		return arrivalAirport;
	}
	public void setArrivalAirport(Airport arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}
	public OffsetDateTime getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(OffsetDateTime departureTime) {
		this.departureTime = departureTime;
	}
	public OffsetDateTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(OffsetDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getTotalSeats() {
		return totalSeats;
	}
	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}
	public Integer getAvailableSeats() {
		return availableSeats;
	}
	public void setAvailableSeats(Integer availableSeats) {
		this.availableSeats = availableSeats;
	}
	public Long getVersion() {
		return version;
	}
	
	@Override
	public String toString() {
		return "Flight [id=" + id + ", codeFlight=" + codeFlight + ", airline=" + airline + ", departureAirport=" + departureAirport
				+ ", arrivalAirport=" + arrivalAirport + ", departureTime=" + departureTime + ", arrivalTime="
				+ arrivalTime + ", price=" + price + ", totalSeats=" + totalSeats + ", availableSeats=" + availableSeats
				+ "]";
	}
	
	

}
