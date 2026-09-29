package com.rideshare.ridesharelite.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rideshare.ridesharelite.entity.RideOffer;

public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {

	List<RideOffer> findByDriverIdOrderByDepartureTimeDesc(Long driverId);

	@Query("""
			SELECT r FROM RideOffer r
			WHERE LOWER(r.origin) LIKE LOWER(CONCAT('%', :origin, '%'))
			  AND LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%'))
			  AND r.departureTime BETWEEN :fromTime AND :toTime
			  AND r.availableSeats > 0
			ORDER BY r.departureTime ASC
			""")
	List<RideOffer> search(@Param("origin") String origin,
			@Param("destination") String destination,
			@Param("fromTime") LocalDateTime fromTime,
			@Param("toTime") LocalDateTime toTime);
}
