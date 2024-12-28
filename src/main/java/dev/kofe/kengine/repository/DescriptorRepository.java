package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Descriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptorRepository extends JpaRepository<Descriptor, Long> {
}
