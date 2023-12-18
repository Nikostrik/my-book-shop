package project.mybookshop.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import project.mybookshop.dto.book.BookDto;
import project.mybookshop.dto.book.BookSearchParametersDto;
import project.mybookshop.dto.book.CreateBookRequestDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.mapper.BookMapper;
import project.mybookshop.model.Book;
import project.mybookshop.repository.book.BookRepository;
import project.mybookshop.repository.book.BookSpecificationBuilder;
import project.mybookshop.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static final Long BOOK_ID = 1L;
    private static final Long INCORRECT_BOOK_ID = 100L;
    private static Book book;
    private static CreateBookRequestDto requestDto;
    private static BookDto expected;
    private static List<Book> books;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    static void beforeAll() {
        book = new Book();
        requestDto = new CreateBookRequestDto();
        expected = new BookDto();
        books = Arrays.asList(
                new Book().setId(BOOK_ID)
                        .setTitle("TestBook 1")
                        .setAuthor("TestAuthor 1")
                        .setPrice(BigDecimal.valueOf(20.00))
                        .setIsbn("1234"),
                new Book().setId(2L).setTitle("TestBook 2")
                        .setAuthor("TestAuthor 2")
                        .setPrice(BigDecimal.valueOf(25.00))
                        .setIsbn("5678"));
    }

    @BeforeEach
    void setup() {
        book.setId(BOOK_ID)
                .setTitle("TestTitle")
                .setAuthor("TestAuthor")
                .setIsbn("1234")
                .setPrice(BigDecimal.valueOf(20.00));
        expected.setId(BOOK_ID)
                .setTitle("TestTitle")
                .setAuthor("TestAuthor")
                .setIsbn("1234")
                .setPrice(BigDecimal.valueOf(20.00));
    }

    @Test
    @DisplayName("""
            Verify the book was saved correct
            """)
    public void createBook_WithValidCreateBookRequestDto_ReturnValidBookDto() {
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(new BookDto());

        BookDto actual = bookService.save(requestDto);

        assertNotNull(actual);

        verify(bookMapper, times(1)).toEntity(requestDto);
        verify(bookRepository, times(1)).save(Mockito.any(Book.class));
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify the all books were return from page
            """)
    public void getAll_WithPageable_ReturnAllBookDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        List<BookDto> expectedBookDtos = books.stream()
                .map(bookMapper::toDto)
                .toList();
        List<BookDto> actualBookDtos = bookService.findAll(pageable);

        assertEquals(expectedBookDtos, actualBookDtos);

        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify the correct book was returned when book exists
            """)
    public void findBook_WithValidBookId_ReturnValidBookDto() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        BookDto actual = bookService.findById(BOOK_ID);

        EqualsBuilder.reflectionEquals(expected, actual);

        verify(bookRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify the exception was return when id of book is incorrect
            """)
    public void findBook_WithNoExistingBookId_ShouldThrowException() {
        when(bookRepository.findById(INCORRECT_BOOK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(INCORRECT_BOOK_ID)
        );

        String expected = "Can't find book by id: " + INCORRECT_BOOK_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(INCORRECT_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify the book was updated when book exists
            """)
    public void updateBook_WithValidBookId_ReturnValidBookDto() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toEntity(requestDto)).thenReturn(new Book());
        when(bookMapper.toDto(any())).thenReturn(new BookDto());

        BookDto actual = bookService.updateById(BOOK_ID, requestDto);

        assertNotNull(actual);
    }

    @Test
    @DisplayName("""
            Verify the exception was return when book doesn't exist
            """)
    public void updateBook_WithNoExistingBookId_ShouldThrowException() {
        when(bookRepository.findById(INCORRECT_BOOK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateById(INCORRECT_BOOK_ID, requestDto)
        );

        String expected = "Can't find book for updating by id: " + INCORRECT_BOOK_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(INCORRECT_BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify the book was deleted by id
            """)
    public void deleteBook_WithValidBookId_Success() {
        doNothing().when(bookRepository).deleteById(BOOK_ID);

        assertAll(() -> bookService.deleteById(BOOK_ID));

        verify(bookRepository, times(1)).deleteById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify the books were searched by search parameters
            """)
    public void searchBooks_WithBookSearchParameterDto_ReturnBookDtos() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"TestAuthor 1"}, new String[]{"TestBook 2"},"1234");
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);

        when(bookSpecificationBuilder.build(params)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification)).thenReturn(books);

        List<BookDto> expectedBookDtos = books.stream()
                .map(bookMapper::toDto)
                .toList();
        List<BookDto> actualBookDtos = bookService.search(params);

        assertNotNull(actualBookDtos);
        assertEquals(expectedBookDtos.size(), actualBookDtos.size());
    }
}
