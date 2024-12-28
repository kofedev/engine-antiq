package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.DescriptorSetDTO;
import dev.kofe.kengine.dto.LanguageDTO;
import dev.kofe.kengine.service.DescriptorSetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/descrset")
public class DescriptorSetController {

    private final DescriptorSetService descriptorSetService;

    public DescriptorSetController (DescriptorSetService descriptorSetService) {
        this.descriptorSetService = descriptorSetService;
    }

    // get by id
    @GetMapping("/{descrSetId}")
    public DescriptorSetDTO getDescriptorSetById (@PathVariable Long descrSetId) {
        return descriptorSetService.getDescriptorSetById(descrSetId);
    }

    // update descriptors (set is used for transferring the descriptors to update)
    @PutMapping("/update-descr-only")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<Void> updateDescriptorsOnly (@RequestBody DescriptorSetDTO descriptorSetDTO) {
        descriptorSetService.updateValuesOnlyOfDescriptorsOnly(descriptorSetDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
