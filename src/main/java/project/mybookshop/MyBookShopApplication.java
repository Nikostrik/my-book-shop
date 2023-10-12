package project.mybookshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.mybookshop.service.BookService;

@SpringBootApplication
public class MyBookShopApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(MyBookShopApplication.class, args);
    }

}
