package project.mybookshop.repository.cartitem;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
