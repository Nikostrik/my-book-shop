package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.orderitem.OrderItemDto;
import project.mybookshop.model.OrderItem;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);
}
