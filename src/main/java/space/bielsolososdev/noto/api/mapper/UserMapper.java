package space.bielsolososdev.noto.api.mapper;

import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.domain.users.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet())
        );
    }
}

