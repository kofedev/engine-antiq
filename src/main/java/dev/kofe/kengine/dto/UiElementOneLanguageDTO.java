package dev.kofe.kengine.dto;

import lombok.Data;

import java.util.List;

@Data
public class UiElementOneLanguageDTO {
    private Long uiElementId;
    private Integer key;
    private Boolean isBig = false;
    private String value;
//    private List<DescriptorDTO> descriptors;

}
