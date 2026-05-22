package br.dev.bielsolosos.noto.api.mapper.user;

import br.dev.bielsolosos.noto.api.model.user.UserResponse;
import br.dev.bielsolosos.noto.domain.users.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileMedia() != null ? user.getProfileMedia().getUrl() : null,
                user.isActive(),
                user.getCreatedAt(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet())
        );
    }
}

