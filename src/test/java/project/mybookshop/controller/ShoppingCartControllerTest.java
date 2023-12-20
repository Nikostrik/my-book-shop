package project.mybookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.mybookshop.dto.cartitem.CartItemRequestDto;
import project.mybookshop.dto.cartitem.CartItemUpdateDto;
import project.mybookshop.dto.shoppingcart.ShoppingCartDto;
import project.mybookshop.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static final Long TEST_ID = 1L;
    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_PASSWORD = "12345678";
    private static User user;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        user = new User();
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
                            "database/user/add-user.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shopping_cart/add-shopping-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/add-test-book.sql")
            );
        }
    }

    @BeforeEach
    void setup() {
        user.setId(TEST_ID)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD);
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
                            "database/cartitem/remove-cart-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shopping_cart/remove-shopping-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/user/remove-user.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/remove-test-book.sql")
            );
        }
    }

    @Test
    @DisplayName("Find user's shopping cart")
    @WithMockUser(roles = "USER")
    void findUserCart_WithCorrectUser_ReturnShoppingCartDto() throws Exception {
        Authentication authentication = getAuthentication(user);

        MvcResult result = mockMvc.perform(get("/api/cart")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(actual.getUserId(), user.getId());
    }

    @Test
    @DisplayName("Add item to shopping cart")
    @Sql(scripts = {
        "classpath:database/shoppingcarts_cartitems/remove-cart-items-in-shopping-cart.sql",
        "classpath:database/cartitem/remove-test-cart-item.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = "USER")
    void addItemsToCart_WithExistingShoppingCart_Success() throws Exception {
        Authentication authentication = getAuthentication(user);
        CartItemRequestDto requestDto = new CartItemRequestDto()
                .setBookId(4L)
                .setQuantity(10);

        mockMvc.perform(post("/api/cart")
                        .with(authentication(authentication))
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("""
            Update item in shopping cart
            """)
    @Sql(scripts = {
            "classpath:database/cartitem/add-test-cart-item.sql",
            "classpath:database/shoppingcarts_cartitems/add-cart-item-to-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcarts_cartitems/remove-cart-items-in-shopping-cart.sql",
            "classpath:database/cartitem/remove-test-cart-item.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = "USER")
    void updateItemInCart_WithExistingItem_Success() throws Exception {
        Authentication authentication = getAuthentication(user);
        CartItemUpdateDto requestDto = new CartItemUpdateDto()
                .setQuantity(20);

        mockMvc.perform(put("/api/cart/cart-items/{cartItemId}", 1)
                        .with(authentication(authentication))
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("""
            Delete item from shopping cart
            """)
    @Sql(scripts = {
            "classpath:database/cartitem/add-test-cart-item.sql",
            "classpath:database/shoppingcarts_cartitems/add-cart-item-to-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(roles = "USER")
    void removeItemFromCart_WithExistingItem_Success() throws Exception {
        Authentication authentication = getAuthentication(user);

        mockMvc.perform(delete("/api/cart/cart-items/{cartItemId}", 1)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    private Authentication getAuthentication(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), authorities);
    }
}
