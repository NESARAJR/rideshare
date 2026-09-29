package com.rideshare.ridesharelite.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rideshare.ridesharelite.dto.RideHistoryResponse;
import com.rideshare.ridesharelite.entity.User;
import com.rideshare.ridesharelite.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<User> create(@Valid @RequestBody User user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(user));
	}

	@GetMapping
	public List<User> all() {
		return userService.findAll();
	}

	@GetMapping("/{id}")
	public User get(@PathVariable Long id) {
		return userService.get(id);
	}

	/** Feature 5: ride history as both driver and rider. */
	@GetMapping("/{id}/history")
	public RideHistoryResponse history(@PathVariable Long id) {
		return userService.history(id);
	}
}
