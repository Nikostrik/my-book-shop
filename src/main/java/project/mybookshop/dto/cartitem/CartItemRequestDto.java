package project.mybookshop.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemRequestDto {
    private Long bookId;
    @Min(0)
    private int quantity;
}
