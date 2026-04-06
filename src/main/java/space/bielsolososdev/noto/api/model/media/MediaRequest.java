package space.bielsolososdev.noto.api.model.media;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record MediaRequest(MultipartFile file, UUID userId) {
}
