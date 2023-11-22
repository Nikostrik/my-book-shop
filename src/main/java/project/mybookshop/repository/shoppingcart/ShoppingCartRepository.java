package project.mybookshop.repository.shoppingcart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser_Email(String email);
}
