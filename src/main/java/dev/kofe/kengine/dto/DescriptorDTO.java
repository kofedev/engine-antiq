package dev.kofe.kengine.dto;

import lombok.Data;

@Data
public class DescriptorDTO {
    private Long descriptorId;
    private Boolean isBig = false;
    private Boolean isSearchable = true;
    private String value = "";
    private LanguageDTO language;
}
