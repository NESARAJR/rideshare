package com.rideshare.ridesharelite.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rideshare.ridesharelite.dto.RideOfferCreateRequest;
import com.rideshare.ridesharelite.dto.RideOfferResponse;
import com.rideshare.ridesharelite.dto.RideRequestResponse;
import com.rideshare.ridesharelite.service.RideOfferService;
import com.rideshare.ridesharelite.service.RideRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class RideOfferController {

	private final RideOfferService rideService;
	private final RideRequestService requestService;

	public RideOfferController(RideOfferService rideService, RideRequestService requestService) {
		this.rideService = rideService;
		this.requestService = requestService;
	}

	/** Feature 1: publish a ride. */
	@PostMapping
	public ResponseEntity<RideOfferResponse> publish(@Valid @RequestBody RideOfferCreateRequest dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(rideService.publish(dto));
	}

	/** Feature 2: search by route and time window. */
	@GetMapping("/search")
	public List<RideOfferResponse> search(@RequestParam String origin, @RequestParam String destination,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
		return rideService.search(origin, destination, from, to);
	}

	/** Pagination + sorting, e.g. /api/rides?page=0&size=5&sort=departureTime,asc */
	@GetMapping
	public Page<RideOfferResponse> all(@PageableDefault(size = 10, sort = "departureTime") Pageable pageable) {
		return rideService.findAll(pageable);
	}

	@GetMapping("/{id}")
	public RideOfferResponse get(@PathVariable Long id) {
		return rideService.get(id);
	}

	@PutMapping("/{id}")
	public RideOfferResponse update(@PathVariable Long id, @Valid @RequestBody RideOfferCreateRequest dto) {
		return rideService.update(id, dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long driverId) {
		rideService.delete(id, driverId);
		return ResponseEntity.noContent().build();
	}

	/** Driver views all seat requests on a ride. */
	@GetMapping("/{id}/requests")
	public List<RideRequestResponse> requests(@PathVariable Long id) {
		return requestService.forRide(id);
	}
}
