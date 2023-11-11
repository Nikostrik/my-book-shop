package project.mybookshop.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
