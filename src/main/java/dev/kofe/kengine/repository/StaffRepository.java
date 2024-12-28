package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    @Query(value = "select i from Staff as i where i.firstName like %:name% or i.lastName like %:name%")
    Page<Staff> findStaffByName(@Param("name") String name, PageRequest pageRequest);

    @Query(value = "select i from Staff as i where i.user.email=:email")
    Staff findStaffByEmail(@Param("email") String email);

    List<Staff> findAllByIsReceiverMailsTrue();

}
