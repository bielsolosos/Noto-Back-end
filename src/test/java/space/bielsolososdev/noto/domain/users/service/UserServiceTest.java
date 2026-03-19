package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import space.bielsolososdev.noto.core.exception.BusinessException;
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
        user = new User(UUID.randomUUID(), "primary-user", "primary-email@gmail.com", "senha",  OffsetDateTime.now(),  OffsetDateTime.now().minusDays(20), true, roles, new ArrayList<>());
        secondUser = new User(UUID.randomUUID(), "secondary-user", "secondary-email@gmail.com", "senha",  OffsetDateTime.now(),  OffsetDateTime.now().minusDays(30), true, Set.of(roleUser), new ArrayList<>());

    }

    Role getRoleUser(){
        Role roleUser = new Role();
        roleUser.setId(UUID.randomUUID());
        roleUser.setName("ROLE_USER");
        roleUser.setDescription("Usuário comum");
        return roleUser;
    }

    @Test
    void getMe() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Abrir o bloco para métodos estáticos
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn(user.getUsername());

            when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            User me = userService.getMe();

            assertTrue(me.equals(user));
        }
    }

    @Test
    void getMeNotAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Abrir o bloco para métodos estáticos
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            // Mock não autenticado.
            when(authentication.isAuthenticated()).thenReturn(false);

            assertThrows(BusinessException.class, () -> userService.getMe());
        }
    }

    @Test
    void getMeNotFound() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Abrir o bloco para métodos estáticos
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            // Mock não autenticado.
            when(authentication.isAuthenticated()).thenReturn(true);

            //Mock do userName
            when(repository.findByUsername(any())).thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> userService.getMe());
        }
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
    void editUser() {
    }

    @Test
    void getEntity() {
    }
}