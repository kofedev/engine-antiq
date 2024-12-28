package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.StaffDTO;
import dev.kofe.kengine.mapper.StaffMapper;
import dev.kofe.kengine.model.Staff;
import dev.kofe.kengine.model.User;
import dev.kofe.kengine.repository.StaffRepository;
import dev.kofe.kengine.service.StaffService;
import dev.kofe.kengine.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static dev.kofe.kengine.constant.KEConstant.ADMIN_DEFAULT_LOGIN;
import static dev.kofe.kengine.constant.KEConstant.ADMIN_DEFAULT_PASSWORD;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final UserService userService;

    public StaffServiceImpl(StaffRepository staffRepository,
                            StaffMapper staffMapper,
                            UserService userService) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
        this.userService = userService;
    }

    @Override
    public Staff loadStaffById(Long staffId) {
        return staffRepository.findById(staffId).orElseThrow(() -> new EntityNotFoundException("Staff with ID" + staffId + " not found"));
    }

    @Override
    public Page<StaffDTO> findStaffByName(String name, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Staff> staffPage = staffRepository.findStaffByName(name, pageRequest);
        return new PageImpl<>(staffPage.getContent().stream().map(staff -> staffMapper.fromStaff(staff)).collect(Collectors.toList()), pageRequest, staffPage.getTotalElements());
    }

    @Override
    public StaffDTO loadStaffByEmail(String email) {
        Staff staff = staffRepository.findStaffByEmail(email);
        return staffMapper.fromStaff(staffRepository.findStaffByEmail(email));
    }

    @Override
    public void createInitialAdmin() {

        User user = userService.createUser(ADMIN_DEFAULT_LOGIN, ADMIN_DEFAULT_PASSWORD);
        user.setConfirmed(true);
        user.setInitial(true);
        userService.assignRoleToUser(user.getEmail(),"Admin");

        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setFirstName("");
        staffDTO.setLastName("");
        staffDTO.setIsReceiverMails(false);

        Staff staff = staffMapper.fromStaffDTO(staffDTO);
        staff.setUser(user);

        Staff savedStaff = staffRepository.save(staff);
    }

    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) {
        User user = userService.createUser(staffDTO.getUser().getEmail(), staffDTO.getUser().getPassword());
        userService.sendConfirmationEmail(user);
        //user.setConfirmed(staffDTO.getUser().getConfirmed());
        userService.assignRoleToUser(user.getEmail(),"Staff");
        Staff staff = staffMapper.fromStaffDTO(staffDTO);
        staff.setUser(user);
        staff.setIsReceiverMails(false);
        Staff savedStaff = staffRepository.save(staff);
        return staffMapper.fromStaff(savedStaff);
    }

    @Override
    public StaffDTO updateStaff(StaffDTO staffDTO) {
        Staff loadedStaff = loadStaffById(staffDTO.getStaffId());
        Staff staff = staffMapper.fromStaffDTO(staffDTO);
        staff.setUser(loadedStaff.getUser());
        Staff updatedStaff = staffRepository.save(staff);
        return staffMapper.fromStaff(updatedStaff);
    }

    @Override
    public List<StaffDTO> fetchStaff() {
        return staffRepository.findAll().stream().map(staff -> staffMapper.fromStaff(staff)).collect(Collectors.toList());
    }

    @Override
    public void removeStaff(Long staffId) {
        //@ToDo optional
        Staff staff = loadStaffById(staffId);
        staffRepository.deleteById(staffId);
    }

    @Override
    public StaffDTO updateFirstAndLastNameStaff(StaffDTO staffDTO) {
        Staff updatedStaff = staffRepository.findById(staffDTO.getStaffId()).orElse(null);
        if (updatedStaff != null) {
            updatedStaff.setFirstName(staffDTO.getFirstName());
            updatedStaff.setLastName(staffDTO.getLastName());
            staffRepository.save(updatedStaff);
            return staffMapper.fromStaff(updatedStaff);
        } else {
            // exception
            return null;
        }
    }

    @Override
    public StaffDTO updateReceiverMailsStatus(StaffDTO staffDTO) {
        Staff updatedStaff = staffRepository.findById(staffDTO.getStaffId()).orElse(null);
        if (updatedStaff != null) {
            if (staffDTO.getIsReceiverMails() != null) {
                updatedStaff.setIsReceiverMails(staffDTO.getIsReceiverMails());
            }
            staffRepository.save(updatedStaff);
            return staffMapper.fromStaff(updatedStaff);
        } else {
            // exception
            return null;
        }
    }

}
