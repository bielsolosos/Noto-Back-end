package space.bielsolososdev.noto.domain.pages.export.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.bielsolososdev.noto.core.enums.MimeTypeEnum;
import space.bielsolososdev.noto.domain.pages.model.Page;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PageExporterServiceNotoPdfTest {

    private final PageExporterServiceNotoPdf service = new PageExporterServiceNotoPdf();
    private Page page;

    @BeforeEach
    void setUp() {
        page = new Page();
        page.setTitle("Minha Nota");
        page.setContent("Conteudo **markdown** com tabela\n\n| A | B |\n|---|---|\n| 1 | 2 |");
    }

    @Test
    void getBytesFromPageShouldGenerateValidPdfBytes() {
        byte[] bytes = service.getBytesFromPage(page);

        assertNotNull(bytes);
        assertTrue(bytes.length > 4);
        assertEquals("%PDF", new String(Arrays.copyOf(bytes, 4), StandardCharsets.US_ASCII));
    }

    @Test
    void getFileNameShouldReturnPdfFileName() {
        String fileName = service.getFileName(page);

        assertEquals("Export Minha Nota.pdf", fileName);
    }

    @Test
    void getMimeTypeShouldReturnPdfMimeType() {
        assertEquals(MimeTypeEnum.PDF, service.getMimeType());
    }
}

