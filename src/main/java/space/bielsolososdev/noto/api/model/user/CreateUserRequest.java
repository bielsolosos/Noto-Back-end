package space.bielsolososdev.noto.api.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank @Email(message = "Deve ser um email válido") String email,
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String password,
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String confirmPassword
) {}
