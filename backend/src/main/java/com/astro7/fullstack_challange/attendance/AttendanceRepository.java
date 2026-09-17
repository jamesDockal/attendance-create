package com.astro7.fullstack_challange.attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	List<Attendance> findTop50ByStatusOrderByCreatedAtAsc(AttendanceStatus status);

	@Transactional
	@Modifying
	@Query("update Attendance a set a.status = :newStatus where a.id = :id and a.status = :currentStatus")
	int updateStatus(Long id, AttendanceStatus currentStatus, AttendanceStatus newStatus);

	@Transactional
	@Modifying
	@Query("update Attendance a set a.status = :newStatus where a.status = :currentStatus")
	int replaceStatus(AttendanceStatus currentStatus, AttendanceStatus newStatus);

}
