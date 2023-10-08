package project.mybookshop.repository;

import java.util.List;
import project.mybookshop.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
