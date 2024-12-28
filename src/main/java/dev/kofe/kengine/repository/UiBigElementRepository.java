package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.UiBigElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UiBigElementRepository extends JpaRepository<UiBigElement, Long> {

    @Query("SELECT u FROM UiBigElement u WHERE u.key = (SELECT MAX(u2.key) FROM UiBigElement u2)")
    UiBigElement findUiBigElementWithMaxKey();

    UiBigElement findByKey(int key);

}
