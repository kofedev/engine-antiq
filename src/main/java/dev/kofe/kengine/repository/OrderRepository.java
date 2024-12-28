package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Order;
import dev.kofe.kengine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByActiveIsTrue();


    @Query("SELECT o FROM Order o JOIN o.orderCells oc JOIN oc.product p WHERE p = :product")
    List<Order> findByProduct(@Param("product") Product product);


    @Query("SELECT o FROM Order o JOIN o.orderCells oc JOIN oc.product p WHERE p.productId = :productId")
    List<Order> findByProductId(@Param("productId") Long productId);


}
