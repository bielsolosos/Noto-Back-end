package br.dev.bielsolosos.noto.domain.pages.export.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;
import br.dev.bielsolosos.noto.domain.pages.model.Page;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageExporterServiceMdTest {

    private final PageExporterServiceMd service = new PageExporterServiceMd();
    private Page page;

    @BeforeEach
    void setUp() {
        page = new Page();
        page.setTitle("Minha Nota");
        page.setContent("Conteudo da nota");
    }

    @Test
    void getBytesFromPageShouldReturnFormattedMarkdown() {
        byte[] bytes = service.getBytesFromPage(page);

        assertEquals("# Minha Nota\n\nConteudo da nota", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    void getFileNameShouldReturnMdFileName() {
        String fileName = service.getFileName(page);

        assertEquals("Export Minha Nota.md", fileName);
    }

    @Test
    void getMimeTypeShouldReturnMarkdownMimeType() {
        assertEquals(MimeTypeEnum.MARKDOWN, service.getMimeType());
    }
}

