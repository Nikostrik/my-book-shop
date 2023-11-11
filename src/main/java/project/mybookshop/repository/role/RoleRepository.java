package project.mybookshop.repository.role;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> getRoleByName(Role.RoleName name);
}
