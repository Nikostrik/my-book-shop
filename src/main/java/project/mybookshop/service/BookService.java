package project.mybookshop.service;

import java.util.List;
import project.mybookshop.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
