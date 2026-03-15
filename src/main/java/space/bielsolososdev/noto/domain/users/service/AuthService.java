package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import space.bielsolososdev.noto.core.utils.SecurityUtils;
import space.bielsolososdev.noto.domain.users.model.dto.LoginRequest;
import space.bielsolososdev.noto.domain.users.model.dto.RefreshRequest;
import space.bielsolososdev.noto.domain.users.model.dto.TokenResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SecurityUtils jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse login(LoginRequest request) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String token = jwtUtil.generateToken(request.username());
        String refreshToken = refreshTokenService.createRefreshToken(request.username());

        return new TokenResponse(token, refreshToken);
    }

    public TokenResponse refresh(RefreshRequest request) {
        String username = refreshTokenService.validateAndConsume(request.refreshToken());
        return new TokenResponse(
                jwtUtil.generateToken(username),
                refreshTokenService.createRefreshToken(username)
        );
    }
}

