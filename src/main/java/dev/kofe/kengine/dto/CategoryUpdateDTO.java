package dev.kofe.kengine.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryUpdateDTO {
    Long categoryId;
    Boolean active;
    List<DescriptorDTO> titleDescriptors;
    CategoryParentDTO parent;
}

