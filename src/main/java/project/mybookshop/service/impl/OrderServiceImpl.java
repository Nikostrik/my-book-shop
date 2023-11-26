package project.mybookshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mybookshop.dto.order.CreateOrderRequestDto;
import project.mybookshop.dto.order.OrderResponseDto;
import project.mybookshop.dto.order.UpdateOrderDto;
import project.mybookshop.dto.orderitem.OrderItemDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.mapper.OrderItemMapper;
import project.mybookshop.mapper.OrderMapper;
import project.mybookshop.model.CartItem;
import project.mybookshop.model.Order;
import project.mybookshop.model.OrderItem;
import project.mybookshop.model.ShoppingCart;
import project.mybookshop.model.User;
import project.mybookshop.repository.order.OrderRepository;
import project.mybookshop.repository.orderitem.OrderItemRepository;
import project.mybookshop.repository.shoppingcart.ShoppingCartRepository;
import project.mybookshop.service.OrderService;
import project.mybookshop.service.ShoppingCartService;
import project.mybookshop.service.UserService;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final Order order = new Order();

    @Override
    @Transactional
    public void createUserOrder(String email, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCartByUser = shoppingCartService.findShoppingCartByUser(email);
        User user = userService.findUserByEmail(email);
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setTotal(countTotal(shoppingCartByUser.getCartItems()));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());

        Order savedOrder = orderRepository.save(order);
        order.setOrderItems(getOrderItemsFromCartItems(savedOrder,
                shoppingCartByUser.getCartItems()));
        orderRepository.save(savedOrder);
        shoppingCartByUser.getCartItems().clear();
        shoppingCartRepository.save(shoppingCartByUser);
    }

    @Override
    public List<OrderResponseDto> findAllUserOrders(String email, Pageable pageable) {
        return orderRepository.findByUser_Email(email, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public void updateOrderStatus(Long id, UpdateOrderDto requestDto) {
        Order orderById = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + id));
        orderById.setStatus(requestDto.getStatus());
        orderRepository.save(orderById);
    }

    @Override
    public List<OrderItemDto> findAllItemsFromOrder(Long orderId, Pageable pageable) {
        return orderItemRepository.findAllByOrder_Id(orderId, pageable).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto findOrderItemByOrderId(Long orderId, Long itemId) {
        return orderItemMapper.toDto(
                orderItemRepository.findByOrder_IdAndId(orderId, itemId));
    }

    private BigDecimal countTotal(Set<CartItem> cartItems) {
        return BigDecimal.valueOf(cartItems.stream()
                .mapToDouble(ci ->
                        ci.getQuantity() * ci.getBook().getPrice().doubleValue())
                .sum());
    }

    private Set<OrderItem> getOrderItemsFromCartItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItemRepository.save(orderItem));
        }
        return orderItems;
    }
}
