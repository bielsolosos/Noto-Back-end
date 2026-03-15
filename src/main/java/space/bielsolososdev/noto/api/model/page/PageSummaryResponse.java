package space.bielsolososdev.noto.api.model.page;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PageSummaryResponse(UUID id, String title, OffsetDateTime updatedAt) {
}
