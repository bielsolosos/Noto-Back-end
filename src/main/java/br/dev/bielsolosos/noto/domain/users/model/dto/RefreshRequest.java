package br.dev.bielsolosos.noto.domain.users.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "É obrigatório ter um refresh token") String refreshToken
) {}

