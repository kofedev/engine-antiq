package dev.kofe.kengine.dto;

import lombok.Data;

@Data
public class LanguageDTO {
    private Long languageId;
    private String languageCode;
    private String languageName;
    private Boolean isActive;
    private Boolean isInitial;
    private Boolean byDefault;
}
