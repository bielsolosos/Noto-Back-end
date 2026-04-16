package space.bielsolososdev.noto.domain.pages.export.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TemplatePathEnum {

    NOTO_SIMPLE_PDF("/templates/noto-pdf.html");

    private final String templatePath;
}
