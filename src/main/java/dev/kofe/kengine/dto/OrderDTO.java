package dev.kofe.kengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    @JsonProperty("cartCells") private List<OrderCellDTO> orderCells;
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
    private String note;
    private Boolean active = true;
    private String letterMessage;
}
