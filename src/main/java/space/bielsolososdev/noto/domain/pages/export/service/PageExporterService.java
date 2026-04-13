package space.bielsolososdev.noto.domain.pages.export.service;

import space.bielsolososdev.noto.core.enums.MimeTypeEnum;
import space.bielsolososdev.noto.domain.pages.model.Page;

public interface PageExporterService {

    byte[] getBytesFromPage(Page entity);

    String getFileName(Page entity);

    MimeTypeEnum getMimeType();

}
