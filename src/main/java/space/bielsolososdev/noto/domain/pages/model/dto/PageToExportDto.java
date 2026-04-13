package space.bielsolososdev.noto.domain.pages.model.dto;
import space.bielsolososdev.noto.core.enums.MimeTypeEnum;

public record PageToExportDto(String fileName, MimeTypeEnum mimeType, long contentLength, byte[] resource) {
};