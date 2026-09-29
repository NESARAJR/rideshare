package com.rideshare.ridesharelite.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "ride_requests")
public class RideRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Many requests -> one ride
	@ManyToOne(optional = false)
	@JoinColumn(name = "ride_id", nullable = false)
	private RideOffer ride;

	// Many requests -> one rider (User)
	@ManyToOne(optional = false)
	@JoinColumn(name = "rider_id", nullable = false)
	private User rider;

	// Number of seats the rider wants on this ride
	@Column(nullable = false, columnDefinition = "int default 1")
	private int seatsRequested = 1;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RequestStatus status = RequestStatus.PENDING;

	@Column(nullable = false)
	private LocalDateTime requestedAt = LocalDateTime.now();

	public RideRequest() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public RideOffer getRide() { return ride; }
	public void setRide(RideOffer ride) { this.ride = ride; }
	public User getRider() { return rider; }
	public void setRider(User rider) { this.rider = rider; }
	public int getSeatsRequested() { return seatsRequested; }
	public void setSeatsRequested(int seatsRequested) { this.seatsRequested = seatsRequested; }
	public RequestStatus getStatus() { return status; }
	public void setStatus(RequestStatus status) { this.status = status; }
	public LocalDateTime getRequestedAt() { return requestedAt; }
	public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}
