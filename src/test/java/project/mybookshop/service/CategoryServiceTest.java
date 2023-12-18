package project.mybookshop.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
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
import project.mybookshop.dto.book.BookDtoWithoutCategoryIds;
import project.mybookshop.dto.category.CategoryDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.mapper.BookMapper;
import project.mybookshop.mapper.CategoryMapper;
import project.mybookshop.model.Book;
import project.mybookshop.model.Category;
import project.mybookshop.repository.book.BookRepository;
import project.mybookshop.repository.category.CategoryRepository;
import project.mybookshop.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final Long CATEGORY_ID = 1L;
    private static final Long INCORRECT_CATEGORY_ID = 100L;
    private static Category category;
    private static CategoryDto requestDto;
    private static CategoryDto expected;
    private static List<Category> categories;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void beforeAll() {
        category = new Category();
        requestDto = new CategoryDto();
        expected = new CategoryDto();
        categories = Arrays.asList(
                new Category()
                        .setId(CATEGORY_ID)
                        .setName("Fantasy"),
                new Category()
                        .setId(2L)
                        .setName("Fiction"));
    }

    @BeforeEach
    void setup() {
        category.setId(CATEGORY_ID)
                .setName("Fantasy");
        requestDto.setName("Fantasy");
        expected.setId(CATEGORY_ID)
                .setName("Fantasy");
    }

    @Test
    @DisplayName("""
            Verify the category was saved correct
            """)
    public void createCategory_WithValidCreateCategoryRequestDto_ReturnValidCategoryDto() {
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);

        Assertions.assertNotNull(actual);

        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(Mockito.any(Category.class));
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify the all categories were return from page
            """)
    public void getAll_WithPageable_ReturnAllCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        List<CategoryDto> expectedBookDtos = categories.stream()
                .map(categoryMapper::toDto)
                .toList();
        List<CategoryDto> actualBookDtos = categoryService.findAll(pageable);

        assertEquals(expectedBookDtos, actualBookDtos);

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Verify the correct category was returned when category exists
            """)
    public void findCategory_WithValidCategoryId_ReturnValidCategoryDto() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        CategoryDto actual = categoryService.getById(CATEGORY_ID);

        EqualsBuilder.reflectionEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Verify the exception was return when id of category is incorrect
            """)
    public void findCategory_WithNoExistingCategoryId_ShouldThrowException() {
        when(categoryRepository.findById(INCORRECT_CATEGORY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(INCORRECT_CATEGORY_ID)
        );

        String expected = "Can't find category by id: " + INCORRECT_CATEGORY_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(INCORRECT_CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Verify the category was updated when category exists
            """)
    public void updateCategory_WithValidCategoryId_ReturnValidCategoryDto() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toEntity(requestDto)).thenReturn(new Category());
        when(categoryMapper.toDto(any())).thenReturn(new CategoryDto());

        CategoryDto actual = categoryService.update(CATEGORY_ID, requestDto);

        assertNotNull(actual);
    }

    @Test
    @DisplayName("""
            Verify the exception was return when category doesn't exist
            """)
    public void updateCategory_WithNoExistingCategoryId_ShouldThrowException() {
        when(categoryRepository.findById(INCORRECT_CATEGORY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(INCORRECT_CATEGORY_ID, requestDto)
        );

        String expected = "Can't find category for updating by id: " + INCORRECT_CATEGORY_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(INCORRECT_CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Verify the category was deleted by id
            """)
    public void deleteCategory_WithValidCategoryId_Success() {
        doNothing().when(categoryRepository).deleteById(CATEGORY_ID);

        assertAll(() -> categoryService.deleteById(CATEGORY_ID));

        verify(categoryRepository, times(1)).deleteById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Verify to get correct list of BookDtoWithoutCategories by id of category
            """)
    public void findBookByCategoryId_WithValidCategoryId_ReturnListOfBookDto() {
        List<Book> books = Arrays.asList(
                new Book().setId(1L)
                        .setTitle("Test Title1")
                        .setAuthor("Test Author1")
                        .setIsbn("123"),
                new Book().setId(1L)
                        .setTitle("Test Title2")
                        .setAuthor("Test Author2")
                        .setIsbn("321")
        );

        when(bookRepository.findAllByCategoriesId(CATEGORY_ID)).thenReturn(books);

        List<BookDtoWithoutCategoryIds> expectedBookDtosWithoutCategoryId = books.stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
        List<BookDtoWithoutCategoryIds> actualBookDtosWithoutCategoryId =
                categoryService.findBooksByCategoryId(CATEGORY_ID);

        assertNotNull(actualBookDtosWithoutCategoryId);
        assertEquals(expectedBookDtosWithoutCategoryId.size(),
                actualBookDtosWithoutCategoryId.size());
        verify(bookRepository).findAllByCategoriesId(CATEGORY_ID);
        verifyNoMoreInteractions(bookRepository);
    }
}
