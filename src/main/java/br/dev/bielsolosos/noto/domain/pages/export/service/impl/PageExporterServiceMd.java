package br.dev.bielsolosos.noto.domain.pages.export.service.impl;

import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;
import br.dev.bielsolosos.noto.domain.pages.export.service.PageExporterService;
import br.dev.bielsolosos.noto.domain.pages.model.Page;

public class PageExporterServiceMd implements PageExporterService {
    @Override
    public byte[] getBytesFromPage(Page entity) {
        String formatted = String.format("# %s\n\n%s", entity.getTitle(), entity.getContent());
        return formatted.getBytes();
    }

    @Override
    public String getFileName(Page entity) {
        String title = entity.getTitle();

        return String.format("Export %s.md", title);
    }

    @Override
    public MimeTypeEnum getMimeType() {
        return MimeTypeEnum.MARKDOWN;
    }
}
