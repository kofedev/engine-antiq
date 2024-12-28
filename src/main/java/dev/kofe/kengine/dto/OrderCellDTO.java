package dev.kofe.kengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kofe.kengine.model.Product;
import lombok.Data;

@Data
public class OrderCellDTO {
    @JsonProperty("cartCellId")private Long orderCellId;
    private ProductDTO product;
    @JsonProperty("cart") private OrderDTO order;
    private Integer quantity;
}
