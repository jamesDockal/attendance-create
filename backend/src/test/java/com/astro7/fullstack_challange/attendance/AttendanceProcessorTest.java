package com.astro7.fullstack_challange.attendance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class AttendanceProcessorTest {

	@Mock
	private AttendanceRepository repository;

	@Mock
	private ProtocolClient protocolClient;

	@Mock
	private AttendanceBroadcaster broadcaster;

	@Mock
	private ExecutorService executor;

	private AttendanceProcessor processor;

	@BeforeEach
	void setUp() {
		processor = new AttendanceProcessor(repository, protocolClient, broadcaster, executor, 3);
	}

	@Test
	void completesAttendanceWithProtocol() {
		Attendance attendance = attendance(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(attendance));
		when(protocolClient.fetchProtocol()).thenReturn("c7a1e0e2-6d1f-4a3b-9f0e-2b8d1c3a4e5f");

		processor.process(1L);

		assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.COMPLETED);
		assertThat(attendance.getProtocol()).isEqualTo("c7a1e0e2-6d1f-4a3b-9f0e-2b8d1c3a4e5f");
		verify(repository).save(attendance);
	}

	@Test
	void keepsAttendancePendingWhenServiceFails() {
		Attendance attendance = attendance(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(attendance));
		when(protocolClient.fetchProtocol()).thenThrow(new RestClientException("503"));

		processor.process(1L);

		assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PENDING);
		assertThat(attendance.getAttempts()).isEqualTo(1);
		assertThat(attendance.getProtocol()).isNull();
		verify(repository).save(attendance);
	}

	@Test
	void failsAttendanceAfterMaxAttempts() {
		Attendance attendance = attendance(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(attendance));
		when(protocolClient.fetchProtocol()).thenThrow(new RestClientException("timeout"));

		processor.process(1L);
		processor.process(1L);
		processor.process(1L);

		assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.FAILED);
		assertThat(attendance.getAttempts()).isEqualTo(3);
	}

	@Test
	void submitsOnlyAttendancesItCouldClaim() {
		when(repository.findTop50ByStatusOrderByCreatedAtAsc(AttendanceStatus.PENDING))
				.thenReturn(List.of(attendance(1L), attendance(2L)));
		when(repository.updateStatus(1L, AttendanceStatus.PENDING, AttendanceStatus.PROCESSING)).thenReturn(1);
		when(repository.updateStatus(2L, AttendanceStatus.PENDING, AttendanceStatus.PROCESSING)).thenReturn(0);

		processor.processPending();

		verify(executor, times(1)).execute(any());
	}

	private Attendance attendance(Long id) {
		Attendance attendance = new Attendance("Maria Silva", "52998224725");
		ReflectionTestUtils.setField(attendance, "id", id);
		return attendance;
	}

}
