package br.dev.bielsolosos.noto.domain.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;

import java.util.UUID;

public interface MediaR2Repository extends JpaRepository<MediaR2, UUID>, JpaSpecificationExecutor<MediaR2> {
}
