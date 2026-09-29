package com.rideshare.ridesharelite.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.ridesharelite.entity.RequestStatus;
import com.rideshare.ridesharelite.entity.RideRequest;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

	List<RideRequest> findByRiderIdOrderByRequestedAtDesc(Long riderId);

	List<RideRequest> findByRideIdOrderByRequestedAtAsc(Long rideId);

	boolean existsByRideIdAndRiderIdAndStatusIn(Long rideId, Long riderId, Collection<RequestStatus> statuses);
}
