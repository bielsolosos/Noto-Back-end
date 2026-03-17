package space.bielsolososdev.noto.domain.pages.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.pages.repository.PageRepository;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.service.UserService;

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

}
