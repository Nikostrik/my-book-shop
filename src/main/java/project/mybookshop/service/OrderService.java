package project.mybookshop.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.mybookshop.dto.order.CreateOrderRequestDto;
import project.mybookshop.dto.order.OrderResponseDto;
import project.mybookshop.dto.order.UpdateOrderDto;
import project.mybookshop.dto.orderitem.OrderItemDto;

public interface OrderService {
    void createUserOrder(String email, CreateOrderRequestDto requestDto);

    List<OrderResponseDto> findAllUserOrders(String email, Pageable pageable);

    void updateOrderStatus(Long id, UpdateOrderDto requestDto);

    List<OrderItemDto> findAllItemsFromOrder(Long orderId, Pageable pageable);

    OrderItemDto findOrderItemByOrderId(Long orderId, Long itemId);
}
