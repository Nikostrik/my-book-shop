package project.mybookshop.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateBookRequestDto {
    @NotNull
    @NotEmpty
    private String title;
    @NotNull
    @NotEmpty
    private String author;
    @NotNull
    @NotEmpty
    private String isbn;
    private String description;
    private String coverImage;
    @NotNull
    @Min(0)
    private BigDecimal price;
    private Set<Long> categories;
}
