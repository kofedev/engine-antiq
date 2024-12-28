package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.UiElementDTO;
import dev.kofe.kengine.dto.UiElementDTO2;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;
import dev.kofe.kengine.service.UiBigElementService;
import dev.kofe.kengine.service.UiShortElementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uibig")
public class UiBigElementController {

    private final UiBigElementService uiBigElementService;

    public UiBigElementController(UiBigElementService uiBigElementService) {
        this.uiBigElementService = uiBigElementService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void>
    registerNewUiBigElementAndExpandForEachLanguage( @RequestBody UiElementOneLanguageDTO request ) {
        uiBigElementService.createNewUiBigElementAndExpandForEachLanguage(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/key/{key}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public UiElementDTO getUiBigElementByKey (@PathVariable int key) {
        return uiBigElementService.getUiBigElementByKey(key);
    }

    @GetMapping("/initial")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public List<UiElementOneLanguageDTO> getAllUiBigElementsForInitialLanguage() {
        return uiBigElementService.getAllUiBigElementsForInitialLanguage();
    }

    // common: get by key
    @GetMapping("/common/{key}")
    public UiElementDTO commonGetBigElementByKey(@PathVariable int key) {
        return uiBigElementService.getUiBigElementByKey(key);
    }

    // common: git TWO elements by key
    @GetMapping("/common/two/{keyOne}/{keyTwo}")
    public UiElementDTO2 commonGetTwoBigElementByKey(@PathVariable int keyOne, @PathVariable int keyTwo) {
        return uiBigElementService.getTwoUiBigElementByKey(keyOne, keyTwo);
    }


}
