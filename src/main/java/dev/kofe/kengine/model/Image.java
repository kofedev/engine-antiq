package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="image_id", nullable = false)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private String imageUrl;

    private boolean active;

    private boolean mainImage;

}
