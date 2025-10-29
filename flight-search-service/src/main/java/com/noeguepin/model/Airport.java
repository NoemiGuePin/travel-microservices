package com.noeguepin.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "airports")
public class Airport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "iata_code", nullable = false, unique = true)
	private String codeIATA;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "city", nullable = false)
	private String city;
	@Column(name = "country", nullable = false)
	private String country;
	
	@OneToMany(mappedBy = "departureAirport", cascade = CascadeType.ALL)
	private List<Flight> departureFlights;
	
	@OneToMany(mappedBy = "arrivalAirport", cascade = CascadeType.ALL)
	private List<Flight> arrivalFlights;

	public Airport() {
	}
	public Airport(String codeIATA, String name, String city, String country) {
		this.codeIATA = codeIATA;
		this.name = name;
		this.city = city;
		this.country = country;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodeIATA() {
		return codeIATA;
	}
	public void setCodeIATA(String codeIATA) {
		this.codeIATA = codeIATA;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public List<Flight> getDepartureFlights() {
		return departureFlights;
	}
	public List<Flight> getArrivalFlights() {
		return arrivalFlights;
	}
	
	@Override
	public String toString() {
		return "Airport [id=" + id + ", codeIATA=" + codeIATA + ", name=" + name + ", city=" + city + ", country=" + country
				+ "]";
	}
}
