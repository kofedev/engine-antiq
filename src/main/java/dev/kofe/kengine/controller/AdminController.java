package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.StatDTO;
import dev.kofe.kengine.repository.OrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final OrderRepository orderRepository;

    public AdminController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/stat")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public StatDTO getStat() {
        StatDTO statDTO = new StatDTO();
        statDTO.setTotalOrders(orderRepository.findAll().size());
        statDTO.setActiveOrders(orderRepository.findAllByActiveIsTrue().size());
        return statDTO;
    }

}
