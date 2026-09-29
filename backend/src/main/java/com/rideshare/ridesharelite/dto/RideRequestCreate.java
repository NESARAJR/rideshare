package com.rideshare.ridesharelite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RideRequestCreate(
		@NotNull(message = "rideId is required") Long rideId,
		@NotNull(message = "riderId is required") Long riderId,
		@NotNull(message = "Number of seats is required") @Positive(message = "Seats must be at least 1") Integer seats) {
}
