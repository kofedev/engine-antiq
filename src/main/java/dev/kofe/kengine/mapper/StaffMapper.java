package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.StaffDTO;
import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.model.Staff;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class StaffMapper {

    UserMapper userMapper;
    public StaffMapper (UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public StaffDTO fromStaff(Staff staff) {
        if (staff == null) return null;
        StaffDTO staffDTO = new StaffDTO();
        BeanUtils.copyProperties(staff, staffDTO);
        UserDTO userDTO = userMapper.fromUser(staff.getUser());
        staffDTO.setUser(userDTO);
        return staffDTO;
    }

    public Staff fromStaffDTO(StaffDTO staffDTO) {
        if (staffDTO == null) return null;
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffDTO, staff);
        return staff;
    }
}
