package space.bielsolososdev.noto.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;

    public User getMe() {
        log.debug("Buscando informacoes do usuario autenticado");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Tentativa de acesso sem autenticacao valida");
            throw new BusinessException("Usuario nao autenticado");
        }

        String username = authentication.getName();
        log.info("Usuario '{}' acessando seu proprio perfil", username);

        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("Usuario autenticado '{}' nao encontrado no banco de dados", username);
            return new BusinessException("Usuario nao encontrado");
        });
    }
}

