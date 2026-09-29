package com.rideshare.ridesharelite.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
	private String phone;

	// One user -> many rides published as a driver
	@JsonIgnore
	@OneToMany(mappedBy = "driver")
	private List<RideOffer> offeredRides = new ArrayList<>();

	// One user -> many seat requests made as a rider
	@JsonIgnore
	@OneToMany(mappedBy = "rider")
	private List<RideRequest> rideRequests = new ArrayList<>();

	public User() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public List<RideOffer> getOfferedRides() { return offeredRides; }
	public void setOfferedRides(List<RideOffer> offeredRides) { this.offeredRides = offeredRides; }
	public List<RideRequest> getRideRequests() { return rideRequests; }
	public void setRideRequests(List<RideRequest> rideRequests) { this.rideRequests = rideRequests; }
}
