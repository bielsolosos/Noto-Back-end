package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.RoleRepository;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User getMe() {
        log.debug("Buscando informações do usuário autenticado");
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Tentativa de acesso sem autenticação válida");
            throw new BusinessException("Usuário não autenticado");
        }

        String username = authentication.getName();
        log.info("Usuário '{}' acessando seu próprio perfil", username);

        return repository.findByUsername(username).orElseThrow(() -> {
            log.error("Usuário autenticado '{}' não encontrado no banco de dados", username);
            return new BusinessException("Usuário não encontrado");
        });
    }

    public User changePassword(UUID id, String oldPassword, String newPassword) {
        log.debug("Tentativa de alteração de senha para o usuário com ID: {}", id);

        var user = repository.findById(id).orElseThrow(() -> {
            log.error("Usuário com ID {} não encontrado ao tentar alterar senha", id);
            return new BusinessException("User não encontrado");
        });

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Tentativa de alteração de senha com senha atual incorreta para usuário: {}", user.getUsername());
            throw new BusinessException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        var savedUser = repository.save(user);

        log.info("Senha alterada com sucesso para o usuário: {}", user.getUsername());
        return savedUser;
    }

    public User createUser(String username, String email, String password) {
        log.debug("Tentativa de criação de novo usuário: username='{}', email='{}'", username, email);

        if (repository.findByUsername(username).isPresent()) {
            log.warn("Tentativa de criar usuário com username já existente: {}", username);
            throw new BusinessException("Nome de usuário já está em uso");
        }

        if (repository.findByEmail(email).isPresent()) {
            log.warn("Tentativa de criar usuário com email já existente: {}", email);
            throw new BusinessException("E-mail já está em uso");
        }

        var entity = new User();
        entity.setEmail(email);
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(password));
        entity.getRoles().add(roleRepository.findByName("ROLE_USER").get());

        var savedUser = repository.save(entity);
        log.info("Novo usuário criado com sucesso: {} (ID: {})", username, savedUser.getId());

        return savedUser;
    }

    public User editUser(UUID id, String username, String email) {
        var entity = getEntity(id);

        verifyUserIntegrity(entity);

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
        log.info("Usuário {} atualizado com sucesso: username='{}', email='{}'", id, username, email);

        return savedUser;
    }

    public User getEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado no sistema."));
    }

    private void verifyUserIntegrity(User entity) {
        if (!entity.getId().equals(getMe().getId())) {
            throw new BadCredentialsException("Sem permissão.");
        }
    }
}

