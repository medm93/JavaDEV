package pl.medm.javadev.repository;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import pl.medm.javadev.model.entity.Role;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@ContextConfiguration(classes = RoleRepository.class)
@SpringBootTest
public class RoleRepositoryTest {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

//    @Autowired
//    private TestEntityManager entityManager;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void roleIsNotNull() {
        List<Role> searchResult = roleRepository.findAll();
        Assertions.assertThat(searchResult).hasSize(2);
        Assertions.assertThat(searchResult).isNotNull();
    }

    @Test
    public void findRoleAdmin() {
        Role searchResult = roleRepository.findByRole(ROLE_ADMIN);
        Assertions.assertThat(searchResult.getRole()).isEqualTo(ROLE_ADMIN);
    }

    @Test
    public void findRoleUser() {
        Role searchResult = roleRepository.findByRole(ROLE_USER);
        Assertions.assertThat(searchResult.getRole()).isEqualTo(ROLE_USER);
    }

    @Test
    @Rollback
    public void saveAndFindRole() {
        Role role = new Role("ROLE_MODERATOR");
        roleRepository.save(role);
        Role searchResult = roleRepository.findByRole(role.getRole());

        Assertions.assertThat(searchResult.getRole()).isEqualTo(role.getRole());
    }

    @Test
    @Rollback
    public void saveAndDeleteRole() {
        Role role = new Role("ROLE_MODERATOR");
        roleRepository.save(role);
        roleRepository.delete(role);
        Role searchResult = roleRepository.findByRole(role.getRole());

        Assertions.assertThat(searchResult).isNull();
    }
}
