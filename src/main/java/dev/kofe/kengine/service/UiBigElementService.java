package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.UiElementDTO;
import dev.kofe.kengine.dto.UiElementDTO2;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;

import java.util.List;

public interface UiBigElementService {
    void createNewUiBigElementAndExpandForEachLanguage(UiElementOneLanguageDTO uiElementToRegister);
    UiElementDTO getUiBigElementByKey(int key);
    void deleteUiBigElement(Long elementId);
    List<UiElementOneLanguageDTO> getAllUiBigElementsForInitialLanguage();
    UiElementDTO2 getTwoUiBigElementByKey(int keyOne, int keyTwo);
}
