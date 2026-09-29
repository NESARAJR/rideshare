package com.rideshare.ridesharelite.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.ridesharelite.dto.Mapper;
import com.rideshare.ridesharelite.dto.RideOfferCreateRequest;
import com.rideshare.ridesharelite.dto.RideOfferResponse;
import com.rideshare.ridesharelite.entity.RideOffer;
import com.rideshare.ridesharelite.exception.BusinessRuleException;
import com.rideshare.ridesharelite.exception.ResourceNotFoundException;
import com.rideshare.ridesharelite.repository.RideOfferRepository;

@Service
public class RideOfferService {

	private final RideOfferRepository rideOfferRepository;
	private final UserService userService;

	public RideOfferService(RideOfferRepository rideOfferRepository, UserService userService) {
		this.rideOfferRepository = rideOfferRepository;
		this.userService = userService;
	}

	/** Feature 1: driver publishes a route. */
	@Transactional
	public RideOfferResponse publish(RideOfferCreateRequest dto) {
		RideOffer ride = new RideOffer();
		ride.setDriver(userService.get(dto.driverId()));
		ride.setOrigin(dto.origin().trim());
		ride.setDestination(dto.destination().trim());
		ride.setDepartureTime(dto.departureTime());
		ride.setTotalSeats(dto.seatsAvailable());
		ride.setAvailableSeats(dto.seatsAvailable());
		return Mapper.toResponse(rideOfferRepository.save(ride));
	}

	/** Feature 2: rider searches by route and time window. */
	@Transactional(readOnly = true)
	public List<RideOfferResponse> search(String origin, String destination, LocalDateTime from, LocalDateTime to) {
		if (to.isBefore(from)) {
			throw new BusinessRuleException("'to' time must not be before 'from' time", HttpStatus.BAD_REQUEST);
		}
		return rideOfferRepository.search(origin.trim(), destination.trim(), from, to).stream()
				.map(Mapper::toResponse).toList();
	}

	/** Paginated + sortable list of all rides. */
	@Transactional(readOnly = true)
	public Page<RideOfferResponse> findAll(Pageable pageable) {
		return rideOfferRepository.findAll(pageable).map(Mapper::toResponse);
	}

	public RideOffer getEntity(Long id) {
		return rideOfferRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ride not found with id " + id));
	}

	@Transactional(readOnly = true)
	public RideOfferResponse get(Long id) {
		return Mapper.toResponse(getEntity(id));
	}

	@Transactional
	public RideOfferResponse update(Long id, RideOfferCreateRequest dto) {
		RideOffer ride = getEntity(id);
		if (!ride.getDriver().getId().equals(dto.driverId())) {
			throw new BusinessRuleException("Only the driver who published this ride can update it", HttpStatus.FORBIDDEN);
		}
		int alreadyApproved = ride.getTotalSeats() - ride.getAvailableSeats();
		if (dto.seatsAvailable() < alreadyApproved) {
			throw new BusinessRuleException(
					"Total seats cannot be less than the " + alreadyApproved + " seat(s) already approved",
					HttpStatus.BAD_REQUEST);
		}
		ride.setOrigin(dto.origin().trim());
		ride.setDestination(dto.destination().trim());
		ride.setDepartureTime(dto.departureTime());
		ride.setTotalSeats(dto.seatsAvailable());
		ride.setAvailableSeats(dto.seatsAvailable() - alreadyApproved);
		return Mapper.toResponse(rideOfferRepository.save(ride));
	}

	@Transactional
	public void delete(Long id, Long driverId) {
		RideOffer ride = getEntity(id);
		if (!ride.getDriver().getId().equals(driverId)) {
			throw new BusinessRuleException("Only the driver who published this ride can delete it", HttpStatus.FORBIDDEN);
		}
		rideOfferRepository.delete(ride);
	}
}
