package project.mybookshop.repository.orderitem;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import project.mybookshop.model.OrderItem;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Find all items by order id")
    @Sql(scripts = {
        "classpath:database/books/add-test-book.sql",
        "classpath:database/user/add-user.sql",
        "classpath:database/order/add-order.sql",
        "classpath:database/order_item/add-order-item.sql",
        "classpath:database/order-orderitem/add-orderitem-to-order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
        "classpath:database/order-orderitem/remove-all-orderitems-from-order.sql",
        "classpath:database/order_item/remove-all-order-items.sql",
        "classpath:database/order/remove-all-orders.sql",
        "classpath:database/books/remove-test-book.sql",
        "classpath:database/user/remove-user.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByOrderId_WithValidOderId_ReturnAllOrderItems() {
        List<OrderItem> actual = orderItemRepository
                .findAllByOrder_Id(1L, Pageable.ofSize(5));

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(4L, actual.get(0).getId());
    }

    @Test
    @DisplayName("Find item by order id and item id")
    @Sql(scripts = {
            "classpath:database/books/add-test-book.sql",
            "classpath:database/user/add-user.sql",
            "classpath:database/order/add-order.sql",
            "classpath:database/order_item/add-order-item.sql",
            "classpath:database/order-orderitem/add-orderitem-to-order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/order-orderitem/remove-all-orderitems-from-order.sql",
            "classpath:database/order_item/remove-all-order-items.sql",
            "classpath:database/order/remove-all-orders.sql",
            "classpath:database/books/remove-test-book.sql",
            "classpath:database/user/remove-user.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByOrderIdAndItemId_WithValidOrderIdAndItemId_ReturnOrderItem() {
        OrderItem actual = orderItemRepository
                .findByOrder_IdAndId(1L, 4L);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Test", actual.getBook().getTitle());
    }
}
