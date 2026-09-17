package com.astro7.fullstack_challange.attendance;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

	private final AttendanceService service;

	public AttendanceController(AttendanceService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<AttendanceResponse> open(@Valid @RequestBody AttendanceRequest request) {
		Attendance attendance = service.open(request);
		return ResponseEntity.created(URI.create("/api/attendances/" + attendance.getId()))
				.body(AttendanceResponse.from(attendance));
	}

	@GetMapping("/{id}")
	public AttendanceResponse find(@PathVariable Long id) {
		return AttendanceResponse.from(service.find(id));
	}

}
