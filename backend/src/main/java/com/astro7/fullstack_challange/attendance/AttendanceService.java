package com.astro7.fullstack_challange.attendance;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttendanceService {

	private static final List<AttendanceStatus> OPEN_STATUSES = List.of(AttendanceStatus.PENDING,
			AttendanceStatus.PROCESSING);

	private final AttendanceRepository repository;

	public AttendanceService(AttendanceRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Attendance open(AttendanceRequest request) {
		String cpf = request.cpf().replaceAll("\\D", "");
		if (repository.existsByCpfAndStatusIn(cpf, OPEN_STATUSES)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um atendimento em andamento para este CPF");
		}
		return repository.save(new Attendance(request.name().trim(), cpf));
	}

	@Transactional(readOnly = true)
	public Attendance find(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendimento não encontrado"));
	}

}
