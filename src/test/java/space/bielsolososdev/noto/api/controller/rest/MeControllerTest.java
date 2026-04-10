package space.bielsolososdev.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.domain.users.model.Role;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.service.MeService;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    private MeService meService;

    @InjectMocks
    private MeController controller;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_USER");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("biel");
        user.setEmail("biel@email.com");
        user.setCreatedAt(OffsetDateTime.now());
        user.setActive(true);
        user.setRoles(Set.of(role));
    }

    @Test
    void getMeSuccess() {
        when(meService.getMe()).thenReturn(user);

        ResponseEntity<UserResponse> response = controller.getMe();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().id());
        assertEquals("biel", response.getBody().username());
        assertEquals("biel@email.com", response.getBody().email());
        assertTrue(response.getBody().roles().contains("ROLE_USER"));
        verify(meService, times(1)).getMe();
    }
}
