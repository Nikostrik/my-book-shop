package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.BookDto;
import project.mybookshop.dto.CreateBookRequestDto;
import project.mybookshop.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);
}
