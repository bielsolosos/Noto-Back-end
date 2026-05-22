package br.dev.bielsolosos.noto.api.mapper.page;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PageResponse(UUID id, String title, String content, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
