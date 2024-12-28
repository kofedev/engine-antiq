package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.OrderCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCellRepository extends JpaRepository<OrderCell, Long> {
}
