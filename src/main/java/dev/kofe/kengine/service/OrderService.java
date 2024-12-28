package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO saveNewOrder(OrderDTO orderDTO, String ipAddress);
    OrderDTO getOrderById (Long orderId);
    List<OrderDTO> getAllOrders();
    OrderDTO updateOrderNote(Long orderId, String note);
    OrderDTO updateOrderActiveStatus(Long orderId, Boolean status);
    void deleteOrder (Long orderId);
    List<OrderDTO> findOrdersByProductId(Long productId);
}
