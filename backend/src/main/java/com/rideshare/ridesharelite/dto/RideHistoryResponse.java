package com.rideshare.ridesharelite.dto;

import java.util.List;

public record RideHistoryResponse(Long userId, String userName, List<RideOfferResponse> asDriver,
		List<RideRequestResponse> asRider) {
}
