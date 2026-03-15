package space.bielsolososdev.noto.api.model.user;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        boolean isActive,
        OffsetDateTime createdAt,
        Set<String> roles
) {}

