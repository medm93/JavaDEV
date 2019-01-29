package pl.medm.javadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.medm.javadev.model.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRole(String role);

    Boolean existsByRole(String role);
}
