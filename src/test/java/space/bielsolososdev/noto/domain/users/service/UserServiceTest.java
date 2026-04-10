package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.media.service.MediaService;
import space.bielsolososdev.noto.domain.users.model.Role;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.RoleRepository;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MediaService mediaService;
    @Mock
    private MeService meService;

    @InjectMocks
    private UserService userService;

    private User user;
    private User secondUser;

    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        Role roleUser = getRoleUser();

        Role roleAdmin = new Role();
        roleAdmin.setId(UUID.randomUUID());
        roleAdmin.setName("ROLE_ADMIN");
        roleAdmin.setDescription("Administrador do sistema");

        roles = Set.of(roleUser, roleAdmin);
        user = new User(UUID.randomUUID(), "primary-user", "primary-email@gmail.com", "senha",  OffsetDateTime.now(),  OffsetDateTime.now().minusDays(20), true, roles, null, new ArrayList<>());
        secondUser = new User(UUID.randomUUID(), "secondary-user", "secondary-email@gmail.com", "senha",  OffsetDateTime.now(),  OffsetDateTime.now().minusDays(30), true, Set.of(roleUser), null, new ArrayList<>());

    }

    Role getRoleUser(){
        Role roleUser = new Role();
        roleUser.setId(UUID.randomUUID());
        roleUser.setName("ROLE_USER");
        roleUser.setDescription("Usuário comum");
        return roleUser;
    }

    @Test
    void changePasswordPasswordSuccess() {

        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("senhaMock");

        User result = userService.changePassword(user.getId(), user.getPassword(), "senhaMock");

        verify(repository, times(1)).findById(user.getId());
        verify(passwordEncoder, times(1)).encode(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        assertNotNull(result);
        assertEquals("senhaMock", result.getPassword());
    }

    @Test
    void changePasswordPasswordIncorrect() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.changePassword(user.getId(), user.getPassword(), "senhaMock"));
    }

    @Test
    void changePasswordPasswordUserNotFound() {
        when(repository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.changePassword(user.getId(), user.getPassword(), "senhaMock"));
    }

    @Test
    void createUserThrowsBusinessExceptionUsername() {
        when(repository.findByUsername("bielsolosos")).thenReturn(Optional.of(user));
        assertThrows(BusinessException.class, () -> userService.createUser("bielsolosos", "emailteste@teste.teste-bolado-mesmo", "****"));
    }

    @Test
    void createUserThrowsBusinessExceptionEmail() {
        when(repository.findByUsername("bielsolosos")).thenReturn(Optional.empty());
        when(repository.findByEmail("primary-email@gmail.com")).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> userService.createUser("bielsolosos", "primary-email@gmail.com", "****"));
    }

    @Test
    void createUserSuccess() {
        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(repository.findByEmail("primary-email@gmail.com")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(getRoleUser()));
        when(passwordEncoder.encode(any())).thenReturn("senhaMock");

        userService.createUser(user.getUsername(), user.getEmail(), user.getPassword());

        verify(repository, times(1)).save(any());
        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    void editUserSuccess() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
        when(meService.getMe()).thenReturn(user);

        User userNovo = userService.editUser(user.getId(), "USERNAME NOVO", "EMAILNOVO@GMAIL.COM");
        assertNotNull(userNovo);

    }

    @Test
    void editUserThrowsBusinessExceptionUsername() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(meService.getMe()).thenReturn(user);
        when(repository.findByUsername(secondUser.getUsername())).thenReturn(Optional.of(secondUser));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.editUser(user.getId(), secondUser.getUsername(), "email-novo@gmail.com"));

        assertEquals("Nome de usuário já existente", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void editUserThrowsBusinessExceptionEmail() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(meService.getMe()).thenReturn(user);
        when(repository.findByEmail(secondUser.getEmail())).thenReturn(Optional.of(secondUser));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.editUser(user.getId(), user.getUsername(), secondUser.getEmail()));

        assertEquals("Email já existente", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void editUserWithoutPermission() {
        when(repository.findById(secondUser.getId())).thenReturn(Optional.of(secondUser));
        when(meService.getMe()).thenReturn(user);

        assertThrows(BadCredentialsException.class, () -> userService.editUser(secondUser.getId(), "USERNAME NOVO", "EMAILNOVO@GMAIL.COM"));

    }


    @Test
    void getEntitySuccess() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        User entity = userService.getEntity(this.user.getId());
        verify(repository, times(1)).findById(this.user.getId());
        assertNotNull(entity);
        assertTrue(entity.equals(user));
    }

    @Test
    void getEntityFailed() {
        when(repository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.getEntity(this.user.getId()));

    }

}

