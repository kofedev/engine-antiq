package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.LanguageDTO;
import dev.kofe.kengine.dto.ResourceDTO;
import dev.kofe.kengine.model.Descriptor;
import dev.kofe.kengine.model.Language;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LanguageService {

    void createInitialLanguage();
    List<LanguageDTO> getAllLanguages();
    List<LanguageDTO> getAllActiveLanguages();
    LanguageDTO getLanguageById(Long languageId);
    LanguageDTO addNewLanguageAndExpandDescriptors(LanguageDTO languageDTO);
    LanguageDTO updateLanguage(LanguageDTO languageDTO, Long languageId);
    Boolean deleteLanguage(Long languageId);
    LanguageDTO getInitialLanguage();
    LanguageDTO setDefaultLanguage(Long languageId);

}
