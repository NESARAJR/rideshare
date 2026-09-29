package com.rideshare.ridesharelite.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rideshare.ridesharelite.dto.DriverDecision;
import com.rideshare.ridesharelite.dto.RideRequestCreate;
import com.rideshare.ridesharelite.dto.RideRequestResponse;
import com.rideshare.ridesharelite.service.RideRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class RideRequestController {

	private final RideRequestService requestService;

	public RideRequestController(RideRequestService requestService) {
		this.requestService = requestService;
	}

	/** Feature 3: rider requests a seat. */
	@PostMapping
	public ResponseEntity<RideRequestResponse> create(@Valid @RequestBody RideRequestCreate dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(requestService.create(dto));
	}

	/** Feature 3 + 4: driver approves (reduces available seats). */
	@PutMapping("/{id}/approve")
	public RideRequestResponse approve(@PathVariable Long id, @Valid @RequestBody DriverDecision dto) {
		return requestService.approve(id, dto.driverId());
	}

	/** Feature 3: driver rejects. */
	@PutMapping("/{id}/reject")
	public RideRequestResponse reject(@PathVariable Long id, @Valid @RequestBody DriverDecision dto) {
		return requestService.reject(id, dto.driverId());
	}
}
