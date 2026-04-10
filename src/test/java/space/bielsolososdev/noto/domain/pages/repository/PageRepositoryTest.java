package space.bielsolososdev.noto.domain.pages.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import space.bielsolososdev.noto.api.mapper.page.PageRequest;
import space.bielsolososdev.noto.api.model.user.CreateUserRequest;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.users.model.Role;
import space.bielsolososdev.noto.domain.users.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PageRepository repository;

    @Test
    void assertSizeFrom2Mocks() {
        User user = createUser(new CreateUserRequest("test-assertSizerFrom2Mocks", "test-email@gmail.com", "a", "a"));
        createPage(new PageRequest("Test", "Test content"), user);
        createPage(new PageRequest("Test2", "Test content 2"), user);

        List<Page> pages = repository.findByUserId(user.getId());

        assertEquals(2, pages.size());
    }

    @Test
    void assertEmptyRequest() {
        User user = createUser(new CreateUserRequest("test-assertSizerFrom2Mocks", "test-email@gmail.com", "a", "a"));
        List<Page> pages = repository.findByUserId(user.getId());
        assertTrue(pages.isEmpty());
    }

    @Test
    @DisplayName("Mocka 2 páginas uma ativa e outra não e depois consulta.")
    void assertEmptyPageWithOneArchivedAndNotAnother() {
        User user = createUser(new CreateUserRequest("test-assertSizerFrom2Mocks", "test-email@gmail.com", "a", "a"));
        createPage(new PageRequest("Test", "Test content"), user, true);
        createPage(new PageRequest("Test2", "Test content 2"), user, false);

        List<Page> pages = repository.findByUserIdAndArchivedTrueOrderByUpdatedAtDesc(user.getId());
        assertEquals(1, pages.size());
    }

    @Test
    @DisplayName("Mocka 2 páginas duas ativas e consulta.")
    void assertEmptyPageWith2ItensArchived() {
        User user = createUser(new CreateUserRequest("test-assertSizerFrom2Mocks", "test-email@gmail.com", "a", "a"));
        createPage(new PageRequest("Test", "Test content"), user, false);
        createPage(new PageRequest("Test2", "Test content 2"), user, false);

        List<Page> pages = repository.findByUserIdAndArchivedTrueOrderByUpdatedAtDesc(user.getId());
        assertEquals(0, pages.size());
    }

    private Page createPage(PageRequest pageRequest, User user, boolean archived) {
        Page page = createPage(pageRequest, user);
        page.setArchived(archived);
        entityManager.merge(page);
        return page;
    }

    private Page createPage(PageRequest pojo, User user) {
        Page page = new Page();
        page.setTitle(pojo.title());
        page.setContent(pojo.content());
        page.setCreatedAt(OffsetDateTime.now());
        page.setUpdatedAt(OffsetDateTime.now());
        page.setUser(user);
        entityManager.persist(page);
        return page;
    }

    private User createUser(CreateUserRequest dto) {
        User user = new User();
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setActive(true);
        user.setRoles(getUserRole());
        user.setUsername(dto.username());
        entityManager.persist(user);
        return user;
    }

    private Set<Role> getUserRole() {
        Role roleUser = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_USER'", Role.class).getSingleResult();
        Role roleAdmin = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_ADMIN'", Role.class).getSingleResult();
        return Set.of(roleUser, roleAdmin);
    }
}

