package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.ResourceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

public interface LanguageExcelService {
    ResourceDTO exportToExcelByLanguage (Long languageId);
    void importLanguageViaExcel(MultipartFile inputFile);

    void importLanguageViaExcel1(FileInputStream is); ///////////////////////

}
