package com.rideshare.ridesharelite.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.ridesharelite.dto.Mapper;
import com.rideshare.ridesharelite.dto.RideHistoryResponse;
import com.rideshare.ridesharelite.entity.User;
import com.rideshare.ridesharelite.exception.BusinessRuleException;
import com.rideshare.ridesharelite.exception.ResourceNotFoundException;
import com.rideshare.ridesharelite.repository.RideOfferRepository;
import com.rideshare.ridesharelite.repository.RideRequestRepository;
import com.rideshare.ridesharelite.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final RideOfferRepository rideOfferRepository;
	private final RideRequestRepository rideRequestRepository;

	public UserService(UserRepository userRepository, RideOfferRepository rideOfferRepository,
			RideRequestRepository rideRequestRepository) {
		this.userRepository = userRepository;
		this.rideOfferRepository = rideOfferRepository;
		this.rideRequestRepository = rideRequestRepository;
	}

	public User create(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new BusinessRuleException("A user with this email already exists", HttpStatus.CONFLICT);
		}
		user.setId(null);
		return userRepository.save(user);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User get(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
	}

	/** Feature 5: ride history as both driver and rider. */
	@Transactional(readOnly = true)
	public RideHistoryResponse history(Long userId) {
		User user = get(userId);
		var asDriver = rideOfferRepository.findByDriverIdOrderByDepartureTimeDesc(userId).stream()
				.map(Mapper::toResponse).toList();
		var asRider = rideRequestRepository.findByRiderIdOrderByRequestedAtDesc(userId).stream()
				.map(Mapper::toResponse).toList();
		return new RideHistoryResponse(user.getId(), user.getName(), asDriver, asRider);
	}
}
