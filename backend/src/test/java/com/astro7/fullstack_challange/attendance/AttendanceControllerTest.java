package com.astro7.fullstack_challange.attendance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AttendanceService service;

	@Test
	void opensAttendance() throws Exception {
		Attendance attendance = new Attendance("Maria Silva", "52998224725");
		ReflectionTestUtils.setField(attendance, "id", 7L);
		when(service.open(any())).thenReturn(attendance);

		mockMvc.perform(post("/api/attendances")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name": "Maria Silva", "cpf": "529.982.247-25"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/attendances/7"))
				.andExpect(jsonPath("$.status").value("PENDING"))
				.andExpect(jsonPath("$.protocol").isEmpty());
	}

	@Test
	void rejectsInvalidData() throws Exception {
		mockMvc.perform(post("/api/attendances")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name": "", "cpf": "111.111.111-11"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.name").value("Informe o nome"))
				.andExpect(jsonPath("$.errors.cpf").value("CPF inválido"));
	}

	@Test
	void returnsConflictWhenCpfHasOpenAttendance() throws Exception {
		when(service.open(any()))
				.thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um atendimento em andamento para este CPF"));

		mockMvc.perform(post("/api/attendances")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name": "Maria Silva", "cpf": "52998224725"}
						"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.detail").value("Já existe um atendimento em andamento para este CPF"));
	}

	@Test
	void returnsNotFoundForUnknownAttendance() throws Exception {
		when(service.find(99L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Atendimento não encontrado"));

		mockMvc.perform(get("/api/attendances/99"))
				.andExpect(status().isNotFound());
	}

}
