package br.dev.bielsolosos.noto.domain.pages.repository.specification;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortByEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortOrderEnum;
import br.dev.bielsolosos.noto.domain.pages.model.Page;
import br.dev.bielsolosos.noto.domain.pages.repository.PageRepository;
import br.dev.bielsolosos.noto.domain.users.model.Role;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.repository.UserRepository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageSpecificationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        owner = createUser("owner", "owner@test.com");
        otherUser = createUser("other", "other@test.com");
    }

    @Test
    void shouldFilterPagesByUserId() {
        createPage(owner, "Owner Page 1", OffsetDateTime.now().minusDays(1));
        createPage(owner, "Owner Page 2", OffsetDateTime.now());
        createPage(otherUser, "Other Page", OffsetDateTime.now());

        List<Page> ownerPages = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC));

        assertEquals(2, ownerPages.size());
        assertTrue(ownerPages.stream().allMatch(p -> p.getUser().getId().equals(owner.getId())));
    }

    @Test
    void shouldFilterPagesByTitleQuery() {
        createPage(owner, "Primeira Nota", OffsetDateTime.now());
        createPage(owner, "Segunda Nota", OffsetDateTime.now().minusDays(1));
        createPage(owner, "Terceira Nota", OffsetDateTime.now().minusDays(2));

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), "Primeira", PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC));

        assertEquals(1, result.size());
        assertEquals("Primeira Nota", result.getFirst().getTitle());
    }

    @Test
    void shouldFilterPagesByTitleQueryCaseInsensitive() {
        createPage(owner, "Primeira Nota", OffsetDateTime.now());
        createPage(owner, "SEGUNDA Nota", OffsetDateTime.now());

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), "segunda", PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC));

        assertEquals(1, result.size());
    }

    @Test
    void shouldSortByUpdatedAtDescending() {
        OffsetDateTime now = OffsetDateTime.now();
        createPageWithUpdatedAt(owner, "Old Page", now.minusDays(5));
        createPageWithUpdatedAt(owner, "New Page", now.minusDays(1));
        createPageWithUpdatedAt(owner, "Middle Page", now.minusDays(3));

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC));

        assertEquals(3, result.size());
        assertEquals("New Page", result.get(0).getTitle());
        assertEquals("Middle Page", result.get(1).getTitle());
        assertEquals("Old Page", result.get(2).getTitle());
    }

    @Test
    void shouldSortByUpdatedAtAscending() {
        OffsetDateTime now = OffsetDateTime.now();
        createPageWithUpdatedAt(owner, "Old Page", now.minusDays(5));
        createPageWithUpdatedAt(owner, "New Page", now.minusDays(1));
        createPageWithUpdatedAt(owner, "Middle Page", now.minusDays(3));

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.ASC));

        assertEquals(3, result.size());
        assertEquals("Old Page", result.get(0).getTitle());
        assertEquals("Middle Page", result.get(1).getTitle());
        assertEquals("New Page", result.get(2).getTitle());
    }

    @Test
    void shouldSortByCreatedAtDescending() {
        OffsetDateTime now = OffsetDateTime.now();
        createPageWithCreatedAt(owner, "Old Page", now.minusDays(5));
        createPageWithCreatedAt(owner, "New Page", now.minusDays(1));
        createPageWithCreatedAt(owner, "Middle Page", now.minusDays(3));

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.CREATED_AT, PageSortOrderEnum.DESC));

        assertEquals(3, result.size());
        assertEquals("New Page", result.get(0).getTitle());
        assertEquals("Middle Page", result.get(1).getTitle());
        assertEquals("Old Page", result.get(2).getTitle());
    }

    @Test
    void shouldSortByCreatedAtAscending() {
        OffsetDateTime now = OffsetDateTime.now();
        createPageWithCreatedAt(owner, "Old Page", now.minusDays(5));
        createPageWithCreatedAt(owner, "New Page", now.minusDays(1));
        createPageWithCreatedAt(owner, "Middle Page", now.minusDays(3));

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.CREATED_AT, PageSortOrderEnum.ASC));

        assertEquals(3, result.size());
        assertEquals("Old Page", result.get(0).getTitle());
        assertEquals("Middle Page", result.get(1).getTitle());
        assertEquals("New Page", result.get(2).getTitle());
    }

    @Test
    void shouldSortByTitleAscending() {
        createPage(owner, "Zebra Page", OffsetDateTime.now());
        createPage(owner, "Apple Page", OffsetDateTime.now());
        createPage(owner, "Mango Page", OffsetDateTime.now());

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.TITLE, PageSortOrderEnum.ASC));

        assertEquals(3, result.size());
        assertEquals("Apple Page", result.get(0).getTitle());
        assertEquals("Mango Page", result.get(1).getTitle());
        assertEquals("Zebra Page", result.get(2).getTitle());
    }

    @Test
    void shouldSortByTitleDescending() {
        createPage(owner, "Zebra Page", OffsetDateTime.now());
        createPage(owner, "Apple Page", OffsetDateTime.now());
        createPage(owner, "Mango Page", OffsetDateTime.now());

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.TITLE, PageSortOrderEnum.DESC));

        assertEquals(3, result.size());
        assertEquals("Zebra Page", result.get(0).getTitle());
        assertEquals("Mango Page", result.get(1).getTitle());
        assertEquals("Apple Page", result.get(2).getTitle());
    }

    @Test
    void shouldCombineQueryAndSortFilters() {
        OffsetDateTime now = OffsetDateTime.now();
        createPageWithUpdatedAt(owner, "Test Page A", now.minusDays(3));
        createPageWithUpdatedAt(owner, "Test Page B", now.minusDays(1));
        createPageWithUpdatedAt(owner, "Other Page", now);

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), "Test", PageSortByEnum.UPDATED_AT, PageSortOrderEnum.ASC));

        assertEquals(2, result.size());
        assertEquals("Test Page A", result.get(0).getTitle());
        assertEquals("Test Page B", result.get(1).getTitle());
    }

    @Test
    void shouldIgnoreBlankQuery() {
        createPage(owner, "Page 1", OffsetDateTime.now());
        createPage(owner, "Page 2", OffsetDateTime.now());

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), "   ", PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC));

        assertEquals(2, result.size());
    }

    @Test
    void shouldIgnoreNullQuery() {
        createPage(owner, "Page 1", OffsetDateTime.now());
        createPage(owner, "Page 2", OffsetDateTime.now());

        List<Page> result = pageRepository.findAll(
                new PageSpecification(owner.getId(), null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC));

        assertEquals(2, result.size());
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setActive(true);
        user.setRoles(getUserRole());
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Page createPage(User user, String title, OffsetDateTime updatedAt) {
        Page page = new Page();
        page.setUser(user);
        page.setTitle(title);
        page.setContent("");
        page.setArchived(false);
        entityManager.persist(page);
        entityManager.flush();
        updatePageTimestamps(page.getId(), updatedAt, updatedAt);
        entityManager.refresh(page);
        return page;
    }

    private Page createPageWithUpdatedAt(User user, String title, OffsetDateTime updatedAt) {
        Page page = new Page();
        page.setUser(user);
        page.setTitle(title);
        page.setContent("");
        page.setArchived(false);
        entityManager.persist(page);
        entityManager.flush();
        updatePageTimestamps(page.getId(), updatedAt.minusDays(1), updatedAt);
        entityManager.refresh(page);
        return page;
    }

    private Page createPageWithCreatedAt(User user, String title, OffsetDateTime createdAt) {
        Page page = new Page();
        page.setUser(user);
        page.setTitle(title);
        page.setContent("");
        page.setArchived(false);
        entityManager.persist(page);
        entityManager.flush();
        updatePageTimestamps(page.getId(), createdAt, createdAt.plusDays(1));
        entityManager.refresh(page);
        return page;
    }

    private void updatePageTimestamps(UUID pageId, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        entityManager.createNativeQuery("UPDATE pages SET created_at = ?, updated_at = ? WHERE id = ?")
                .setParameter(1, Timestamp.from(createdAt.toInstant()))
                .setParameter(2, Timestamp.from(updatedAt.toInstant()))
                .setParameter(3, pageId)
                .executeUpdate();
    }

    private Set<Role> getUserRole() {
        Role roleUser = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_USER'", Role.class).getSingleResult();
        return Set.of(roleUser);
    }
}
