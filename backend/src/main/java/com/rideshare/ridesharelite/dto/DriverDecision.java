package com.rideshare.ridesharelite.dto;

import jakarta.validation.constraints.NotNull;

public record DriverDecision(@NotNull(message = "driverId is required") Long driverId) {
}
