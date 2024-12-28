package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class UiBigElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long uiBigElementId;

    private int key;

    @OneToOne
    private DescriptorSet valueSet;

}
