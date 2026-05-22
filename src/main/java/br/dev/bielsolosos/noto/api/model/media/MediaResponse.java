package br.dev.bielsolosos.noto.api.model.media;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MediaResponse(UUID id, String fileName, String url, String markdown, OffsetDateTime createdAt) {
}
