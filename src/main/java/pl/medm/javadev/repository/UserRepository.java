package pl.medm.javadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.medm.javadev.model.entity.Role;
import pl.medm.javadev.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    User findByEmailOrIndexNumber(String email, String indexNumber);

    boolean existsByEmailOrIndexNumber(String email, String indexNumber);

    boolean existsByRoles(String role);
}
