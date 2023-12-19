package project.mybookshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import project.mybookshop.dto.cartitem.CartItemRequestDto;
import project.mybookshop.dto.cartitem.CartItemResponseDto;
import project.mybookshop.dto.cartitem.CartItemUpdateDto;
import project.mybookshop.dto.shoppingcart.ShoppingCartDto;
import project.mybookshop.mapper.CartItemMapper;
import project.mybookshop.mapper.ShoppingCartMapper;
import project.mybookshop.model.Book;
import project.mybookshop.model.CartItem;
import project.mybookshop.model.ShoppingCart;
import project.mybookshop.model.User;
import project.mybookshop.repository.cartitem.CartItemRepository;
import project.mybookshop.repository.shoppingcart.ShoppingCartRepository;
import project.mybookshop.service.impl.ShoppingCartServiceImpl;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    private static final Long TEST_ID = 1L;
    private static final int TEST_QUANTITY = 10;
    private static final String TEST_EMAIL = "test_email@gmail.com";
    private static ShoppingCart shoppingCart;
    private static ShoppingCartDto shoppingCartDto;
    private static CartItem cartItem;
    private static Set<CartItem> cartItems;
    private static Set<CartItemResponseDto> cartItemResponseDtos;
    private static CartItemRequestDto cartItemRequestDto;
    private static CartItemResponseDto cartItemResponseDto;
    private static CartItemUpdateDto cartItemUpdateDto;
    private static User user;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private UserService userService;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeAll
    static void beforeAll() {
        shoppingCart = new ShoppingCart();
        shoppingCartDto = new ShoppingCartDto();
        cartItem = new CartItem();
        cartItems = new HashSet<>();
        cartItemRequestDto = new CartItemRequestDto();
        cartItemResponseDto = new CartItemResponseDto();
        cartItemUpdateDto = new CartItemUpdateDto();
        cartItemResponseDtos = new HashSet<>();
        user = new User();
    }

    @BeforeEach
    void setup() {
        user.setId(TEST_ID)
                .setEmail(TEST_EMAIL);
        shoppingCart.setId(TEST_ID)
                .setCartItems(cartItems)
                .setUser(user);
        cartItem.setId(TEST_ID)
                .setBook(new Book()
                        .setId(TEST_ID)
                        .setTitle("Test"))
                .setQuantity(TEST_QUANTITY);
        cartItems.add(cartItem);
        shoppingCart.setCartItems(cartItems);
        cartItemRequestDto
                .setBookId(TEST_ID)
                .setQuantity(TEST_QUANTITY);
        cartItemResponseDto
                .setId(TEST_ID)
                .setBookId(TEST_ID)
                .setBookTitle("Test")
                .setQuantity(TEST_QUANTITY);
        shoppingCartDto.setId(TEST_ID)
                .setUserId(TEST_ID)
                .setCartItems(cartItemResponseDtos);
        cartItemUpdateDto.setQuantity(TEST_QUANTITY);
    }

    @AfterEach
    void tearDown() {
        cartItems.clear();
    }

    @Test
    @DisplayName("""
            Verify get cart item by email of user
            """)
    void getCartWithItem_WithValidEmail_ReturnShoppingCartDto() {
        when(shoppingCartRepository.findByUser_Email(TEST_EMAIL))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actual = shoppingCartService.getCartWithItems(TEST_EMAIL);

        EqualsBuilder.reflectionEquals(shoppingCartDto, actual);

        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Verify to add correct item to shopping cart
            """)
    void addItemToCart_WithValidData_Success() {
        when(shoppingCartRepository.findByUser_Email(TEST_EMAIL))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemMapper.toEntity(cartItemRequestDto)).thenReturn(cartItem);
        when(cartItemRepository.save(any())).thenReturn(cartItem);

        Assertions.assertAll(
                () -> shoppingCartService.addItemToCart(TEST_EMAIL, cartItemRequestDto));

        verify(shoppingCartRepository, times(1)).findByUser_Email(TEST_EMAIL);
        verify(cartItemMapper, times(1)).toEntity(cartItemRequestDto);
        verify(cartItemRepository, times(1)).save(any());
        verifyNoMoreInteractions(cartItemMapper);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("""
            Verify to update existing items in shopping cart
            """)
    void updateItemInCart_WithValidCartItem_Success() {
        when(cartItemRepository.findById(TEST_ID)).thenReturn(Optional.of(cartItem));
        when(cartItemMapper.updateEntity(any())).thenReturn(cartItem);
        when(cartItemRepository.save(any())).thenReturn(cartItem);

        Assertions.assertAll(
                () -> shoppingCartService.updateItemInCart(TEST_EMAIL, TEST_ID, cartItemUpdateDto));

        verify(cartItemRepository, times(1)).findById(TEST_ID);
        verify(cartItemRepository, times(1)).save(any());
        verify(cartItemMapper, times(1)).updateEntity(any());
        verifyNoMoreInteractions(shoppingCartRepository);
        verifyNoMoreInteractions(cartItemMapper);
    }

    @Test
    @DisplayName("""
            Verify to delete item from shopping cart
            """)
    void deleteItemFromCart_WithExistingCarItemId_Success() {
        doNothing().when(cartItemRepository).deleteById(TEST_ID);

        Assertions.assertAll(
                () -> shoppingCartService.deleteItemFromCart(TEST_EMAIL, TEST_ID));

        verify(cartItemRepository, times(1)).deleteById(TEST_ID);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("""
            Verify to create shopping cart for user
            """)
    void createShoppingCartForUser_WithCorrectEmail_ReturnShoppingCart() {
        when(userService.findUserByEmail(TEST_EMAIL)).thenReturn(user);
        when(shoppingCartRepository.save(any())).thenReturn(shoppingCart);

        ShoppingCart actual = shoppingCartService.createShoppingCartForUser(TEST_EMAIL);

        Assertions.assertNotNull(actual);
        assertEquals(user,actual.getUser());

        verify(shoppingCartRepository, times(1)).save(any());
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Verify to find Shopping cart by user
            """)
    void findShoppingCartByUser_WithExistingEmail_ReturnShoppingCart() {
        when(shoppingCartRepository.findByUser_Email(TEST_EMAIL))
                .thenReturn(Optional.ofNullable(shoppingCart));

        ShoppingCart actual = shoppingCartService.findShoppingCartByUser(TEST_EMAIL);

        EqualsBuilder.reflectionEquals(shoppingCart, actual);

        verify(shoppingCartRepository, times(1)).findByUser_Email(TEST_EMAIL);
        verifyNoMoreInteractions(shoppingCartRepository);
    }
}
