package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "order_cells")
@Data
@NoArgsConstructor
public class OrderCell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="order_cell_id", nullable = false)
    private Long orderCellId;

    @OneToOne
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    private Integer quantity;

}
