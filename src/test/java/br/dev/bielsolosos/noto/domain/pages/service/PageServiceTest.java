package br.dev.bielsolosos.noto.domain.pages.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import br.dev.bielsolosos.noto.core.enums.ExportTypeEnum;
import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortByEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortOrderEnum;
import br.dev.bielsolosos.noto.domain.pages.model.Page;
import br.dev.bielsolosos.noto.domain.pages.model.dto.PageToExportDto;
import br.dev.bielsolosos.noto.domain.pages.repository.PageRepository;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.service.MeService;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

    private static final String RICH_MARKDOWN_CONTENT = String.join("\n",
            "Introducao com **negrito**, *italico* e `inline code`.",
            "",
            "## Checklist",
            "- [x] Item concluido",
            "- [ ] Item pendente",
            "",
            "## Tabela",
            "| Nome | Valor |",
            "|---|---:|",
            "| Taxa | 12.5 |",
            "",
            "> Bloco de citacao para custom renderer",
            "",
            "```java",
            "public class Demo {",
            "    public static void main(String[] args) {",
            "        System.out.println(\"ok\");",
            "    }",
            "}",
            "```",
            "",
            "Link util: [Noto](https://example.com)",
            "",
            "![Imagem de teste](https://example.com/image.png)"
    );

    @Mock
    private PageRepository pageRepository;

    @Mock
    private MeService meService;

    @InjectMocks
    private PageService pageService;

    private User owner;
    private User otherUser;
    private Page page;
    private UUID pageId;

    @BeforeEach
    void setUp() {
        pageId = UUID.randomUUID();

        owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setUsername("biel");

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("outro");

        page = new Page();
        page.setId(pageId);
        page.setTitle("Minha Nota");
        page.setContent(RICH_MARKDOWN_CONTENT);
        page.setUser(owner);
    }

    @Test
    void getById_deveRetornarPagina_quandoUsuarioEhDono() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);

        Page resultado = pageService.getById(pageId);

        assertNotNull(resultado);
        assertEquals(pageId, resultado.getId());
        assertEquals("Minha Nota", resultado.getTitle());

        verify(pageRepository, times(1)).findById(pageId);
        verify(meService, times(1)).getMe();
    }

    @Test
    void getById_deveLancarExcecao_quandoPaginaNaoEncontrada() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pageService.getById(pageId));

        assertEquals("Página não encontrada no sistema.", ex.getMessage());
        verify(pageRepository, times(1)).findById(pageId);
        verifyNoMoreInteractions(meService);
    }

    @Test
    void getById_deveLancarExcecao_quandoUsuarioNaoEhDono() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(otherUser);

        assertThrows(BusinessException.class, () -> pageService.getById(pageId));

        verify(meService, times(1)).getMe();
    }

    @Test
    void createPageSuccessfullyWithoutParams() {
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Page page = pageService.createPage(null, null);

        assertTrue(page.getTitle().contains("Nova Nota dia:"));
        assertTrue(page.getContent().isEmpty());
        verify(pageRepository, times(1)).save(any(Page.class));
        verify(meService, times(1)).getMe();
    }

    @Test
    void createPageSuccessfullyWithParams() {
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Page page = pageService.createPage("PageTeste", "Conteúdo Diferentão");

        assertTrue(page.getTitle().contains("PageTeste"));
        assertEquals("Conteúdo Diferentão", page.getContent());

        verify(pageRepository, times(1)).save(any(Page.class));
        verify(meService, times(1)).getMe();
    }

    @Test
    void updateContentSucessfullyWithoutTitle(){
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final String UPDATED_CONTENT = "NOVO CONTEÚDO ATUALIZADO";
        Page updatedPage = pageService.updateContent(page.getId(), page.getTitle(), UPDATED_CONTENT);

        assertEquals(page.getTitle(), updatedPage.getTitle());
        assertEquals(UPDATED_CONTENT, updatedPage.getContent());

    }

    @Test
    void updateContentSucessfullyWithTitle(){
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final String UPDATED_CONTENT = "NOVO CONTEÚDO ATUALIZADO";
        final String UPDATED_TITLE = "TITULO NOVO PAE";
        Page updatedPage = pageService.updateContent(page.getId(), UPDATED_TITLE, UPDATED_CONTENT);

        assertEquals(UPDATED_TITLE, updatedPage.getTitle());
        assertEquals(UPDATED_CONTENT, updatedPage.getContent());

    }

    @Test
    void getAllOrderedByUpdatedAtDesc() {
        when(meService.getMe()).thenReturn(owner);

        Page maisAntiga = new Page();
        maisAntiga.setTitle("Antiga");
        maisAntiga.setUpdatedAt(OffsetDateTime.now().minusDays(2));

        Page maisRecente = new Page();
        maisRecente.setTitle("Recente");
        maisRecente.setUpdatedAt(OffsetDateTime.now());

        List<Page> listaEsperada = List.of(maisRecente, maisAntiga);

        when(pageRepository.findAll(any(Specification.class)))
                .thenReturn(listaEsperada);

        List<Page> allFoundPages = pageService.getAll();

        assertNotNull(allFoundPages);
        assertEquals(2, allFoundPages.size());

        assertEquals("Recente", allFoundPages.get(0).getTitle());
        assertEquals(listaEsperada, allFoundPages);
    }

    @Test
    void getAllWithSortParams() {
        when(meService.getMe()).thenReturn(owner);

        Page pageA = new Page();
        pageA.setTitle("A Página");
        pageA.setCreatedAt(OffsetDateTime.now());

        Page pageZ = new Page();
        pageZ.setTitle("Z Página");
        pageZ.setCreatedAt(OffsetDateTime.now().minusDays(1));

        List<Page> listaEsperada = List.of(pageA, pageZ);

        when(pageRepository.findAll(any(Specification.class)))
                .thenReturn(listaEsperada);

        List<Page> allFoundPages = pageService.getAll(null, PageSortByEnum.TITLE, PageSortOrderEnum.ASC);

        assertNotNull(allFoundPages);
        assertEquals(2, allFoundPages.size());
        assertEquals("A Página", allFoundPages.get(0).getTitle());
        assertEquals(listaEsperada, allFoundPages);
    }

    @Test
    void getAllWithQuery() {
        when(meService.getMe()).thenReturn(owner);

        Page page = new Page();
        page.setTitle("Teste Página");
        page.setUpdatedAt(OffsetDateTime.now());

        List<Page> listaEsperada = List.of(page);

        when(pageRepository.findAll(any(Specification.class)))
                .thenReturn(listaEsperada);

        List<Page> allFoundPages = pageService.getAll("Teste", PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC);

        assertNotNull(allFoundPages);
        assertEquals(1, allFoundPages.size());
        assertEquals("Teste Página", allFoundPages.get(0).getTitle());
    }

    @Test
    void getAllOrderedByUpdatedAtDescArchived() {
        when(meService.getMe()).thenReturn(owner);

        Page maisAntiga = new Page();
        maisAntiga.setTitle("Antiga");
        maisAntiga.setUpdatedAt(OffsetDateTime.now().minusDays(2));
        maisAntiga.setArchived(true);

        Page maisRecente = new Page();
        maisRecente.setTitle("Recente");
        maisRecente.setUpdatedAt(OffsetDateTime.now());
        maisRecente.setArchived(true);

        // A lista que o repository retornaria JÁ ORDENADA (Descendente: mais recente primeiro)
        List<Page> listaEsperada = List.of(maisRecente, maisAntiga);

        when(pageRepository.findByUserIdAndArchivedTrueOrderByUpdatedAtDesc(any()))
                .thenReturn(listaEsperada);

        List<Page> allFoundPages = pageService.getAllArchivedPages();

        allFoundPages.stream().forEach(page -> {
            assertTrue(page.isArchived());
        });
    }

    @Test
    void assertPageIsArchived() {
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pageService.archivePage(page.getId());

        verify(pageRepository, times(1)).findById(page.getId());
    }

    @Test
    void assertPageIsUnarchived() {
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pageService.unarchivePage(page.getId());

        verify(pageRepository, times(1)).findById(page.getId());
    }

    @Test
    void assertDeletePage(){
        when(meService.getMe()).thenReturn(owner);
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));
        pageService.delete(page.getId());

        verify(pageRepository, times(1)).findById(page.getId());
        verify(pageRepository, times(1)).delete(page);
    }

    @Test
    void assertCantDeletePage(){
        when(meService.getMe()).thenReturn(otherUser);
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));

        // O assertThrows garante que a BusinessException seja lançada
        assertThrows(BusinessException.class, () -> {
            pageService.delete(page.getId());
        });

        // O findById é chamado para validar, mas o delete NÃO pode ser chamado
        verify(pageRepository, times(1)).findById(page.getId());
        verify(pageRepository, never()).delete(any(Page.class));
    }

    @Test
    void exportPageMdSuccessfully() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);

        PageToExportDto dto = pageService.exportPage(pageId, ExportTypeEnum.MD);

        assertNotNull(dto);
        assertEquals("Export Minha Nota.md", dto.fileName());
        assertEquals(MimeTypeEnum.MARKDOWN, dto.mimeType());
        assertEquals(dto.contentLength(), dto.resource().length);
        assertEquals("# Minha Nota\n\n" + RICH_MARKDOWN_CONTENT, new String(dto.resource(), StandardCharsets.UTF_8));
        verify(pageRepository, times(1)).findById(pageId);
        verify(meService, times(1)).getMe();
    }

    @Test
    void exportPagePdfSuccessfully() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(owner);

        PageToExportDto dto = pageService.exportPage(pageId, ExportTypeEnum.NOTO_PDF);

        assertNotNull(dto);
        assertEquals("Export Minha Nota.pdf", dto.fileName());
        assertEquals(MimeTypeEnum.PDF, dto.mimeType());
        assertEquals(dto.contentLength(), dto.resource().length);
        assertTrue(dto.resource().length > 4);
        assertEquals("%PDF", new String(Arrays.copyOf(dto.resource(), 4), StandardCharsets.US_ASCII));
        verify(pageRepository, times(1)).findById(pageId);
        verify(meService, times(1)).getMe();
    }

    @Test
    void exportPageShouldThrowWhenPageNotFound() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pageService.exportPage(pageId, ExportTypeEnum.MD));

        assertEquals("Página não encontrada no sistema.", ex.getMessage());
        verify(pageRepository, times(1)).findById(pageId);
        verifyNoMoreInteractions(meService);
    }

    @Test
    void exportPageShouldThrowWhenUserHasNoPermission() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(meService.getMe()).thenReturn(otherUser);

        assertThrows(BusinessException.class, () -> pageService.exportPage(pageId, ExportTypeEnum.MD));

        verify(pageRepository, times(1)).findById(pageId);
        verify(meService, times(1)).getMe();
    }

}
