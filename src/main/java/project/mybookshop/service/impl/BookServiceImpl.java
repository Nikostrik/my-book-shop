package project.mybookshop.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.mybookshop.dto.BookDto;
import project.mybookshop.dto.CreateBookRequestDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.mapper.BookMapper;
import project.mybookshop.model.Book;
import project.mybookshop.repository.BookRepository;
import project.mybookshop.service.BookService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        return bookMapper.toDto(bookRepository
                .save(bookMapper.toModel(requestDto)));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

}
