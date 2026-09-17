package com.astro7.fullstack_challange.attendance;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AttendanceRequest(
		@NotBlank(message = "Informe o nome")
		@Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
		String name,

		@NotNull(message = "Informe o CPF")
		@CPF(message = "CPF inválido")
		String cpf) {
}
