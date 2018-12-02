package pl.medm.javadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.medm.javadev.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailOrIndexNumber(String emailOrIndexNumber);
}
