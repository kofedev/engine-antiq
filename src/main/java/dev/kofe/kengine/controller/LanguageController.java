package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.LanguageDTO;
import dev.kofe.kengine.dto.ResourceDTO;
import dev.kofe.kengine.service.LanguageExcelService;
import dev.kofe.kengine.service.LanguageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@RestController
@RequestMapping("/language")
public class LanguageController {

    private final LanguageService languageService;
    private final LanguageExcelService languageExcelService;

    public LanguageController (LanguageService languageService, LanguageExcelService languageExcelService) {
        this.languageService = languageService;
        this.languageExcelService = languageExcelService;
    }

    // get all
    @GetMapping("")
    public List<LanguageDTO> getAllLanguage() {
        return languageService.getAllLanguages();
    }

    // get all ACTIVE
    @GetMapping("/common/active")
    public List<LanguageDTO> getAllActiveLanguage() {
        return languageService.getAllActiveLanguages();
    }

    // get by id //@ToDo ---------- ??? common???
    @GetMapping("/{languageId}")
    public LanguageDTO getLanguageById (@PathVariable Long languageId) {
        return languageService.getLanguageById(languageId);
    }

    // get initial language
    @GetMapping("/initial")
    public LanguageDTO getInitialLanguage() {
        return languageService.getInitialLanguage();
    }

    // new
    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public LanguageDTO saveNewLanguageAndExpandDescriptors(@RequestBody LanguageDTO languageDTO) {
        return languageService.addNewLanguageAndExpandDescriptors(languageDTO);
    }

    // update
    @PutMapping("/{languageId}")
    @PreAuthorize("hasAuthority('Admin')")
    public LanguageDTO updateLanguage(@RequestBody LanguageDTO languageDTO, @PathVariable Long languageId) {
        return languageService.updateLanguage(languageDTO, languageId);
    }

    // delete
    @DeleteMapping("/{languageId}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> deleteLanguage(@PathVariable Long languageId) {
        return new ResponseEntity<>(languageService.deleteLanguage(languageId), HttpStatus.OK);
    }

    // set default language
    @PutMapping("/default/{languageId}")
    @PreAuthorize("hasAuthority('Admin')")
    public LanguageDTO setDefaultLanguage(@RequestBody LanguageDTO languageDTO, @PathVariable Long languageId) {
        return languageService.setDefaultLanguage(languageId);
    }

    // export language (UiShort- and UiBigElements) to Excel file
    @GetMapping("/export/excel/{languageId}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Resource> exportLanguageToExcelAndGetFile(@PathVariable Long languageId) throws UnsupportedEncodingException {
        ResourceDTO resourceDTO = languageExcelService.exportToExcelByLanguage(languageId);
        String fileName = "uiElements.xlsx";
        fileName = URLDecoder.decode(fileName, "ISO8859_1");
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .contentType(resourceDTO.getMediaType())
                .headers(httpHeaders)
                .body(resourceDTO.getResource());
    }

    // import language (Excel file)
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void> importLanguageViaExcel (@RequestParam("file") MultipartFile inputFile) throws IOException {
        if (inputFile == null) return null;
        languageExcelService.importLanguageViaExcel(inputFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
