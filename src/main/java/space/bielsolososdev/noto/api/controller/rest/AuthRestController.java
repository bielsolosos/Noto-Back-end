package space.bielsolososdev.noto.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.bielsolososdev.noto.domain.users.model.dto.LoginRequest;
import space.bielsolososdev.noto.domain.users.model.dto.RefreshRequest;
import space.bielsolososdev.noto.domain.users.model.dto.TokenResponse;
import space.bielsolososdev.noto.domain.users.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(service.refresh(request));
    }
}

