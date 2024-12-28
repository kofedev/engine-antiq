package dev.kofe.kengine.dto;

import lombok.Data;

@Data
public class ImageDTO {
    private Long imageId;
    //private ProductDTO product;
    private String imageUrl;
    private boolean active;
    private boolean mainImage;
}
