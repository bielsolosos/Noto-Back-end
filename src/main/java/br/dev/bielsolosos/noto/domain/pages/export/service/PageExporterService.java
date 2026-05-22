package br.dev.bielsolosos.noto.domain.pages.export.service;

import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;
import br.dev.bielsolosos.noto.domain.pages.model.Page;

public interface PageExporterService {

    byte[] getBytesFromPage(Page entity);

    String getFileName(Page entity);

    MimeTypeEnum getMimeType();

}
