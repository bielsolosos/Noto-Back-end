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
        Role roleUser = new Role();
        roleUser.setId(UUID.randomUUID());
        roleUser.setName("ROLE_USER");
        roleUser.setDescription("Usuário comum");

        Role roleAdmin = new Role();
        roleAdmin.setId(UUID.randomUUID());
        roleAdmin.setName("ROLE_ADMIN");
        roleAdmin.setDescription("Administrador do sistema");

        roles = Set.of(roleUser, roleAdmin);
        user = new User(UUID.randomUUID(), "primary-user", "primary-email@gmail.com", "senha",  OffsetDateTime.now(),  OffsetDateTime.now().minusDays(20), true, roles, new ArrayList<>());
        secondUser = new User(UUID.randomUUID(), "secondary-user", "secondary-email@gmail.com", "senha",  OffsetDateTime.now(),  OffsetDateTime.now().minusDays(30), true, Set.of(roleUser), new ArrayList<>());

    }

    @Test
    void getMe() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);

        // Abrir o bloco para métodos estáticos
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            //TODO falta mockar como Autenticado. Colocar o true
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(user.getUsername());
            when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            User me = userService.getMe();

            assertTrue(me.equals(user));
        }
    }

    @Test
    void changePassword() {
    }

    @Test
    void createUser() {
    }

    @Test
    void editUser() {
    }

    @Test
    void getEntity() {
    }
}