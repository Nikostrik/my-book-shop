package project.mybookshop.dto.book;

public record BookSearchParametersDto(
        String[] authors,
        String[] titles,
        String isbn) {
}
