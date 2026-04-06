package space.bielsolososdev.noto.domain.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import space.bielsolososdev.noto.domain.users.model.Role;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setName("ROLE_USER");

        Role adminRole = new Role();
        adminRole.setId(UUID.randomUUID());
        adminRole.setName("ROLE_ADMIN");

        activeUser = new User(
                UUID.randomUUID(),
                "biel",
                "biel@email.com",
                "senha",
                null,
                null,
                true,
                Set.of(userRole, adminRole),
                new ArrayList<>()
        );

        inactiveUser = new User(
                UUID.randomUUID(),
                "inativo",
                "inativo@email.com",
                "senha",
                null,
                null,
                false,
                Set.of(userRole),
                new ArrayList<>()
        );
    }

    @Test
    void loadUserByUsernameSuccess() {
        when(userRepository.findByUsername(activeUser.getUsername())).thenReturn(Optional.of(activeUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(activeUser.getUsername());

        assertNotNull(userDetails);
        assertEquals(activeUser.getUsername(), userDetails.getUsername());
        assertEquals(activeUser.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }

    @Test
    void loadUserByUsernameInactiveUser() {
        when(userRepository.findByUsername(inactiveUser.getUsername())).thenReturn(Optional.of(inactiveUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(inactiveUser.getUsername());

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        assertFalse(userDetails.isAccountNonExpired());
        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(userRepository.findByUsername("nao-existe")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nao-existe")
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
