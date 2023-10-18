package project.mybookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
