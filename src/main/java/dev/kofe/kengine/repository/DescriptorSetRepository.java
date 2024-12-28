package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.DescriptorSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptorSetRepository extends JpaRepository<DescriptorSet, Long> {
}
