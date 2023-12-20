package project.mybookshop.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemUpdateDto {
    @Min(0)
    private int quantity;
}
