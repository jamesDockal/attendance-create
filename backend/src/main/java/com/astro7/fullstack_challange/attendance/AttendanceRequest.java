package com.astro7.fullstack_challange.attendance;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AttendanceRequest(
		@NotBlank(message = "Informe o nome")
		@Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
		String name,

		@NotBlank(message = "Informe o CPF")
		@CPF(message = "CPF inválido")
		String cpf) {
}
