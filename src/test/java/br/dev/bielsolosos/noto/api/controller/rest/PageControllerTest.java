package br.dev.bielsolosos.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import br.dev.bielsolosos.noto.api.mapper.page.PageRequest;
import br.dev.bielsolosos.noto.api.mapper.page.PageResponse;
import br.dev.bielsolosos.noto.api.model.MessageResponse;
import br.dev.bielsolosos.noto.api.model.page.PageSummaryResponse;
import br.dev.bielsolosos.noto.core.enums.ExportTypeEnum;
import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortByEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortOrderEnum;
import br.dev.bielsolosos.noto.domain.pages.model.Page;
import br.dev.bielsolosos.noto.domain.pages.model.dto.PageToExportDto;
import br.dev.bielsolosos.noto.domain.pages.service.PageService;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageControllerTest {

    @Mock
    private PageService pageService;

    @InjectMocks
    private PageController controller;

    private Page page;
    private UUID pageId;

    @BeforeEach
    void setUp() {
        pageId = UUID.randomUUID();

        page = new Page();
        page.setId(pageId);
        page.setTitle("Minha Nota");
        page.setContent("Conteúdo");
        page.setCreatedAt(OffsetDateTime.now().minusDays(1));
        page.setUpdatedAt(OffsetDateTime.now());
    }

    @Test
    void listAllPagesForSummarySuccess() {
        when(pageService.getAll(null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC)).thenReturn(List.of(page));

        ResponseEntity<List<PageSummaryResponse>> response = controller.listAllPagesForSummary(
                null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(page.getId(), response.getBody().get(0).id());
        assertEquals(page.getTitle(), response.getBody().get(0).title());
        verify(pageService, times(1)).getAll(null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC);
    }

    @Test
    void getPageSuccess() {
        when(pageService.getById(pageId)).thenReturn(page);

        ResponseEntity<PageResponse> response = controller.getPage(pageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(page.getId(), response.getBody().id());
        assertEquals(page.getTitle(), response.getBody().title());
        assertEquals(page.getContent(), response.getBody().content());
        verify(pageService, times(1)).getById(pageId);
    }

    @Test
    void deletePageSuccess() {
        ResponseEntity<MessageResponse> response = controller.deletePage(pageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Página deletado com sucesso", response.getBody().message());
        verify(pageService, times(1)).delete(pageId);
    }

    @Test
    void editPageContentSuccess() {
        PageRequest request = new PageRequest("Novo título", "Novo conteúdo");
        when(pageService.updateContent(pageId, request.title(), request.content())).thenReturn(page);

        ResponseEntity<PageResponse> response = controller.editPageContent(request, pageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(page.getId(), response.getBody().id());
        verify(pageService, times(1)).updateContent(pageId, request.title(), request.content());
    }

    @Test
    void createPageSuccess() {
        PageRequest request = new PageRequest("Minha Nota", "Conteúdo");
        when(pageService.createPage(request.title(), request.content())).thenReturn(page);

        ResponseEntity<PageResponse> response = controller.createPage(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(page.getId(), response.getBody().id());
        assertEquals(page.getTitle(), response.getBody().title());
        verify(pageService, times(1)).createPage(request.title(), request.content());
    }

    @Test
    void exportPageSuccess() {
        byte[] content = "# Minha Nota\n\nConteúdo".getBytes(StandardCharsets.UTF_8);
        PageToExportDto dto = new PageToExportDto("Export Minha Nota.md", MimeTypeEnum.MARKDOWN, content.length, content);
        when(pageService.exportPage(pageId, ExportTypeEnum.MD)).thenReturn(dto);

        ResponseEntity<Resource> response = controller.exportPage(pageId, ExportTypeEnum.MD);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MediaType.parseMediaType("text/markdown"), response.getHeaders().getContentType());
        assertEquals(content.length, response.getHeaders().getContentLength());
        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains("attachment"));
        assertTrue(contentDisposition.contains("filename=\"Export Minha Nota.md\""));
        assertTrue(contentDisposition.contains("filename*=UTF-8''Export%20Minha%20Nota.md"));
        assertFalse(contentDisposition.contains("=?UTF-8?Q?"));
        assertArrayEquals(content, ((ByteArrayResource) response.getBody()).getByteArray());
        verify(pageService, times(1)).exportPage(pageId, ExportTypeEnum.MD);
    }

    @Test
    void exportPageShouldPropagateBusinessException() {
        when(pageService.exportPage(pageId, ExportTypeEnum.NOTO_PDF)).thenThrow(new BusinessException("sem permissão"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.exportPage(pageId, ExportTypeEnum.NOTO_PDF));

        assertEquals("sem permissão", ex.getMessage());
        verify(pageService, times(1)).exportPage(pageId, ExportTypeEnum.NOTO_PDF);
    }
}
