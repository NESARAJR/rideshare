package com.rideshare.ridesharelite.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.ridesharelite.dto.Mapper;
import com.rideshare.ridesharelite.dto.RideRequestCreate;
import com.rideshare.ridesharelite.dto.RideRequestResponse;
import com.rideshare.ridesharelite.entity.RequestStatus;
import com.rideshare.ridesharelite.entity.RideOffer;
import com.rideshare.ridesharelite.entity.RideRequest;
import com.rideshare.ridesharelite.entity.User;
import com.rideshare.ridesharelite.exception.BusinessRuleException;
import com.rideshare.ridesharelite.exception.ResourceNotFoundException;
import com.rideshare.ridesharelite.repository.RideOfferRepository;
import com.rideshare.ridesharelite.repository.RideRequestRepository;

@Service
public class RideRequestService {

	private final RideRequestRepository requestRepository;
	private final RideOfferRepository rideOfferRepository;
	private final RideOfferService rideOfferService;
	private final UserService userService;
	private final NotificationService notificationService;

	public RideRequestService(RideRequestRepository requestRepository, RideOfferRepository rideOfferRepository,
			RideOfferService rideOfferService, UserService userService, NotificationService notificationService) {
		this.requestRepository = requestRepository;
		this.rideOfferRepository = rideOfferRepository;
		this.rideOfferService = rideOfferService;
		this.userService = userService;
		this.notificationService = notificationService;
	}

	/** Feature 3: rider requests a seat. */
	@Transactional
	public RideRequestResponse create(RideRequestCreate dto) {
		RideOffer ride = rideOfferService.getEntity(dto.rideId());
		User rider = userService.get(dto.riderId());

		// BUSINESS RULE: a user cannot request a seat on their own published ride
		if (ride.getDriver().getId().equals(rider.getId())) {
			throw new BusinessRuleException("You cannot request a seat on your own published ride",
					HttpStatus.BAD_REQUEST);
		}
		if (dto.seats() > ride.getTotalSeats()) {
			throw new BusinessRuleException(
					"This ride only has " + ride.getTotalSeats() + " seat(s) in total", HttpStatus.BAD_REQUEST);
		}
		if (ride.getDepartureTime().isBefore(LocalDateTime.now())) {
			throw new BusinessRuleException("This ride has already departed", HttpStatus.BAD_REQUEST);
		}
		if (requestRepository.existsByRideIdAndRiderIdAndStatusIn(ride.getId(), rider.getId(),
				List.of(RequestStatus.PENDING, RequestStatus.APPROVED))) {
			throw new BusinessRuleException("You already have an active request for this ride", HttpStatus.CONFLICT);
		}

		RideRequest request = new RideRequest();
		request.setRide(ride);
		request.setRider(rider);
		request.setSeatsRequested(dto.seats());
		request.setStatus(RequestStatus.PENDING);
		RideRequest saved = requestRepository.save(request);
		notificationService.requestCreated(saved);
		return Mapper.toResponse(saved);
	}

	/** Feature 3 + 4: driver approves; approved requests reduce available seats. */
	@Transactional
	public RideRequestResponse approve(Long requestId, Long driverId) {
		RideRequest request = getEntity(requestId);
		RideOffer ride = request.getRide();
		checkDriverOwnsRide(ride, driverId);
		checkPending(request);

		// BUSINESS RULE: cannot approve if no seats remain - checked BEFORE saving
		if (ride.getAvailableSeats() <= 0) {
			throw new BusinessRuleException("Cannot approve request: no seats remain on this ride", HttpStatus.CONFLICT);
		}
		if (request.getSeatsRequested() > ride.getAvailableSeats()) {
			throw new BusinessRuleException("Cannot approve request: it needs " + request.getSeatsRequested()
					+ " seat(s) but only " + ride.getAvailableSeats() + " remain", HttpStatus.CONFLICT);
		}

		ride.setAvailableSeats(ride.getAvailableSeats() - request.getSeatsRequested());
		request.setStatus(RequestStatus.APPROVED);
		rideOfferRepository.save(ride);
		RideRequest saved = requestRepository.save(request);
		notificationService.statusChanged(saved);
		return Mapper.toResponse(saved);
	}

	@Transactional
	public RideRequestResponse reject(Long requestId, Long driverId) {
		RideRequest request = getEntity(requestId);
		checkDriverOwnsRide(request.getRide(), driverId);
		checkPending(request);
		request.setStatus(RequestStatus.REJECTED);
		RideRequest saved = requestRepository.save(request);
		notificationService.statusChanged(saved);
		return Mapper.toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<RideRequestResponse> forRide(Long rideId) {
		rideOfferService.getEntity(rideId);
		return requestRepository.findByRideIdOrderByRequestedAtAsc(rideId).stream().map(Mapper::toResponse).toList();
	}

	private RideRequest getEntity(Long id) {
		return requestRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ride request not found with id " + id));
	}

	private void checkDriverOwnsRide(RideOffer ride, Long driverId) {
		if (!ride.getDriver().getId().equals(driverId)) {
			throw new BusinessRuleException("Only the driver of this ride can approve or reject requests",
					HttpStatus.FORBIDDEN);
		}
	}

	private void checkPending(RideRequest request) {
		if (request.getStatus() != RequestStatus.PENDING) {
			throw new BusinessRuleException("This request is already " + request.getStatus(), HttpStatus.CONFLICT);
		}
	}
}
