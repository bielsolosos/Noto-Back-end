package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import space.bielsolososdev.noto.domain.users.model.RefreshToken;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.RefreshTokenRepository;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;
import space.bielsolososdev.noto.infrastructure.NotoProperties;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final NotoProperties properties;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(properties.getJwt().getRefreshExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public String validateAndConsume(String token) {
        RefreshToken data = refreshTokenRepository.findByTokenForUpdate(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido ou já utilizado"));

        refreshTokenRepository.delete(data);

        if (data.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expirado");
        }

        return data.getUser().getUsername();
    }

    public void cleanupExpiredTokens() {
        long removed = refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        if (removed > 0) {
            log.debug("Removed {} expired refresh tokens", removed);
        }
    }
}

