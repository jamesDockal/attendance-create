package com.astro7.fullstack_challange.attendance;

import java.time.LocalDateTime;

public record AttendanceResponse(
		Long id,
		String name,
		String cpf,
		AttendanceStatus status,
		String protocol,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {

	static AttendanceResponse from(Attendance attendance) {
		return new AttendanceResponse(
				attendance.getId(),
				attendance.getPatientName(),
				attendance.getCpf(),
				attendance.getStatus(),
				attendance.getProtocol(),
				attendance.getCreatedAt(),
				attendance.getUpdatedAt());
	}

}
