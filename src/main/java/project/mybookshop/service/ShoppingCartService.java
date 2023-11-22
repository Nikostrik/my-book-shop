package project.mybookshop.service;

import project.mybookshop.dto.cartitem.CartItemRequestDto;
import project.mybookshop.dto.cartitem.CartItemUpdateDto;
import project.mybookshop.dto.shoppingcart.ShoppingCartDto;
import project.mybookshop.model.CartItem;
import project.mybookshop.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCartDto getCartWithItems(String email);

    void addItemToCart(String email, CartItemRequestDto requestDto);

    void updateItemInCart(String email, Long cartItemId, CartItemUpdateDto requestDto);

    void deleteItemFromCart(String email, Long cartItemId);

    ShoppingCart createShoppingCartForUser(String email);

    CartItem findCartItemById(Long id);

    ShoppingCart findShoppingCartByUser(String email);
}
