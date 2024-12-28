package dev.kofe.kengine.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="category_id", nullable = false)
    private Long categoryId;

    private Boolean active = true;

    private Boolean root = false;

    @OneToOne
    private DescriptorSet titleSet;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent = null;

    @OneToMany (mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories = new ArrayList<>();

    @OneToMany (mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public void addSubcategory (Category subcategory) {
        this.subcategories.add(subcategory);
        subcategory.setParent(this);
    }

    public void removeSubcategory(Category category) {
        this.subcategories.remove(category);
        category.subcategories.remove(null);
    }

    public void addProduct (Product product) {
        this.products.add(product);
        product.setCategory(this);
    }

    public void removeProduct (Product product) {
        this.products.remove(product);
        product.setCategory(null);
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", active='" + active + '\'' +
                ", root='" + root + '\'' +
                '}';
    }

}
