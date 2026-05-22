package br.dev.bielsolosos.noto.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MimeTypeEnum {

    HTML("text/html"),
    PDF("application/pdf"),
    MARKDOWN("text/markdown");

    private String mimeType;
}
