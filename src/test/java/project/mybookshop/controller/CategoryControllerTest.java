package project.mybookshop.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
import project.mybookshop.dto.book.BookDtoWithoutCategoryIds;
import project.mybookshop.dto.category.CategoryDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
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
                    new ClassPathResource("database/categories/add-three-categories.sql")
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
                    new ClassPathResource("database/categories/remove-all-categories.sql")
            );
        }
    }

    @Test
    @Sql(scripts =
            "classpath:database/categories/remove-test-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new category")
    @WithMockUser(roles = "ADMIN")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryDto categoryDto = new CategoryDto().setName("test");

        String jsonRequest = objectMapper.writeValueAsString(categoryDto);

        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(categoryDto.getName(), actual.getName());
    }

    @Test
    @DisplayName("Find all categories")
    @WithMockUser(roles = "USER")
    void findAll_GivenCategory_ShouldReturnAllCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto[].class);
        List<Long> categoryIds = Arrays.stream(actual)
                .map(CategoryDto::getId)
                .toList();

        Assertions.assertEquals(3, actual.length);
        assertThat(categoryIds).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Get category by id")
    @WithMockUser(roles = "USER")
    void getById_WithValidCategoryId_ShouldReturnCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.getId());
    }

    @Test
    @DisplayName("Update category by id")
    @WithMockUser(roles = "ADMIN")
    void updateById_WithValidCategoryId_ShouldReturnUpdatedCategoryDto() throws Exception {
        CategoryDto categoryDto = new CategoryDto().setName("Updated Category");

        MvcResult result = mockMvc.perform(put("/api/categories/{id}", 1)
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(categoryDto, actual, "id");
    }

    @Test
    @DisplayName("Delete category by id")
    @Sql(scripts = {
            "classpath:database/categories/add-test-category.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(roles = "ADMIN")
    void deleteById_WithValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/categories/{id}", 4)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Get a book by category id")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books_categories/add-books-to-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books_categories/delete-books-categories-table.sql",
            "classpath:database/books/remove-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = "USER")
    void findBookByCategoryId_WithValidCategoryId_ReturnListOfBookDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/{id}/books", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDtoWithoutCategoryIds[].class);
        List<BookDtoWithoutCategoryIds> bookDtoWithoutCategoryIds = Arrays.stream(actual).toList();

        Assertions.assertEquals(2, actual.length);
        Assertions.assertTrue(Arrays.stream(actual)
                .anyMatch(bookDto -> bookDto.getIsbn().equals("1234")));
        Assertions.assertTrue(Arrays.stream(actual)
                .anyMatch(bookDto -> bookDto.getIsbn().equals("5678")));
    }
}
