package project.mybookshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mybookshop.dto.cartitem.CartItemRequestDto;
import project.mybookshop.dto.cartitem.CartItemUpdateDto;
import project.mybookshop.dto.shoppingcart.ShoppingCartDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.mapper.CartItemMapper;
import project.mybookshop.mapper.ShoppingCartMapper;
import project.mybookshop.model.CartItem;
import project.mybookshop.model.ShoppingCart;
import project.mybookshop.model.User;
import project.mybookshop.repository.cartitem.CartItemRepository;
import project.mybookshop.repository.shoppingcart.ShoppingCartRepository;
import project.mybookshop.service.ShoppingCartService;
import project.mybookshop.service.UserService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserService userService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public ShoppingCartDto getCartWithItems(String email) {
        return shoppingCartMapper.toDto(findShoppingCartByUser(email));
    }

    @Override
    @Transactional
    public void addItemToCart(String email, CartItemRequestDto requestDto) {
        CartItem cartItem = cartItemMapper.toEntity(requestDto);
        ShoppingCart shoppingCart = findShoppingCartByUser(email);
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void updateItemInCart(
            String email,
            Long cartItemId,
            CartItemUpdateDto requestDto) {
        CartItem cartItem = findCartItemById(cartItemId);
        CartItem updatedCartItem = cartItemMapper.updateEntity(requestDto);
        cartItem.setQuantity(updatedCartItem.getQuantity());
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void deleteItemFromCart(String email, Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public ShoppingCart createShoppingCartForUser(String email) {
        ShoppingCart shoppingCart = new ShoppingCart();
        User user = userService.findUserByEmail(email);
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public CartItem findCartItemById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find item by id: " + id));
    }

    @Override
    @Transactional
    public ShoppingCart findShoppingCartByUser(String email) {
        return shoppingCartRepository.findByUser_Email(email).orElseGet(
                () -> createShoppingCartForUser(email));
    }
}
