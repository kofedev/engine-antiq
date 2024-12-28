package dev.kofe.kengine.dto;

import lombok.Data;
import java.util.List;

@Data
public class UiElementDTO {
    private Long uiElementId;
    private Integer key;
    private Boolean isBig = false;
    private List<DescriptorDTO> descriptors;
    private Long setId;
    private Boolean isLast = false;
}
