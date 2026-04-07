package space.bielsolososdev.noto.api.model.media;

import org.springframework.web.multipart.MultipartFile;

public record MediaRequest(MultipartFile file) {
}
