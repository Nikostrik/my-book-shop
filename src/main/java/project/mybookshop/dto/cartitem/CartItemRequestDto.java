package project.mybookshop.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemRequestDto {
    private Long bookId;
    @Min(0)
    private Long quantity;
}
