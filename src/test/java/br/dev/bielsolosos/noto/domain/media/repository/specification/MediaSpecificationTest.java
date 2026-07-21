package br.dev.bielsolosos.noto.domain.media.repository.specification;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;
import br.dev.bielsolosos.noto.domain.media.repository.MediaR2Repository;
import br.dev.bielsolosos.noto.domain.users.model.Role;
import br.dev.bielsolosos.noto.domain.users.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MediaSpecificationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MediaR2Repository repository;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM media_r2").executeUpdate();

        testUser = new User();
        testUser.setUsername("testuser_media_" + UUID.randomUUID().toString().substring(0, 5));
        testUser.setEmail(UUID.randomUUID().toString() + "@test.com");
        testUser.setPassword("123456");
        testUser.setActive(true);
        testUser.setRoles(getUserRole());
        entityManager.persist(testUser);

        otherUser = new User();
        otherUser.setUsername("otheruser_media_" + UUID.randomUUID().toString().substring(0, 5));
        otherUser.setEmail(UUID.randomUUID().toString() + "@test.com");
        otherUser.setPassword("123456");
        otherUser.setActive(true);
        otherUser.setRoles(getUserRole());
        entityManager.persist(otherUser);

        entityManager.flush();
    }

    @Test
    void toPredicateWithFilterShouldMatchByFilenameAndUrlIgnoringCaseAndSpaces() {
        createMedia(testUser, "teste_image.png", "http://example.com/images/1");
        createMedia(testUser, "avatar.jpg", "http://example.com/teste_folder/2");
        createMedia(testUser, "document.pdf", "http://example.com/docs/3");

        var byFilename = repository.findAll(new MediaSpecification(testUser.getId(), "  TESTE_IMAGE  "));
        var byUrl = repository.findAll(new MediaSpecification(testUser.getId(), "TESTE_FOLDER"));

        assertEquals(1, byFilename.size());
        assertEquals("teste_image.png", byFilename.getFirst().getFilename());

        assertEquals(1, byUrl.size());
        assertEquals("avatar.jpg", byUrl.getFirst().getFilename());
    }

    @Test
    void toPredicateWithBlankFilterShouldNotApplyFilter() {
        createMedia(testUser, "image1.png", "http://example.com/1");
        createMedia(testUser, "image2.png", "http://example.com/2");

        List<MediaR2> allMedia = repository.findAll(new MediaSpecification(testUser.getId(), "   "));

        assertEquals(2, allMedia.size());
    }

    @Test
    void shouldOnlyReturnMediaOwnedByGivenUser() {
        createMedia(testUser, "mine1.png", "http://example.com/mine/1");
        createMedia(testUser, "mine2.png", "http://example.com/mine/2");
        createMedia(otherUser, "other1.png", "http://example.com/other/1");
        createMedia(otherUser, "other2.png", "http://example.com/other/2");

        List<MediaR2> testUserMedia = repository.findAll(new MediaSpecification(testUser.getId(), null));
        List<MediaR2> otherUserMedia = repository.findAll(new MediaSpecification(otherUser.getId(), null));

        assertEquals(2, testUserMedia.size());
        assertTrue(testUserMedia.stream().allMatch(m -> m.getUser().getId().equals(testUser.getId())));

        assertEquals(2, otherUserMedia.size());
        assertTrue(otherUserMedia.stream().allMatch(m -> m.getUser().getId().equals(otherUser.getId())));
    }

    @Test
    void shouldCombineUserFilterAndTextFilter() {
        createMedia(testUser, "shared.png", "http://example.com/shared/1");
        createMedia(testUser, "other.png", "http://example.com/x/2");
        createMedia(otherUser, "shared.png", "http://example.com/shared/3");

        List<MediaR2> result = repository.findAll(new MediaSpecification(testUser.getId(), "shared"));

        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.getFirst().getUser().getId());
    }

    private void createMedia(User user, String filename, String url) {
        MediaR2 media = new MediaR2();
        media.setFilename(filename);
        media.setUrl(url);
        media.setContentType("image/png");
        media.setSizeBytes(1024L);
        media.setUser(user);
        media.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(media);
        entityManager.flush();
    }

    private Set<Role> getUserRole() {
        Role roleUser = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_USER'", Role.class).getSingleResult();
        return Set.of(roleUser);
    }
}
