package space.bielsolososdev.noto.domain.pages.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.pages.repository.PageRepository;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.service.UserService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private UserService userService;

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
        page.setContent("Conteúdo da nota");
        page.setUser(owner);
    }

    @Test
    void getById_deveRetornarPagina_quandoUsuarioEhDono() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(userService.getMe()).thenReturn(owner);

        Page resultado = pageService.getById(pageId);

        assertNotNull(resultado);
        assertEquals(pageId, resultado.getId());
        assertEquals("Minha Nota", resultado.getTitle());

        verify(pageRepository, times(1)).findById(pageId);
        verify(userService, times(1)).getMe();
    }

    @Test
    void getById_deveLancarExcecao_quandoPaginaNaoEncontrada() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> pageService.getById(pageId));

        assertEquals("Página não encontrada no sistema.", ex.getMessage());
        verify(pageRepository, times(1)).findById(pageId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getById_deveLancarExcecao_quandoUsuarioNaoEhDono() {
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(userService.getMe()).thenReturn(otherUser);

        assertThrows(BusinessException.class, () -> pageService.getById(pageId));

        verify(userService, times(1)).getMe();
    }

    @Test
    void createPageSuccessfullyWithoutParams() {
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Page page = pageService.createPage(null, null);

        assertTrue(page.getTitle().contains("Nova Nota dia:"));
        assertTrue(page.getContent().isEmpty());
        verify(pageRepository, times(1)).save(any(Page.class));
        verify(userService, times(1)).getMe();
    }

    @Test
    void createPageSuccessfullyWithParams() {
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Page page = pageService.createPage("PageTeste", "Conteúdo Diferentão");

        assertTrue(page.getTitle().contains("PageTeste"));
        assertEquals("Conteúdo Diferentão", page.getContent());

        verify(pageRepository, times(1)).save(any(Page.class));
        verify(userService, times(1)).getMe();
    }

    @Test
    void updateContentSucessfullyWithoutTitle(){
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final String UPDATED_CONTENT = "NOVO CONTEÚDO ATUALIZADO";
        Page updatedPage = pageService.updateContent(page.getId(), page.getTitle(), UPDATED_CONTENT);

        assertEquals(page.getTitle(), updatedPage.getTitle());
        assertEquals(UPDATED_CONTENT, updatedPage.getContent());

    }

    @Test
    void updateContentSucessfullyWithTitle(){
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(page));
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final String UPDATED_CONTENT = "NOVO CONTEÚDO ATUALIZADO";
        final String UPDATED_TITLE = "TITULO NOVO PAE";
        Page updatedPage = pageService.updateContent(page.getId(), UPDATED_TITLE, UPDATED_CONTENT);

        assertEquals(UPDATED_TITLE, updatedPage.getTitle());
        assertEquals(UPDATED_CONTENT, updatedPage.getContent());

    }

    @Test
    void getAllOrderedByUpdatedAtDesc() {
        when(userService.getMe()).thenReturn(owner);

        Page maisAntiga = new Page();
        maisAntiga.setTitle("Antiga");
        maisAntiga.setUpdatedAt(OffsetDateTime.now().minusDays(2));

        Page maisRecente = new Page();
        maisRecente.setTitle("Recente");
        maisRecente.setUpdatedAt(OffsetDateTime.now());

        // A lista que o repository retornaria JÁ ORDENADA (Descendente: mais recente primeiro)
        List<Page> listaEsperada = List.of(maisRecente, maisAntiga);

        when(pageRepository.findByUserIdOrderByUpdatedAtDesc(any()))
                .thenReturn(listaEsperada);

        List<Page> allFoundPages = pageService.getAll();

        assertNotNull(allFoundPages);
        assertEquals(2, allFoundPages.size());

        // Garante que a primeira da lista é a mais recente
        assertEquals("Recente", allFoundPages.get(0).getTitle());
        assertEquals(listaEsperada, allFoundPages);
    }

    @Test
    void getAllOrderedByUpdatedAtDescArchived() {
        when(userService.getMe()).thenReturn(owner);

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
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pageService.archivePage(page.getId());

        verify(pageRepository, times(1)).findById(page.getId());
    }

    @Test
    void assertPageIsUnarchived() {
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.save(any(Page.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pageService.unarchivePage(page.getId());

        verify(pageRepository, times(1)).findById(page.getId());
    }

    @Test
    void assertDeletePage(){
        when(userService.getMe()).thenReturn(owner);
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));
        pageService.delete(page.getId());

        verify(pageRepository, times(1)).findById(page.getId());
        verify(pageRepository, times(1)).delete(page);
    }

    @Test
    void assertCantDeletePage(){
        when(userService.getMe()).thenReturn(otherUser);
        when(pageRepository.findById(page.getId())).thenReturn(Optional.of(page));

        // O assertThrows garante que a BusinessException seja lançada
        assertThrows(BusinessException.class, () -> {
            pageService.delete(page.getId());
        });

        // O findById é chamado para validar, mas o delete NÃO pode ser chamado
        verify(pageRepository, times(1)).findById(page.getId());
        verify(pageRepository, never()).delete(any(Page.class));
    }

}
