package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.OrderDTO;
import dev.kofe.kengine.dto.OrderNoteDTO;
import dev.kofe.kengine.dto.OrderStatusDTO;
import dev.kofe.kengine.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/common")
    public OrderDTO saveNewOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        return orderService.saveNewOrder(orderDTO, clientIp);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public OrderDTO getOrderById (@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    // update note
    @PutMapping("/note/{orderId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public OrderDTO updateOrderNote (@PathVariable Long orderId, @RequestBody OrderNoteDTO orderNoteDTO) {
        return orderService.updateOrderNote(orderId, orderNoteDTO.getNote());
    }

    // update status
    @PutMapping("/status/{orderId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public OrderDTO updateOrderStatus (@PathVariable Long orderId, @RequestBody OrderStatusDTO orderStatusDTO) {
        return orderService.updateOrderActiveStatus(orderId, orderStatusDTO.getStatus());
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public void deleteOrder (@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }

    // List of orders by product
    @GetMapping("/byproduct/{productId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    List<OrderDTO> findOrdersByProductId(@PathVariable Long productId) {
        return orderService.findOrdersByProductId(productId);
    }


}
