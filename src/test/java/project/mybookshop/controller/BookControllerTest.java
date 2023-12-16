package project.mybookshop.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.mybookshop.dto.book.BookDto;
import project.mybookshop.dto.book.BookSearchParametersDto;
import project.mybookshop.dto.book.CreateBookRequestDto;
import project.mybookshop.model.Book;
import project.mybookshop.model.Category;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final Long BOOK_ID = 1L;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/add-three-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories/add-three-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books_categories/add-books-to-categories.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books_categories/delete-books-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/remove-all-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories/remove-all-categories.sql")
            );
        }
    }

    @Test
    @Sql(scripts = {
            "classpath:database/books_categories/delete-books-categories-table.sql",
            "classpath:database/books/remove-harry-potter-book.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_ValidRequestDto_Success() throws Exception {
        Category category = new Category().setId(1L).setName("fantasy");
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Harry Potter").setAuthor("Joanne Rowling")
                .setIsbn("4321").setPrice(new BigDecimal(10)).setCategories(Set.of(1L));

        Book expected = new Book()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setCategories(Set.of(category));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/api/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Find all books")
    @WithMockUser(roles = "USER")
    void findAll_GivenBooks_ShouldReturnAllBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class);

        Assertions.assertEquals(3, actual.length);
        List<Long> bookIds = Arrays.stream(actual)
                .map(BookDto::getId)
                .toList();
        assertThat(bookIds).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Get book by id")
    @WithMockUser(roles = "USER")
    void getById_WithValidBookId_ShouldReturnBookDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/{id}", BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(BOOK_ID, actual.getId());
    }

    @Test
    @DisplayName("Update book by id")
    @WithMockUser(roles = "ADMIN")
    void updateById_WithValidId_ShouldReturnUpdatedBookDto() throws Exception {
        Category category = new Category().setId(1L).setName("fantasy");
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Updated Book").setAuthor("Updated Author")
                .setIsbn("1234").setPrice(new BigDecimal(22)).setCategories(Set.of(1L));
        Book expected = new Book()
                .setId(BOOK_ID)
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setCategories(Set.of(category));

        MvcResult result = mockMvc.perform(put("/api/books/{id}", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);

    }

    @Test
    @DisplayName("Delete book by id")
    @Sql(scripts = {
            "classpath:database/books/add-test-book.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(roles = "ADMIN")
    void deleteById_WithValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/books/{id}", 4)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Search a books by params")
    @WithMockUser(roles = "USER")
    void searchBooks_WithBookSearchParameterDto_ReturnBookDtos() throws Exception {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"Test Author 2"}, new String[]{"Test Book 3"},"1234");
        MvcResult result = mockMvc.perform(get("/api/books/search")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class);
        Assertions.assertEquals(3, actual.length);
        Assertions.assertTrue(Arrays.stream(actual)
                .anyMatch(bookDto -> bookDto.getIsbn().equals("1234")));
        Assertions.assertTrue(Arrays.stream(actual)
                .anyMatch(bookDto -> bookDto.getAuthor().equals("Test Author 2")));
        Assertions.assertTrue(Arrays.stream(actual)
                .anyMatch(bookDto -> bookDto.getTitle().equals("Test Book 3")));
    }
}
