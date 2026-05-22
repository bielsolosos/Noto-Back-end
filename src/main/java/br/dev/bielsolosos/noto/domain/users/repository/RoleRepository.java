package br.dev.bielsolosos.noto.domain.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.dev.bielsolosos.noto.domain.users.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);
}

