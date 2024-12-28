package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Image;
import dev.kofe.kengine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByProduct(Product product);
}
