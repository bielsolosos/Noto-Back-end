package br.dev.bielsolosos.noto.domain.pages.model.dto;
import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;

public record PageToExportDto(String fileName, MimeTypeEnum mimeType, long contentLength, byte[] resource) {
}