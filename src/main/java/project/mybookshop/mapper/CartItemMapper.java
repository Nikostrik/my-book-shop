package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.cartitem.CartItemRequestDto;
import project.mybookshop.dto.cartitem.CartItemResponseDto;
import project.mybookshop.dto.cartitem.CartItemUpdateDto;
import project.mybookshop.model.CartItem;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(target = "book",
            source = "bookId",
            qualifiedByName = "bookFromId")
    CartItem toEntity(CartItemRequestDto requestDto);

    CartItem updateEntity(CartItemUpdateDto requestDto);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem cartItem);

}
