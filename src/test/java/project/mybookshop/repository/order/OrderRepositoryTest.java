package project.mybookshop.repository.order;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import project.mybookshop.model.Order;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("")
    @Sql(scripts = {
            "classpath:database/user/add-user.sql",
            "classpath:database/order/add-order.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/order/remove-all-orders.sql",
            "classpath:database/user/remove-user.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserEmail_WithValidEmail_ReturnAllOrders() {
        List<Order> actual = orderRepository
                .findByUser_Email("test@gmail.com", Pageable.ofSize(5));

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(1L, actual.get(0).getId());
    }
}
