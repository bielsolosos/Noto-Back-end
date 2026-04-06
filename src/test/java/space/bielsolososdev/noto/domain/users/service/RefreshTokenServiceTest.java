package space.bielsolososdev.noto.domain.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.bielsolososdev.noto.infrastructure.NotoProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private NotoProperties properties;
    @Mock
    private NotoProperties.Jwt jwtProperties;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        when(properties.getJwt()).thenReturn(jwtProperties);
        when(jwtProperties.getRefreshExpiration()).thenReturn(60_000L);
        refreshTokenService = new RefreshTokenService(properties);
    }

    @Test
    void createRefreshTokenSuccess() {
        String token1 = refreshTokenService.createRefreshToken("biel");
        String token2 = refreshTokenService.createRefreshToken("biel");

        assertNotNull(token1);
        assertNotNull(token2);
        assertFalse(token1.isBlank());
        assertFalse(token2.isBlank());
        assertNotEquals(token1, token2);
    }

    @Test
    void validateAndConsumeSuccess() {
        String token = refreshTokenService.createRefreshToken("biel");

        String username = refreshTokenService.validateAndConsume(token);

        assertEquals("biel", username);
    }

    @Test
    void validateAndConsumeAlreadyConsumedToken() {
        String token = refreshTokenService.createRefreshToken("biel");
        refreshTokenService.validateAndConsume(token);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateAndConsume(token)
        );

        assertEquals("Refresh token inválido ou já utilizado", ex.getMessage());
    }

    @Test
    void validateAndConsumeExpiredToken() {
        when(jwtProperties.getRefreshExpiration()).thenReturn(-1L);
        refreshTokenService = new RefreshTokenService(properties);
        String token = refreshTokenService.createRefreshToken("biel");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateAndConsume(token)
        );

        assertEquals("Refresh token expirado", ex.getMessage());
    }

    @Test
    void cleanupExpiredTokensRemovesExpiredToken() {
        when(jwtProperties.getRefreshExpiration()).thenReturn(-1L);
        refreshTokenService = new RefreshTokenService(properties);
        String token = refreshTokenService.createRefreshToken("biel");

        refreshTokenService.cleanupExpiredTokens();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateAndConsume(token)
        );
        assertEquals("Refresh token inválido ou já utilizado", ex.getMessage());
    }
}
