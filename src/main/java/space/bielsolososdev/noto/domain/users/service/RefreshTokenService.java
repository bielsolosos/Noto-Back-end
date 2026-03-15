package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.bielsolososdev.noto.infrastructure.NotoProperties;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final NotoProperties properties;
    private final ConcurrentHashMap<String, RefreshTokenData> tokens = new ConcurrentHashMap<>();

    public String createRefreshToken(String username) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(properties.getJwt().getRefreshExpiration());
        tokens.put(token, new RefreshTokenData(username, expiresAt));
        return token;
    }

    public String validateAndConsume(String token) {
        RefreshTokenData data = tokens.remove(token);

        if (data == null) {
            throw new IllegalArgumentException("Refresh token inválido ou já utilizado");
        }

        if (data.expiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expirado");
        }

        return data.username();
    }

    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int initialSize = tokens.size();
        tokens.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
        int removed = initialSize - tokens.size();
        if (removed > 0) {
            log.debug("Removed {} expired refresh tokens", removed);
        }
    }

    private record RefreshTokenData(String username, Instant expiresAt) {}
}

