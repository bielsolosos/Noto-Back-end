package br.dev.bielsolosos.noto.domain.users.service;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserService adminUserService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("biel");
        user.setEmail("biel@email.com");
        user.setPassword("senha");
        user.setActive(true);
    }

    @Test
    void listUsersSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(List.of(user));

        when(repository.findAll(org.mockito.ArgumentMatchers.<Specification<User>>any(), eq(pageable))).thenReturn(page);

        Page<User> response = adminUserService.listUsers(
                pageable,
                "biel",
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(repository, times(1)).findAll(org.mockito.ArgumentMatchers.<Specification<User>>any(), eq(pageable));
    }

    @Test
    void toggleUserActiveSuccess() {
        when(userService.getEntity(userId)).thenReturn(user);
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminUserService.toggleUserActive(userId);

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(userService, times(1)).getEntity(userId);
        verify(repository, times(1)).save(user);
    }

    @Test
    void deleteUserSuccess() {
        when(userService.getEntity(userId)).thenReturn(user);

        adminUserService.deleteUser(userId);

        verify(userService, times(1)).getEntity(userId);
        verify(repository, times(1)).delete(user);
    }

    @Test
    void adminChangePasswordSuccess() {
        when(userService.getEntity(userId)).thenReturn(user);
        when(passwordEncoder.encode("nova-senha")).thenReturn("senha-criptografada");
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminUserService.adminChangePassword(userId, "nova-senha");

        assertNotNull(result);
        assertEquals("senha-criptografada", result.getPassword());
        verify(userService, times(1)).getEntity(userId);
        verify(passwordEncoder, times(1)).encode("nova-senha");
        verify(repository, times(1)).save(user);
    }

    @Test
    void adminEditUserSuccessSameCredentials() {
        when(userService.getEntity(userId)).thenReturn(user);
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminUserService.adminEditUser(userId, user.getUsername(), user.getEmail());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        verify(repository, never()).findByUsername(anyString());
        verify(repository, never()).findByEmail(anyString());
        verify(repository, times(1)).save(user);
    }

    @Test
    void adminEditUserThrowsBusinessExceptionUsernameAlreadyExists() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("ja-existe");

        when(userService.getEntity(userId)).thenReturn(user);
        when(repository.findByUsername("ja-existe")).thenReturn(Optional.of(otherUser));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> adminUserService.adminEditUser(userId, "ja-existe", user.getEmail())
        );

        assertEquals("Nome de usuário já existente", ex.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void adminEditUserThrowsBusinessExceptionEmailAlreadyExists() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("duplicado@email.com");

        when(userService.getEntity(userId)).thenReturn(user);
        when(repository.findByUsername("novo-username")).thenReturn(Optional.empty());
        when(repository.findByEmail("duplicado@email.com")).thenReturn(Optional.of(otherUser));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> adminUserService.adminEditUser(userId, "novo-username", "duplicado@email.com")
        );

        assertEquals("Email já existente", ex.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void adminEditUserSuccessChangedUsernameAndEmail() {
        when(userService.getEntity(userId)).thenReturn(user);
        when(repository.findByUsername("novo-username")).thenReturn(Optional.empty());
        when(repository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminUserService.adminEditUser(userId, "novo-username", "novo@email.com");

        assertNotNull(result);
        assertEquals("novo-username", result.getUsername());
        assertEquals("novo@email.com", result.getEmail());
        verify(repository, times(1)).findByUsername("novo-username");
        verify(repository, times(1)).findByEmail("novo@email.com");
        verify(repository, times(1)).save(user);
    }
}
