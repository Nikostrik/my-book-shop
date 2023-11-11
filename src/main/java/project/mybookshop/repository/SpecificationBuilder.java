package project.mybookshop.repository;

import org.springframework.data.jpa.domain.Specification;
import project.mybookshop.dto.book.BookSearchParametersDto;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto searchParameters);
}
