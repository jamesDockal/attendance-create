package com.astro7.fullstack_challange.attendance;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttendanceService {

	private final AttendanceRepository repository;

	public AttendanceService(AttendanceRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Attendance open(AttendanceRequest request) {
		String cpf = request.cpf().replaceAll("\\D", "");
		return repository.save(new Attendance(request.name().trim(), cpf));
	}

	@Transactional(readOnly = true)
	public Attendance find(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendimento não encontrado"));
	}

}
