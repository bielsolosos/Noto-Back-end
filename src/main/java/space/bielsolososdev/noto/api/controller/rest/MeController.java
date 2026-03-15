package space.bielsolososdev.noto.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.bielsolososdev.noto.api.mapper.UserMapper;
import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.domain.users.service.UserService;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(UserMapper.toUserResponse(userService.getMe()));
    }
}

