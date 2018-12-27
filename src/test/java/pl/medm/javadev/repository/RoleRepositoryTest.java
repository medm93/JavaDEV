package pl.medm.javadev.repository;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.medm.javadev.model.Role;
import pl.medm.javadev.repository.RoleRepository;

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
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

//    @Autowired
//    private TestEntityManager entityManager;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void givenFindNoRole_whenRepository_thenEmpty() {
        List<Role> searchResult = roleRepository.findAll();
        Assertions.assertThat(searchResult).hasSize(2);
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
}
