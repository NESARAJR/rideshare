package com.rideshare.ridesharelite.dto;

import java.time.LocalDateTime;

public record RideOfferResponse(Long id, Long driverId, String driverName, String origin, String destination,
		LocalDateTime departureTime, int totalSeats, int availableSeats, String status) {
}
