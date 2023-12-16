package project.mybookshop.repository.book;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import project.mybookshop.model.Book;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all books by existed category
            """)
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
            "classpath:database/categories/add-three-categories.sql",
            "classpath:database/books_categories/add-books-to-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books_categories/delete-books-categories-table.sql",
            "classpath:database/categories/remove-all-categories.sql",
            "classpath:database/books/remove-all-books.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllBooksByCategory_ExistedCategoryIds_ReturnListOfBooks() {
        List<Book> actual = bookRepository.findAllByCategoriesId(1L);

        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("Test Book 1", actual.get(0).getTitle());
        Assertions.assertEquals("Test Author 2", actual.get(1).getAuthor());
    }
}
