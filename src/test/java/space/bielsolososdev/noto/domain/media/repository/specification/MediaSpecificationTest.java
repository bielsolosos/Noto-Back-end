package space.bielsolososdev.noto.domain.media.repository.specification;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import space.bielsolososdev.noto.domain.media.model.MediaR2;
import space.bielsolososdev.noto.domain.media.repository.MediaR2Repository;
import space.bielsolososdev.noto.domain.users.model.Role;
import space.bielsolososdev.noto.domain.users.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MediaSpecificationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MediaR2Repository repository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Remove everything because we have cross-test dependencies optionally
        entityManager.createNativeQuery("DELETE FROM media_r2").executeUpdate();

        testUser = new User();
        testUser.setUsername("testuser_media_" + UUID.randomUUID().toString().substring(0, 5));
        testUser.setEmail(UUID.randomUUID().toString() + "@test.com");
        testUser.setPassword("123456");
        testUser.setActive(true);
        testUser.setRoles(getUserRole());
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void toPredicateWithFilterShouldMatchByFilenameAndUrlIgnoringCaseAndSpaces() {
        createMedia("teste_image.png", "http://example.com/images/1");
        createMedia("avatar.jpg", "http://example.com/teste_folder/2");
        createMedia("document.pdf", "http://example.com/docs/3");

        var byFilename = repository.findAll(new MediaSpecification("  TESTE_IMAGE  "));
        var byUrl = repository.findAll(new MediaSpecification("TESTE_FOLDER"));

        assertEquals(1, byFilename.size());
        assertEquals("teste_image.png", byFilename.getFirst().getFilename());

        assertEquals(1, byUrl.size());
        assertEquals("avatar.jpg", byUrl.getFirst().getFilename());
    }

    @Test
    void toPredicateWithBlankFilterShouldNotApplyFilter() {
        createMedia("image1.png", "http://example.com/1");
        createMedia("image2.png", "http://example.com/2");

        List<MediaR2> allMedia = repository.findAll(new MediaSpecification("   "));

        assertEquals(2, allMedia.size());
    }

    private void createMedia(String filename, String url) {
        MediaR2 media = new MediaR2();
        media.setFilename(filename);
        media.setUrl(url);
        media.setContentType("image/png");
        media.setSizeBytes(1024L);
        media.setUser(testUser);
        media.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(media);
        entityManager.flush();
    }

    private Set<Role> getUserRole() {
        Role roleUser = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_USER'", Role.class).getSingleResult();
        return Set.of(roleUser);
    }
}
