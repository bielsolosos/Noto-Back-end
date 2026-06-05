package br.dev.bielsolosos.noto.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.dev.bielsolosos.noto.api.mapper.user.UserMapper;
import br.dev.bielsolosos.noto.api.model.user.UserResponse;
import br.dev.bielsolosos.noto.core.ratelimit.IpRateLimiter;
import br.dev.bielsolosos.noto.core.ratelimit.enums.IpRateLimitConfigEnum;
import br.dev.bielsolosos.noto.domain.users.service.MeService;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;

    @GetMapping
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(UserMapper.toUserResponse(meService.getMe()));
    }
}
