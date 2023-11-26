package project.mybookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mybookshop.dto.order.CreateOrderRequestDto;
import project.mybookshop.dto.order.OrderResponseDto;
import project.mybookshop.dto.order.UpdateOrderDto;
import project.mybookshop.dto.orderitem.OrderItemDto;
import project.mybookshop.model.User;
import project.mybookshop.service.OrderService;

@Tag(name = "Order management", description = "Endpoints for mapping order and orderItem")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Create a user order",
            description = "Create a user order")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public void createUserOrder(
            Authentication authentication,
            @RequestBody CreateOrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        orderService.createUserOrder(user.getEmail(), requestDto);
    }

    @Operation(summary = "Get all user orders",
            description = "Get a list of all user orders")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<OrderResponseDto> findAllUserOrders(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.findAllUserOrders(user.getEmail(), pageable);
    }

    @Operation(summary = "Update an order",
            description = "Update a status of order")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public void updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderDto requestDto) {
        orderService.updateOrderStatus(id, requestDto);
    }

    @Operation(summary = "Get all order items from order",
            description = "Get a list of all items from order")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> findAllItemsFromOrder(
            @PathVariable Long orderId,
            Pageable pageable) {
        return orderService.findAllItemsFromOrder(orderId, pageable);
    }

    @Operation(summary = "Get order item from order by id",
            description = "Get order item by id from order by id")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto findOrderItemByOrderId(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return orderService.findOrderItemByOrderId(orderId, itemId);
    }
}
