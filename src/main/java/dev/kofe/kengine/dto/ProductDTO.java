package dev.kofe.kengine.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long productId;
    private Boolean active;
    private List<DescriptorDTO> titleDescriptors;
    private List<DescriptorDTO> briefDescriptors;
    private List<DescriptorDTO> fullDescriptors;
    private Long fullDescriptorSetId;
    private String partNumber;
    private BigDecimal offerPrice;
    private int currentQuantity;
    private java.sql.Timestamp publishedOn;
    private String keyWords;
    private String note;
    private CategoryParentDTO category;
    private List<ImageDTO> images;
}
