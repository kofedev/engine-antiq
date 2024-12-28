package dev.kofe.kengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFullDTO {
    ProductDTO product;
    List<OrderDTO> orders;
    CategoryDTO category;
}
