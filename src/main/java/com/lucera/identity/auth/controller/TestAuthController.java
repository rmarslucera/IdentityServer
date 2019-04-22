package com.lucera.identity.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAuthController {
	
	@GetMapping("/lucera/api/test/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public String userAccess() {
		return "{userProfile: test success}";
	}

	@GetMapping("/lucera/api/test/broker")
	@PreAuthorize("hasRole('BROKER') or hasRole('ADMIN')")
	public String brokerOrAdminAccess() {
		return "{userProfile: Broker or Admin access}";
	}
	
	@GetMapping("/lucera/api/test/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "{userProfile: Admin Contents}";
	}
}