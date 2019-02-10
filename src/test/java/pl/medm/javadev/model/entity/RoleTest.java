package pl.medm.javadev.model.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class RoleTest {

    @Autowired
    private TestEntityManager entityManager;

    private static Role ROLE_ADMIN;
    private static Role ROLE_USER;

    @BeforeAll
    public static void setUp() {
        ROLE_ADMIN = new Role("ROLE_ADMIN");
        ROLE_USER = new Role("ROLE_USER");
    }

    @Test
    void saveRoleAdmin() {
        Role actual = entityManager.persistAndFlush(ROLE_ADMIN);
        System.err.println(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(ROLE_ADMIN.getRole(), actual.getRole());
    }

    @Test
    void createRoleRoleException() {
        Throwable exception = Assertions.assertThrows(NullPointerException.class, () ->
                new Role()
        );
        System.err.println(exception);
    }
}
