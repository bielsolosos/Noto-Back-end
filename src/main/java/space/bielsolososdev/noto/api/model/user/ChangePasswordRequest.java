package space.bielsolososdev.noto.api.model.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String oldPassword,
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String newPassword
) {}

