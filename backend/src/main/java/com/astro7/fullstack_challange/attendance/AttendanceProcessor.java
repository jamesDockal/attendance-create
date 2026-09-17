package com.astro7.fullstack_challange.attendance;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AttendanceProcessor {

	private static final Logger log = LoggerFactory.getLogger(AttendanceProcessor.class);

	private final AttendanceRepository repository;
	private final ProtocolClient protocolClient;
	private final ExecutorService executor;

	public AttendanceProcessor(AttendanceRepository repository, ProtocolClient protocolClient,
			ExecutorService processingExecutor) {
		this.repository = repository;
		this.protocolClient = protocolClient;
		this.executor = processingExecutor;
	}

	@Scheduled(fixedDelayString = "${processing.interval}")
	public void processPending() {
		for (Attendance attendance : repository.findTop50ByStatusOrderByCreatedAtAsc(AttendanceStatus.PENDING)) {
			Long id = attendance.getId();
			if (repository.updateStatus(id, AttendanceStatus.PENDING, AttendanceStatus.PROCESSING) == 1) {
				executor.execute(() -> process(id));
			}
		}
	}

	void process(Long id) {
		Attendance attendance = repository.findById(id).orElseThrow();
		try {
			attendance.complete(protocolClient.fetchProtocol());
		} catch (Exception e) {
			log.warn("Falha ao buscar protocolo do atendimento {}: {}", id, e.getMessage());
			attendance.retryLater();
		}
		repository.save(attendance);
	}

}
