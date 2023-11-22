package project.mybookshop.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import project.mybookshop.dto.cartitem.CartItemResponseDto;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
