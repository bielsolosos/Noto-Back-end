package space.bielsolososdev.noto.api.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EditUserRequest(
        @NotBlank String username,
        @NotBlank @Email(message = "Deve ser um email válido") String email
) {}

