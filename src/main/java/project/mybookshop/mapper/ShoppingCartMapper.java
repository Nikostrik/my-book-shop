package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.shoppingcart.ShoppingCartDto;
import project.mybookshop.model.ShoppingCart;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

}
