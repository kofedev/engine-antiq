package dev.kofe.kengine.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "descriptor_sets")
@Data
@NoArgsConstructor
public class DescriptorSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "descriptor_set_id", nullable = false)
    private Long descriptorSetId;

    @OneToMany (mappedBy = "descriptorSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Descriptor> descriptors = new ArrayList<>();

    public void addDescriptor(Descriptor descriptor) {
        this.descriptors.add(descriptor);
        descriptor.setDescriptorSet(this);
    }

    public void removeDescriptor(Descriptor descriptor) {
        this.descriptors.remove(descriptor);
        descriptor.setDescriptorSet(null);
    }

    @Override
    public String toString() {
        return "DescriptorSet{" +
                "descriptorSetId=" + descriptorSetId +
                '}';
    }

}
