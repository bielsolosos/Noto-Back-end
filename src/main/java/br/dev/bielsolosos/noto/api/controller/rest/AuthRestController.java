package br.dev.bielsolosos.noto.api.controller.rest;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.dev.bielsolosos.noto.domain.users.model.dto.LoginRequest;
import br.dev.bielsolosos.noto.domain.users.model.dto.RefreshRequest;
import br.dev.bielsolosos.noto.domain.users.model.dto.TokenResponse;
import br.dev.bielsolosos.noto.domain.users.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService service;

    @PostMapping("/login")
    @RateLimiter(name = "public-routes")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(service.refresh(request));
    }

}

