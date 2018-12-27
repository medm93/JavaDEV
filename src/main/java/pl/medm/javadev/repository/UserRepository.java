package pl.medm.javadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.medm.javadev.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    User findByEmailOrIndexNumber(String email, String indexNumber);
}
