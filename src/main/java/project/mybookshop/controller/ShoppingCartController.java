package project.mybookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mybookshop.dto.cartitem.CartItemRequestDto;
import project.mybookshop.dto.cartitem.CartItemUpdateDto;
import project.mybookshop.dto.shoppingcart.ShoppingCartDto;
import project.mybookshop.model.User;
import project.mybookshop.service.ShoppingCartService;

@Tag(name = "Cart management", description = "Endpoints for mapping shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "get a user cart",
            description = "Retrieve user's shopping cart")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ShoppingCartDto findUserCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getCartWithItems(user.getEmail());
    }

    @Operation(summary = "add book to cart",
            description = "Add book to the shopping cart")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public void addItemToCart(
            Authentication authentication,
            @RequestBody @Valid CartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.addItemToCart(user.getEmail(), requestDto);
    }

    @Operation(summary = "Update a book quantity in cart",
            description = "Update quantity of a book in the shopping cart")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/cart-items/{cartItemId}")
    public void updateItemInCart(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestBody @Valid CartItemUpdateDto requestDto) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.updateItemInCart(
                        user.getEmail(),
                        cartItemId,
                        requestDto);
    }

    @Operation(summary = "Delete a book from cart",
            description = "Remove a book from the shopping cart")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    public void removeItemFromCart(
            Authentication authentication,
            @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.deleteItemFromCart(
                        user.getEmail(),
                        cartItemId);
    }
}
