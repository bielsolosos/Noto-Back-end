package br.dev.bielsolosos.noto.domain.pages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import br.dev.bielsolosos.noto.domain.pages.model.Page;

import java.util.List;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID>, JpaSpecificationExecutor<Page> {

    List<Page> findByUserId(UUID userId);

    List<Page> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    List<Page> findByUserIdAndArchivedTrueOrderByUpdatedAtDesc(UUID userId);
}

