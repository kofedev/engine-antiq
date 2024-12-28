package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.StaffDTO;
import dev.kofe.kengine.model.User;
import dev.kofe.kengine.service.StaffService;
import dev.kofe.kengine.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private StaffService staffService;
    private UserService userService;

    public StaffController(StaffService staffService, UserService userService) {
        this.staffService = staffService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Admin')")
    public Page<StaffDTO> searchStaff(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "size", defaultValue = "5") int size) {
        return staffService.findStaffByName(keyword, page, size);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('Admin')")
    public List<StaffDTO> findAllStaff() {
        return staffService.fetchStaff();
    }

    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasAuthority('Admin')")
    public void deleteStaff(@PathVariable Long staffId) {
        staffService.removeStaff(staffId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public StaffDTO saveStaff(@RequestBody StaffDTO staffDTO) {
        User user = userService.loadUserByEmail(staffDTO.getUser().getEmail());
        if (user != null) throw new RuntimeException("Email Already Exist");
        return staffService.createStaff(staffDTO);
    }

    @GetMapping("/find")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public StaffDTO loadStaffByEmail(@RequestParam(name = "email", defaultValue = "") String email) {
        return staffService.loadStaffByEmail(email);
    }

    @PutMapping("/name")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public StaffDTO updateFirstAndLastNameStaff(@RequestBody StaffDTO staffDTO) {
        return staffService.updateFirstAndLastNameStaff(staffDTO);
    }

    @PutMapping("/receiver")
    @PreAuthorize("hasAuthority('Admin')")
    public StaffDTO updateStaffReceiverMailsStatus (@RequestBody StaffDTO staffDTO) {
        return staffService.updateReceiverMailsStatus(staffDTO);
    }

}
