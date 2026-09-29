package com.rideshare.ridesharelite.dto;

import com.rideshare.ridesharelite.entity.RequestStatus;
import com.rideshare.ridesharelite.entity.RideOffer;
import com.rideshare.ridesharelite.entity.RideRequest;

public final class Mapper {

	private Mapper() {}

	public static RideOfferResponse toResponse(RideOffer r) {
		return new RideOfferResponse(r.getId(), r.getDriver().getId(), r.getDriver().getName(), r.getOrigin(),
				r.getDestination(), r.getDepartureTime(), r.getTotalSeats(), r.getAvailableSeats(),
				r.getAvailableSeats() > 0 ? "OPEN" : "FULL");
	}

	public static RideRequestResponse toResponse(RideRequest q) {
		RideOffer r = q.getRide();
		boolean approved = q.getStatus() == RequestStatus.APPROVED;
		return new RideRequestResponse(q.getId(), r.getId(), r.getOrigin(), r.getDestination(),
				r.getDepartureTime(), r.getDriver().getId(), r.getDriver().getName(),
				approved ? r.getDriver().getPhone() : null,
				q.getRider().getId(), q.getRider().getName(),
				approved ? q.getRider().getPhone() : null,
				q.getSeatsRequested(), q.getStatus().name(), q.getRequestedAt());
	}
}
