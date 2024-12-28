package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "descriptors")
@Data
@NoArgsConstructor
public class Descriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "descriptor_id", nullable = false)
    private Long descriptorId;

    @Basic
    @Column(name = "is_big")
    private Boolean isBig = false;

    @Basic
    @Column(name = "is_searchable")
    private Boolean isSearchable = true;

    @Basic
    @Column(name = "value")
    private String value = "";

    @OneToOne (cascade = CascadeType.REMOVE)
    private BigValue bigValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private DescriptorSet descriptorSet;

    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;

    @Override
    public String toString() {
        return "Descriptor{" +
                "descriptorId=" + descriptorId +
                ", isBig='" + isBig + '\'' +
                ", isSearchable='" + isSearchable + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}
