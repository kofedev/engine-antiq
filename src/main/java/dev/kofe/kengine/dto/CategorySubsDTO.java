package dev.kofe.kengine.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategorySubsDTO {
    private Long categoryId;
    private Boolean active = true;
    private List<DescriptorDTO> titleDescriptors;
}
