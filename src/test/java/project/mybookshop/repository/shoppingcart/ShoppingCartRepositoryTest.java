package project.mybookshop.repository.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import project.mybookshop.model.ShoppingCart;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    private static final String TEST_EMAIL = "test@gmail.com";
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("""
            Find shopping cart by existing user email
            """)
    @Sql(scripts = {
            "classpath:database/user/add-user.sql",
            "classpath:database/shopping_cart/add-shopping-cart.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shopping_cart/remove-shopping-cart.sql",
            "classpath:database/user/remove-user.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserEmail_ByExistingEmail_ReturnShoppingCart() {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository
                .findByUser_Email(TEST_EMAIL);

        assertNotNull(shoppingCart);
        assertEquals(TEST_EMAIL, shoppingCart.get().getUser().getEmail());
    }

    @Test
    @DisplayName("""
            Find shopping cart by non existing user email
            """)
    void findByUserEmail_ByNonExistingEmail_ReturnNull() {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository
                .findByUser_Email(TEST_EMAIL);

        assertEquals(Optional.empty(), shoppingCart);
    }
}
