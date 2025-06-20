package br.edu.atitus.identity_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "Senha não pode estar em branco")
    String password
) {}