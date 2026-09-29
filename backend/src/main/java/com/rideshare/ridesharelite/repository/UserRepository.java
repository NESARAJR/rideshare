package com.rideshare.ridesharelite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.ridesharelite.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
}
