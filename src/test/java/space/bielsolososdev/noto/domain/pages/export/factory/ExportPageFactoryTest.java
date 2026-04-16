package space.bielsolososdev.noto.domain.pages.export.factory;

import org.junit.jupiter.api.Test;
import space.bielsolososdev.noto.core.enums.ExportTypeEnum;
import space.bielsolososdev.noto.domain.pages.export.service.PageExporterService;
import space.bielsolososdev.noto.domain.pages.export.service.impl.PageExporterServiceMd;
import space.bielsolososdev.noto.domain.pages.export.service.impl.PageExporterServiceNotoPdf;

import static org.junit.jupiter.api.Assertions.*;

class ExportPageFactoryTest {

    private final ExportPageFactory factory = new ExportPageFactory();

    @Test
    void generatePageServiceShouldReturnMdImplementation() {
        PageExporterService service = factory.generatePageService(ExportTypeEnum.MD);

        assertInstanceOf(PageExporterServiceMd.class, service);
    }

    @Test
    void generatePageServiceShouldReturnPdfImplementation() {
        PageExporterService service = factory.generatePageService(ExportTypeEnum.NOTO_PDF);

        assertInstanceOf(PageExporterServiceNotoPdf.class, service);
    }

    @Test
    void generatePageServiceShouldThrowWhenTypeIsNull() {
        assertThrows(NullPointerException.class, () -> factory.generatePageService(null));
    }
}

