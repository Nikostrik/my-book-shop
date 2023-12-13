package project.mybookshop.controller;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import project.mybookshop.dto.book.CreateBookRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @Test
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Test Title 1").setAuthor("Test Author 1")
                .setIsbn("1234").setPrice(new BigDecimal(10));
    }

}
