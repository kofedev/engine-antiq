package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByActiveIsTrue();
    Category findByRootIsTrue();


    @Query(value = "WITH RECURSIVE subcategories AS ( " +
            "  SELECT * FROM categories WHERE category_id = :categoryId AND active = true " +
            "  UNION " +
            "  SELECT c.* FROM categories c " +
            "  JOIN subcategories s ON c.parent_category_id = s.category_id AND c.active = true " +
            ") " +
            "SELECT * FROM subcategories", nativeQuery = true)
    List<Category> findAllSubcategoriesRecursive(@Param("categoryId") Long categoryId);

    @Query(value = "WITH RECURSIVE subcategories AS ( " +
            "  SELECT * FROM categories WHERE category_id = :categoryId AND active = true " +
            "  UNION " +
            "  SELECT c.* FROM categories c " +
            "  JOIN subcategories s ON c.parent_category_id = s.category_id AND c.active = true " +
            ") " +
            "SELECT category_id FROM subcategories", nativeQuery = true)
    List<Long> findSubcategoryIdsRecursive(@Param("categoryId") Long categoryId);

}
