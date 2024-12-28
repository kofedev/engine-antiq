package dev.kofe.kengine.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryParentDTO {
    private Long categoryId;
    private Boolean active = true;
    private Boolean root = false;
    private List<DescriptorDTO> titleDescriptors;
}
