package project.mybookshop.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemUpdateDto {
    @Min(0)
    private Long quantity;
}
