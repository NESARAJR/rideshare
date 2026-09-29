package com.rideshare.ridesharelite.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "ride_offers")
public class RideOffer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Many rides -> one driver (User)
	@ManyToOne(optional = false)
	@JoinColumn(name = "driver_id", nullable = false)
	private User driver;

	@Column(nullable = false)
	private String origin;

	@Column(nullable = false)
	private String destination;

	@Column(nullable = false)
	private LocalDateTime departureTime;

	@Column(nullable = false)
	private int totalSeats;

	@Column(nullable = false)
	private int availableSeats;

	// Optimistic locking: prevents two simultaneous approvals over-booking seats
	@Version
	private Long version;

	@JsonIgnore
	@OneToMany(mappedBy = "ride", cascade = CascadeType.REMOVE)
	private List<RideRequest> requests = new ArrayList<>();

	public RideOffer() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public User getDriver() { return driver; }
	public void setDriver(User driver) { this.driver = driver; }
	public String getOrigin() { return origin; }
	public void setOrigin(String origin) { this.origin = origin; }
	public String getDestination() { return destination; }
	public void setDestination(String destination) { this.destination = destination; }
	public LocalDateTime getDepartureTime() { return departureTime; }
	public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
	public int getTotalSeats() { return totalSeats; }
	public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
	public int getAvailableSeats() { return availableSeats; }
	public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
	public Long getVersion() { return version; }
	public void setVersion(Long version) { this.version = version; }
	public List<RideRequest> getRequests() { return requests; }
	public void setRequests(List<RideRequest> requests) { this.requests = requests; }
}
