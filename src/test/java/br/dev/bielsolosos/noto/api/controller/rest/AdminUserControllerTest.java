package br.dev.bielsolosos.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import br.dev.bielsolosos.noto.api.model.MessageResponse;
import br.dev.bielsolosos.noto.api.model.user.ChangePasswordRequest;
import br.dev.bielsolosos.noto.api.model.user.EditUserRequest;
import br.dev.bielsolosos.noto.api.model.user.UserResponse;
import br.dev.bielsolosos.noto.domain.users.model.Role;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.repository.UserRepository;
import br.dev.bielsolosos.noto.domain.users.service.AdminUserService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserController controller;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_USER");

        user = new User();
        user.setId(userId);
        user.setUsername("biel");
        user.setEmail("biel@email.com");
        user.setPassword("senha");
        user.setCreatedAt(OffsetDateTime.now());
        user.setActive(true);
        user.setRoles(Set.of(role));
    }

    @Test
    void listUsersSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(List.of(user));
        when(adminUserService.listUsers(pageable, "biel", true, null, null)).thenReturn(page);

        ResponseEntity<Page<UserResponse>> response = controller.listUsers(pageable, "biel", true, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user.getId(), response.getBody().getContent().getFirst().id());
        verify(adminUserService, times(1)).listUsers(pageable, "biel", true, null, null);
    }

    @Test
    void listUsersToListSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        ResponseEntity<List<UserResponse>> response = controller.listUsersToList();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(user.getId(), response.getBody().getFirst().id());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void editUserSuccess() {
        EditUserRequest request = new EditUserRequest("novo-user", "novo@email.com");
        when(adminUserService.adminEditUser(userId, request.username(), request.email())).thenReturn(user);

        ResponseEntity<UserResponse> response = controller.editUser(userId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().id());
        verify(adminUserService, times(1)).adminEditUser(userId, request.username(), request.email());
    }

    @Test
    void changePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest("old123", "new123");
        when(adminUserService.adminChangePassword(userId, request.newPassword())).thenReturn(user);

        ResponseEntity<MessageResponse> response = controller.changePassword(userId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Senha alterada com sucesso", response.getBody().message());
        verify(adminUserService, times(1)).adminChangePassword(userId, request.newPassword());
    }

    @Test
    void toggleActiveSuccess() {
        User toggled = new User();
        toggled.setId(userId);
        toggled.setUsername(user.getUsername());
        toggled.setEmail(user.getEmail());
        toggled.setRoles(user.getRoles());
        toggled.setCreatedAt(user.getCreatedAt());
        toggled.setActive(false);
        when(adminUserService.toggleUserActive(userId)).thenReturn(toggled);

        ResponseEntity<UserResponse> response = controller.toggleActive(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isActive());
        verify(adminUserService, times(1)).toggleUserActive(userId);
    }

    @Test
    void deleteUserSuccess() {
        ResponseEntity<MessageResponse> response = controller.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuário deletado com sucesso", response.getBody().message());
        verify(adminUserService, times(1)).deleteUser(userId);
    }
}
