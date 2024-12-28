package dev.kofe.kengine.dto;
import dev.kofe.kengine.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageDTO {
    int currentPage;
    int totalPages;
    List<ProductDTO> products;
    CategoryDTO category;
}
