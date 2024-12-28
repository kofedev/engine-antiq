package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="product_id", nullable = false)
    private Long productId;

    private Boolean active = true;

    @OneToOne
    private DescriptorSet titleSet;

    @OneToOne
    private DescriptorSet briefSet;

    @OneToOne
    private DescriptorSet fullSet;

    private String partNumber;

    private BigDecimal offerPrice;

    private int currentQuantity;

    private java.sql.Timestamp publishedOn;

    private String keyWords;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @OneToMany (mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public void addImage(Image image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void removeImage(Image image) {
        this.images.remove(image);
        image.setProduct(null);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", active='" + active + '\'' +
                '}';
    }

}
