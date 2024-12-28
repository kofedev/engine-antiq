package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "big_value")
@Data
@NoArgsConstructor
public class BigValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "big_value_id", nullable = false)
    private Long bigValueId;

    @Lob
    @Column
    private String value;

    @OneToOne (mappedBy = "bigValue")
    private Descriptor descriptor;
}
