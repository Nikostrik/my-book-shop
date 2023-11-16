package project.mybookshop.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mybookshop.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
