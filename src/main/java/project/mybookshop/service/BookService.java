package project.mybookshop.service;

import java.util.List;
import project.mybookshop.dto.BookDto;
import project.mybookshop.dto.BookSearchParametersDto;
import project.mybookshop.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto params);
}
