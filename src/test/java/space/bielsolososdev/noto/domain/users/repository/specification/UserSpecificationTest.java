package space.bielsolososdev.noto.domain.users.repository.specification;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import space.bielsolososdev.noto.api.model.user.CreateUserRequest;
import space.bielsolososdev.noto.domain.users.model.Role;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserSpecificationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void removeSeededAdmin() {
        entityManager.createNativeQuery("DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE username = 'admin')")
                .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users WHERE username = 'admin'")
                .executeUpdate();
    }

    @Test
    void toPredicateWithFilterShouldMatchByUsernameAndEmailIgnoringCaseAndSpaces() {
        createUser(new CreateUserRequest("alice", "alice@test.com", "123456", "123456"));
        createUser(new CreateUserRequest("bob", "bob@company.com", "123456", "123456"));

        var byUsername = repository.findAll(new UserSpecification("  ALI  ", null, null, null));
        var byEmail = repository.findAll(new UserSpecification("COMPANY.COM", null, null, null));

        assertEquals(1, byUsername.size());
        assertEquals("alice", byUsername.getFirst().getUsername());

        assertEquals(1, byEmail.size());
        assertEquals("bob", byEmail.getFirst().getUsername());
    }

    @Test
    void toPredicateWithIsActiveShouldFilterUsersByStatus() {
        createUser(new CreateUserRequest("active-user", "active-status@test.com", "123456", "123456"), true, OffsetDateTime.now());
        createUser(new CreateUserRequest("inactive-user", "inactive-status@test.com", "123456", "123456"), false, OffsetDateTime.now());

        List<User> activeUsers = repository.findAll(new UserSpecification("status@test.com", true, null, null));
        List<User> inactiveUsers = repository.findAll(new UserSpecification("status@test.com", false, null, null));

        assertEquals(1, activeUsers.size());
        assertEquals("active-user", activeUsers.getFirst().getUsername());

        assertEquals(1, inactiveUsers.size());
        assertEquals("inactive-user", inactiveUsers.getFirst().getUsername());
    }

    @Test
    void toPredicateWithCreatedDateRangeShouldReturnOnlyUsersInsideRange() {
        OffsetDateTime now = OffsetDateTime.now();
        createUser(new CreateUserRequest("old-user", "old@test.com", "123456", "123456"), true, now.minusDays(10));
        createUser(new CreateUserRequest("inside-range", "inside@test.com", "123456", "123456"), true, now.minusDays(4));
        createUser(new CreateUserRequest("recent-user", "recent@test.com", "123456", "123456"), true, now.minusDays(1));

        var spec = new UserSpecification(
                null,
                true,
                now.minusDays(5).toLocalDateTime(),
                now.minusDays(2).toLocalDateTime()
        );

        List<User> users = repository.findAll(spec);

        assertEquals(1, users.size());
        assertEquals("inside-range", users.getFirst().getUsername());
    }

    @Test
    void toPredicateWithCombinedFiltersShouldRespectAllCriteria() {
        OffsetDateTime now = OffsetDateTime.now();
        createUser(new CreateUserRequest("joao-admin", "joao-admin@test.com", "123456", "123456"), true, now.minusDays(3));
        createUser(new CreateUserRequest("joao-disabled", "joao-disabled@test.com", "123456", "123456"), false, now.minusDays(3));
        createUser(new CreateUserRequest("maria-admin", "maria@test.com", "123456", "123456"), true, now.minusDays(8));

        var spec = new UserSpecification(
                "joao",
                true,
                now.minusDays(5).toLocalDateTime(),
                now.minusDays(1).toLocalDateTime()
        );

        List<User> users = repository.findAll(spec);

        assertEquals(1, users.size());
        assertEquals("joao-admin", users.getFirst().getUsername());
    }

    @Test
    void toPredicateWithBlankFilterShouldNotApplyTextFilter() {
        createUser(new CreateUserRequest("user-1", "user1@test.com", "123456", "123456"), true, OffsetDateTime.now());
        createUser(new CreateUserRequest("user-2", "user2@test.com", "123456", "123456"), true, OffsetDateTime.now());

        List<User> users = repository.findAll(new UserSpecification("   ", true, null, null));

        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(User::isActive));
    }

    private void createUser(CreateUserRequest dto) {
        createUser(dto, true, OffsetDateTime.now());
    }

    private void createUser(CreateUserRequest dto, boolean active, OffsetDateTime createdAt) {
        User user = new User();
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(createdAt);
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setActive(active);
        user.setRoles(getUserRole());
        user.setUsername(dto.username());
        entityManager.persist(user);

        entityManager.flush();
        entityManager.createNativeQuery("UPDATE users SET created_at = ?, updated_at = ? WHERE id = ?")
                .setParameter(1, Timestamp.from(createdAt.toInstant()))
                .setParameter(2, Timestamp.from(createdAt.toInstant()))
                .setParameter(3, user.getId())
                .executeUpdate();
        entityManager.refresh(user);
    }

    private Set<Role> getUserRole() {
        Role roleUser = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_USER'", Role.class).getSingleResult();
        Role roleAdmin = entityManager.createQuery("SELECT r FROM Role r where r.name = 'ROLE_ADMIN'", Role.class).getSingleResult();
        return Set.of(roleUser, roleAdmin);
    }
}