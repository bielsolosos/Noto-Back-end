package br.dev.bielsolosos.noto.domain.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import br.dev.bielsolosos.noto.domain.users.model.RefreshToken;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.repository.RefreshTokenRepository;
import br.dev.bielsolosos.noto.domain.users.repository.UserRepository;
import br.dev.bielsolosos.noto.infrastructure.NotoProperties;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private NotoProperties properties;
    @Mock
    private NotoProperties.Jwt jwtProperties;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(properties, userRepository, refreshTokenRepository);
    }

    @Test
    void createRefreshTokenSuccess() {
        when(properties.getJwt()).thenReturn(jwtProperties);
        when(jwtProperties.getRefreshExpiration()).thenReturn(60_000L);

        User user = new User();
        user.setUsername("biel");
        when(userRepository.findByUsername("biel")).thenReturn(Optional.of(user));

        String token = refreshTokenService.createRefreshToken("biel");

        assertNotNull(token);
        assertFalse(token.isBlank());

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken savedToken = tokenCaptor.getValue();
        assertEquals("biel", savedToken.getUser().getUsername());
        assertEquals(token, savedToken.getToken());
        assertTrue(savedToken.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void validateAndConsumeSuccess() {
        String token = "refresh-token";
        User user = new User();
        user.setUsername("biel");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(60));
        when(refreshTokenRepository.findByTokenForUpdate(token)).thenReturn(Optional.of(refreshToken));

        String username = refreshTokenService.validateAndConsume(token);

        assertEquals("biel", username);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void validateAndConsumeAlreadyConsumedToken() {
        String token = "already-consumed";
        when(refreshTokenRepository.findByTokenForUpdate(token)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateAndConsume(token)
        );

        assertEquals("Refresh token inválido ou já utilizado", ex.getMessage());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void validateAndConsumeExpiredToken() {
        String token = "expired-token";
        User user = new User();
        user.setUsername("biel");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().minusSeconds(1));
        when(refreshTokenRepository.findByTokenForUpdate(token)).thenReturn(Optional.of(refreshToken));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateAndConsume(token)
        );

        assertEquals("Refresh token expirado", ex.getMessage());
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void cleanupExpiredTokensRemovesExpiredToken() {
        when(refreshTokenRepository.deleteByExpiresAtBefore(any(Instant.class))).thenReturn(1L);

        refreshTokenService.cleanupExpiredTokens();

        verify(refreshTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
    }
}
