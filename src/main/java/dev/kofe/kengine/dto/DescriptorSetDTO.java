package dev.kofe.kengine.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DescriptorSetDTO {
    private Long descriptorSetId;
    private List<DescriptorDTO> descriptors = new ArrayList<>();
}
