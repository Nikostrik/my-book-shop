package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.category.CategoryDto;
import project.mybookshop.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
