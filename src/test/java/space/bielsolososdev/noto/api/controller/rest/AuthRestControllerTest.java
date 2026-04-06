package space.bielsolososdev.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import space.bielsolososdev.noto.domain.users.model.dto.LoginRequest;
import space.bielsolososdev.noto.domain.users.model.dto.RefreshRequest;
import space.bielsolososdev.noto.domain.users.model.dto.TokenResponse;
import space.bielsolososdev.noto.domain.users.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthRestController controller;

    private LoginRequest loginRequest;
    private RefreshRequest refreshRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("biel", "senha123");
        refreshRequest = new RefreshRequest("refresh-1");
    }

    @Test
    void loginSuccess() {
        TokenResponse tokenResponse = new TokenResponse("jwt", "refresh");
        when(authService.login(loginRequest)).thenReturn(tokenResponse);

        ResponseEntity<TokenResponse> response = controller.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt", response.getBody().token());
        assertEquals("refresh", response.getBody().refreshToken());
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void refreshSuccess() {
        TokenResponse tokenResponse = new TokenResponse("jwt-2", "refresh-2");
        when(authService.refresh(refreshRequest)).thenReturn(tokenResponse);

        ResponseEntity<TokenResponse> response = controller.refresh(refreshRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-2", response.getBody().token());
        assertEquals("refresh-2", response.getBody().refreshToken());
        verify(authService, times(1)).refresh(refreshRequest);
    }
}
