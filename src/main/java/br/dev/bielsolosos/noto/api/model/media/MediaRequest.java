package br.dev.bielsolosos.noto.api.model.media;

import org.springframework.web.multipart.MultipartFile;

public record MediaRequest(MultipartFile file) {
}
