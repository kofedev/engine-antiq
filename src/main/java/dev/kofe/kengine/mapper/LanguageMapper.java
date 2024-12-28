package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.LanguageDTO;
import dev.kofe.kengine.model.Language;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageMapper {

    public LanguageDTO fromLanguage (Language language) {
        if (language == null) return null;
        LanguageDTO languageDTO = new LanguageDTO();
        BeanUtils.copyProperties(language, languageDTO);
        return languageDTO;
    }

    public List<LanguageDTO> fromLanguageList(List<Language> languageList) {
        if (languageList == null) return null;
        return languageList.stream()
                .map(this::fromLanguage)
                .collect(Collectors.toList());
    }

}
