package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="order_id", nullable = false)
    private Long orderId;

    @OneToMany (mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderCell> orderCells;

    private BigDecimal total;
    private String name;
    private String phone;
    private String email;
    private java.sql.Timestamp lastVisit;
    private java.sql.Timestamp createdDate;
    private String message;

    private String ipAddress;

    private Long languageId;
    private String languageCode;
    private Boolean active = true;

    private String note;

    public void addOrderCell (OrderCell orderCell) {
        this.orderCells.add(orderCell);
        orderCell.setOrder(this);
    }

    public void removeOrderCell (OrderCell orderCell) {
        this.orderCells.remove(orderCell);
        orderCell.setOrder(null);

    }

}

