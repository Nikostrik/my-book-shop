package project.mybookshop.repository.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import project.mybookshop.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    private static final String TEST_EMAIL = "test@gmail.com";
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find a user by valid email")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_WithValidEmail_ReturnUser() {
        Optional<User> actual = userRepository.findByEmail(TEST_EMAIL);

        assertNotNull(actual);
        assertEquals(TEST_EMAIL, actual.get().getEmail());
    }

    @Test
    @DisplayName("""
            Find user by non existing email
            """)
    void findByUserEmail_ByNonExistingEmail_ReturnNull() {
        Optional<User> shoppingCart = userRepository.findByEmail(TEST_EMAIL);

        assertEquals(Optional.empty(), shoppingCart);
    }
}
