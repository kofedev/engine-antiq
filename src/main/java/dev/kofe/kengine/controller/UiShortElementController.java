package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.UiElementDTO;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;
import dev.kofe.kengine.service.UiShortElementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uishort")
@CrossOrigin("*")
public class UiShortElementController {

    private final UiShortElementService uiShortElementService;

    public UiShortElementController(UiShortElementService uiShortElementService) {
        this.uiShortElementService = uiShortElementService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void>
    registerNewUiShortElementAndExpandForEachLanguage( @RequestBody UiElementOneLanguageDTO request ) {
        uiShortElementService.createNewUiShortElementAndExpandForEachLanguage(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/key/{key}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public UiElementDTO getUiShortElementByKey (@PathVariable int key) {
        return uiShortElementService.getUiElementByKey(key);
    }

    @DeleteMapping("/{elementId}")
    @PreAuthorize("hasAuthority('Admin')")
    public void deleteUiElement(@PathVariable Long elementId) {
        this.uiShortElementService.deleteUiElement(elementId);
    }

    @GetMapping("/initial")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public List<UiElementOneLanguageDTO> getAllUiShortElementsForInitialLanguage() {
        return uiShortElementService.getAllUiShortElementsForInitialLanguage();
    }

    @GetMapping("/common/{languageId}")
    public String[] getUiShortsElementsByLanguage(@PathVariable Long languageId) {
        return uiShortElementService.getUiShortsElementsByLanguage(languageId);
    }

}
