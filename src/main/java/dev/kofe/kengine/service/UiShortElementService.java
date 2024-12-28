package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.UiElementDTO;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;

import java.util.List;

public interface UiShortElementService {

    void createNewUiShortElementAndExpandForEachLanguage(UiElementOneLanguageDTO uiElementToRegister);
    UiElementDTO getUiElementByKey(int key);
    void deleteUiElement(Long elementId);
    String[] getUiShortsElementsByLanguage(Long languageId);
    List<UiElementOneLanguageDTO> getAllUiShortElementsForInitialLanguage();

}
