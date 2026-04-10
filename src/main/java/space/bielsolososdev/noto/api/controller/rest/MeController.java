package space.bielsolososdev.noto.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.bielsolososdev.noto.api.mapper.user.UserMapper;
import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.domain.users.service.MeService;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;

    @GetMapping
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(UserMapper.toUserResponse(meService.getMe()));
    }
}

