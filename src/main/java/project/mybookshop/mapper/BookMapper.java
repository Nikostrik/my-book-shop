package project.mybookshop.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.book.BookDto;
import project.mybookshop.dto.book.BookDtoWithoutCategoryIds;
import project.mybookshop.dto.book.CreateBookRequestDto;
import project.mybookshop.model.Book;
import project.mybookshop.model.Category;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @AfterMapping
    default void setCategoryIds(
            @MappingTarget BookDto bookDto,
            Book book) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }

    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto createBookRequestDto);

    @AfterMapping
    default void setCategories(
            @MappingTarget Book book,
            CreateBookRequestDto requestDto) {
        Set<Category> categories = requestDto.getCategories().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);
}
