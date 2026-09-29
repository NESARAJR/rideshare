package com.rideshare.ridesharelite.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RideOfferCreateRequest(
		@NotNull(message = "driverId is required") Long driverId,
		@NotBlank(message = "Origin is required") String origin,
		@NotBlank(message = "Destination is required") String destination,
		@NotNull(message = "Departure time is required") @Future(message = "Departure time must be in the future") LocalDateTime departureTime,
		@NotNull(message = "Seats available is required") @Positive(message = "Seats must be greater than 0") Integer seatsAvailable) {
}
