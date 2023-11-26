package project.mybookshop.repository.order;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Email(String email, Pageable pageable);
}
