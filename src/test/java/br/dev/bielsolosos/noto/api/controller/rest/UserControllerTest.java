package br.dev.bielsolosos.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;
import br.dev.bielsolosos.noto.api.model.MessageResponse;
import br.dev.bielsolosos.noto.api.model.media.MediaRequest;
import br.dev.bielsolosos.noto.api.model.user.ChangePasswordRequest;
import br.dev.bielsolosos.noto.api.model.user.CreateUserRequest;
import br.dev.bielsolosos.noto.api.model.user.EditUserRequest;
import br.dev.bielsolosos.noto.api.model.user.UserResponse;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;
import br.dev.bielsolosos.noto.domain.users.model.Role;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.service.MeService;
import br.dev.bielsolosos.noto.domain.users.service.UserService;
import br.dev.bielsolosos.noto.infrastructure.NotoProperties;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private MeService meService;
    @Mock
    private NotoProperties props;

    @InjectMocks
    private UserController controller;

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
        user.setPassword("senha123");
        user.setCreatedAt(OffsetDateTime.now());
        user.setActive(true);
        user.setRoles(Set.of(role));
    }

    @Test
    void changePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest("senha123", "nova123");
        when(meService.getMe()).thenReturn(user);
        when(userService.changePassword(user.getId(), request.oldPassword(), request.newPassword())).thenReturn(user);

        ResponseEntity<MessageResponse> response = controller.changePassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Senha alterada com sucesso", response.getBody().message());
        verify(userService, times(1)).changePassword(user.getId(), request.oldPassword(), request.newPassword());
    }

    @Test
    void editUserSuccess() {
        EditUserRequest request = new EditUserRequest("novo-username", "novo@email.com");
        when(meService.getMe()).thenReturn(user);
        when(userService.editUser(user.getId(), request.username(), request.email())).thenReturn(user);

        ResponseEntity<UserResponse> response = controller.editUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().id());
        assertEquals("biel", response.getBody().username());
        assertNull(response.getBody().profileImageUrl());
        verify(userService, times(1)).editUser(user.getId(), request.username(), request.email());
    }

    @Test
    void uploadProfileImageSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        MediaRequest request = new MediaRequest(file);

        MediaR2 profileMedia = new MediaR2();
        profileMedia.setUrl("https://cdn.example.com/avatar.webp");
        user.setProfileMedia(profileMedia);

        UserResponse updatedUser = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "https://cdn.example.com/avatar.webp",
                user.isActive(),
                user.getCreatedAt(),
                Set.of("ROLE_USER")
        );

        when(userService.uploadProfileImage(file)).thenReturn(user);

        ResponseEntity<UserResponse> response = controller.uploadProfileImage(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://cdn.example.com/avatar.webp", response.getBody().profileImageUrl());
        verify(userService, times(1)).uploadProfileImage(file);
    }

  
    @Test
    void createUserRegistrationDisabled() {
        CreateUserRequest request = new CreateUserRequest("biel", "biel@email.com", "senha123", "senha123");
        when(props.isRegistrationEnabled()).thenReturn(false);

        assertThrows(NoHandlerFoundException.class, () -> controller.createUser(request));
        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void createUserPasswordMismatch() {
        CreateUserRequest request = new CreateUserRequest("biel", "biel@email.com", "senha123", "diferente");
        when(props.isRegistrationEnabled()).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.createUser(request));

        assertEquals("As senhas não conferem", ex.getMessage());
        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void createUserSuccess() throws NoHandlerFoundException {
        CreateUserRequest request = new CreateUserRequest("biel", "biel@email.com", "senha123", "senha123");
        when(props.isRegistrationEnabled()).thenReturn(true);
        when(userService.createUser(request.username(), request.email(), request.password())).thenReturn(user);

        ResponseEntity<UserResponse> response = controller.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().id());
        assertEquals("biel", response.getBody().username());
        assertNull(response.getBody().profileImageUrl());
        verify(userService, times(1)).createUser(request.username(), request.email(), request.password());
    }
}
