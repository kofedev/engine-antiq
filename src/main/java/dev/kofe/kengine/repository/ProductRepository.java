package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Category;
import dev.kofe.kengine.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory(Category category);
    List<Product> findAllByCategoryAndActiveIsTrue(Category category);

    @Query("SELECT p FROM Product p " +
            "WHERE (p.category = :category OR p.category.parent = :category) AND p.category.active = true AND p.active = true")
    Page<Product> findAllActiveProductsByCategoryAndSubcategories(
            @Param("category") Category category,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category.categoryId IN :categoryIds")
    Page<Product> findAllByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category.categoryId IN :categoryIds")
    List<Product> findAllByCategoryIds(@Param("categoryIds") List<Long> categoryIds);



    @Query("SELECT p FROM Product p WHERE p.active = true                AND p.currentQuantity > 0                          AND p.category.categoryId IN :categoryIds " +
            "AND (:minPrice IS NULL OR p.offerPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.offerPrice <= :maxPrice)")
    Page<Product> findAllByCategoryIdsAndPriceRange(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);



    @Query("SELECT p FROM Product p " +
            "WHERE p.active=true AND p.currentQuantity>0 " +

            "AND p.category.categoryId IN :categoryIds " +
            "AND (COALESCE(:searchTerm, '') = '' " +
            "      OR EXISTS (" +
            "            SELECT 1 FROM Descriptor d " +
            "            WHERE d.descriptorSet = p.titleSet " +

            "            AND LOWER(CAST(d.value AS text)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            "      )" +
            "      OR EXISTS (" +
            "            SELECT 1 FROM Descriptor d " +
            "            WHERE d.descriptorSet = p.briefSet " +

            "            AND LOWER(CAST(d.value AS text)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            "      )" +

            "      OR LOWER(CAST(p.keyWords AS text)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))  " +

            ") " +

            "AND (:minPrice IS NULL OR p.offerPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.offerPrice <= :maxPrice) " )
    Page<Product> findAllByCategoryIdsAndSearchAndPriceRange(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("searchTerm") String searchTerm,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);


    @Query("SELECT p.category FROM Product p WHERE p.productId = :productId")
    Optional<Category> findCategoryByProductId(@Param("productId") Long productId);


}
