package br.dev.bielsolosos.noto.domain.users.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username é obrigatório") String username,
        @NotBlank(message = "Password é obrigatório") String password
) {}

