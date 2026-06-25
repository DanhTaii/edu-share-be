package vn.edu.nlu.edushare.edu_share.api.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nlu.edushare.edu_share.api.category.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findFirstByNameIgnoreCase(String name);
}