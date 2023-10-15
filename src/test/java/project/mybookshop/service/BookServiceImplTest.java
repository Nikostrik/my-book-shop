package project.mybookshop.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project.mybookshop.dto.BookDto;
import project.mybookshop.dto.CreateBookRequestDto;
import project.mybookshop.exceptions.DataProcessingException;
import project.mybookshop.mapper.BookMapper;

@Transactional
@SpringBootTest
public class BookServiceImplTest {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    private CreateBookRequestDto testBookRequestDto;

    @BeforeEach
    void setUp() {
        testBookRequestDto = new CreateBookRequestDto();
        testBookRequestDto.setTitle("Test book");
        testBookRequestDto.setAuthor("Test author");
        testBookRequestDto.setIsbn("1111");
        testBookRequestDto.setPrice(BigDecimal.valueOf(22));
        testBookRequestDto.setDescription("Test book!");
        testBookRequestDto.setCoverImage("test.jpg");
    }

    @Rollback
    @Test
    void save_checkOnRollback() {
        bookService.save(testBookRequestDto);

        Assertions.assertThrows(DataProcessingException.class, () ->
                bookService.findById(bookMapper
                        .toModel(testBookRequestDto).getId()));
    }

    @Commit
    @Test
    void save_correctBook_Ok() {
        testBookRequestDto.setIsbn("1234");
        BookDto savedBook = bookService.save(testBookRequestDto);

        assertNotNull(bookService.findById(savedBook.getId()));
    }
}
