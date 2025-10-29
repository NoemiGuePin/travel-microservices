package com.noeguepin.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "airlines")
public class Airline {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "iata_code", nullable = false, unique = true)
	private String codeIATA;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "airline", cascade = CascadeType.ALL)
	private List<Flight> flights;

	public Airline() {
	}
	
	public Airline(String code, String name) {
	    this.codeIATA = code;
	    this.name = name;
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
	public List<Flight> getFlights() {
		return flights;
	}
	
	@Override
	public String toString() {
		return "Airline [id=" + id + ", codeIATA=" + codeIATA + ", name=" + name + "]";
	}
}
