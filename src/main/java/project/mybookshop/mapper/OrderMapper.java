package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.order.OrderResponseDto;
import project.mybookshop.dto.order.UpdateOrderDto;
import project.mybookshop.model.Order;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    Order updateEntity(UpdateOrderDto request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderResponseDto toDto(Order order);
}
