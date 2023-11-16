package project.mybookshop.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.mybookshop.dto.book.BookDtoWithoutCategoryIds;
import project.mybookshop.dto.category.CategoryDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.mapper.BookMapper;
import project.mybookshop.mapper.CategoryMapper;
import project.mybookshop.model.Category;
import project.mybookshop.repository.book.BookRepository;
import project.mybookshop.repository.category.CategoryRepository;
import project.mybookshop.service.CategoryService;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category categoryById = categoryRepository
                .findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find category by id: " + id)
                );
        return categoryMapper.toDto(categoryById);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return categoryMapper.toDto(categoryRepository
                .save(categoryMapper.toEntity(categoryDto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find category for updating by id: " + id);
        }
        Category category = categoryMapper.toEntity(categoryDto);
        category.setId(id);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long categoryId) {
        return bookRepository.findAllByCategoriesId(categoryId).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
