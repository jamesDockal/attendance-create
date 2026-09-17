package com.astro7.fullstack_challange.attendance;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String patientName;

	private String cpf;

	@Enumerated(EnumType.STRING)
	private AttendanceStatus status;

	private String protocol;

	private int attempts;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	protected Attendance() {
	}

	public Attendance(String patientName, String cpf) {
		this.patientName = patientName;
		this.cpf = cpf;
		this.status = AttendanceStatus.PENDING;
	}

	public void complete(String protocol) {
		this.protocol = protocol;
		this.status = AttendanceStatus.COMPLETED;
	}

	public void registerFailure(int maxAttempts) {
		attempts++;
		status = attempts >= maxAttempts ? AttendanceStatus.FAILED : AttendanceStatus.PENDING;
	}

	public Long getId() {
		return id;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getCpf() {
		return cpf;
	}

	public AttendanceStatus getStatus() {
		return status;
	}

	public String getProtocol() {
		return protocol;
	}

	public int getAttempts() {
		return attempts;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

}
