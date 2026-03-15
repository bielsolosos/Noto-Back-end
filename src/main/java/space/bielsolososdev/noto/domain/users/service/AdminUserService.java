package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;
import space.bielsolososdev.noto.domain.users.repository.specification.UserSpecification;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public Page<User> listUsers(Pageable pageable, String filter, Boolean isActive,
                                LocalDateTime createdAfter, LocalDateTime createdBefore) {
        var spec = new UserSpecification(filter, isActive, createdAfter, createdBefore);
        return repository.findAll(spec, pageable);
    }

    public User toggleUserActive(UUID id) {
        log.debug("Alternando status ativo do usuário com ID: {}", id);

        var user = userService.getEntity(id);
        user.setActive(!user.isActive());

        var savedUser = repository.save(user);
        log.info("Status do usuário {} alterado para: {}",
                user.getUsername(), user.isActive() ? "ATIVO" : "INATIVO");

        return savedUser;
    }

    public void deleteUser(UUID id) {
        log.debug("Tentativa de deletar usuário com ID: {}", id);

        var user = userService.getEntity(id);
        repository.delete(user);

        log.warn("Usuário {} (ID: {}) foi DELETADO do sistema", user.getUsername(), id);
    }

    public User adminChangePassword(UUID id, String newPassword) {
        log.debug("Admin alterando senha do usuário com ID: {}", id);

        var user = userService.getEntity(id);
        user.setPassword(passwordEncoder.encode(newPassword));

        var savedUser = repository.save(user);
        log.info("Senha do usuário {} alterada por administrador", user.getUsername());

        return savedUser;
    }

    public User adminEditUser(UUID id, String username, String email) {
        var entity = userService.getEntity(id);

        if (!entity.getUsername().equals(username)) {
            repository.findByUsername(username).ifPresent(u -> {
                throw new BusinessException("Nome de usuário já existente");
            });
        }

        if (!entity.getEmail().equals(email)) {
            repository.findByEmail(email).ifPresent(u -> {
                throw new BusinessException("Email já existente");
            });
        }

        entity.setUsername(username);
        entity.setEmail(email);

        var savedUser = repository.save(entity);
        log.info("Admin atualizou usuário {}: username='{}', email='{}'", id, username, email);

        return savedUser;
    }
}

