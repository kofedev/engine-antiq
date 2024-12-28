package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.StaffDTO;
import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.model.Staff;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {

    Staff loadStaffById(Long staffId);

    Page<StaffDTO> findStaffByName(String name, int page, int size);

    StaffDTO loadStaffByEmail(String email);

    void createInitialAdmin();

    StaffDTO createStaff(StaffDTO staffDTO);

    StaffDTO updateStaff(StaffDTO staffDTO);

    StaffDTO updateFirstAndLastNameStaff(StaffDTO staffDTO);

    List<StaffDTO> fetchStaff();

    void removeStaff(Long staffId);

    StaffDTO updateReceiverMailsStatus(StaffDTO staffDTO);


}
