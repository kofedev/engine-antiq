package dev.kofe.kengine.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private Long categoryId;
    private Boolean active;
    private Boolean root;
    private CategoryParentDTO parent;
    private List<DescriptorDTO> titleDescriptors;
    private List<CategorySubsDTO> subcategories;
    private List<ProductDTO> products;
}
