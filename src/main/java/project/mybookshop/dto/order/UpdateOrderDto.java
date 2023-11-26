package project.mybookshop.dto.order;

import lombok.Data;
import project.mybookshop.model.Order;

@Data
public class UpdateOrderDto {
    private Order.Status status;
}
