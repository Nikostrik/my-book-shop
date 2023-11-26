package project.mybookshop.repository.orderitem;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrder_Id(Long orderId, Pageable pageable);

    OrderItem findByOrder_IdAndId(Long orderId, Long orderItemId);
}
