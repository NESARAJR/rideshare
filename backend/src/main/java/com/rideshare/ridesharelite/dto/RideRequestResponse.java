package com.rideshare.ridesharelite.dto;

import java.time.LocalDateTime;

/**
 * driverPhone / riderPhone are only filled in once the request is APPROVED,
 * so contact numbers are exchanged only after the driver accepts.
 */
public record RideRequestResponse(Long id, Long rideId, String origin, String destination,
		LocalDateTime departureTime, Long driverId, String driverName, String driverPhone,
		Long riderId, String riderName, String riderPhone, int seatsRequested,
		String status, LocalDateTime requestedAt) {
}
