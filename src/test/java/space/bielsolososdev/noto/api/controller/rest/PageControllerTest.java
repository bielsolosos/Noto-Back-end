package space.bielsolososdev.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import space.bielsolososdev.noto.api.mapper.PageRequest;
import space.bielsolososdev.noto.api.mapper.PageResponse;
import space.bielsolososdev.noto.api.model.MessageResponse;
import space.bielsolososdev.noto.api.model.page.PageSummaryResponse;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.pages.service.PageService;

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
        when(pageService.getAll()).thenReturn(List.of(page));

        ResponseEntity<List<PageSummaryResponse>> response = controller.listAllPagesForSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(page.getId(), response.getBody().get(0).id());
        assertEquals(page.getTitle(), response.getBody().get(0).title());
        verify(pageService, times(1)).getAll();
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
}
