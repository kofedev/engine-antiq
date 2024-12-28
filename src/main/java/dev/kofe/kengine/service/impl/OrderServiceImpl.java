package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.OrderCellDTO;
import dev.kofe.kengine.dto.OrderDTO;
import dev.kofe.kengine.mail.EmailService;
import dev.kofe.kengine.mapper.OrderMapper;
import dev.kofe.kengine.model.Order;
import dev.kofe.kengine.model.OrderCell;
import dev.kofe.kengine.model.Product;
import dev.kofe.kengine.model.Staff;
import dev.kofe.kengine.repository.OrderCellRepository;
import dev.kofe.kengine.repository.OrderRepository;
import dev.kofe.kengine.repository.ProductRepository;
import dev.kofe.kengine.repository.StaffRepository;
import dev.kofe.kengine.service.OrderService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderCellRepository orderCellRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final StaffRepository staffRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderCellRepository orderCellRepository,
                            ProductRepository productRepository,
                            EmailService emailService,
                            StaffRepository staffRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderCellRepository = orderCellRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.staffRepository = staffRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDTO saveNewOrder(OrderDTO orderDTO, String ipAddress) {
        Order order = new Order();
        order.setOrderCells(new ArrayList<>());
        order.setCreatedDate(orderDTO.getCreatedDate());
        order.setName(orderDTO.getName());
        order.setEmail(orderDTO.getEmail());
        order.setPhone(orderDTO.getPhone());
        order.setMessage(orderDTO.getMessage());
        order.setLanguageId(orderDTO.getLanguageId());
        order.setLanguageCode(orderDTO.getLanguageCode());
        order.setIpAddress(ipAddress);

        order.setActive(true);

        //@ToDo last visit and so on
        orderRepository.save(order);
        for (OrderCellDTO orderCellDTO : orderDTO.getOrderCells()) {
            OrderCell orderCell = new OrderCell();
            orderCell.setQuantity(orderCellDTO.getQuantity());
            orderCellRepository.save(orderCell);
            Product product = productRepository.findById(orderCellDTO.getProduct().getProductId()).orElse(null);
            if (product != null) {
             orderCell.setProduct(product);
            }
            order.addOrderCell(orderCell);/////
            orderRepository.save(order);
            orderCell.setOrder(order);
            orderCellRepository.save(orderCell);
        }

        // *** send email to customer
        final String MESSAGE_TO_CUSTOMER = orderDTO.getLetterMessage();
        try {
            emailService.sendEmail(orderDTO.getEmail(), "List", MESSAGE_TO_CUSTOMER);
        } catch (MessagingException e) {
//            throw new RuntimeException(e); //@ToDo
            System.out.println("Messaging exception!");
        }
        // *** send email to managers
        sendEmailsToManagersAboutNewOrder(orderDTO);
        return null; //@ToDo
    }

    private void sendEmailsToManagersAboutNewOrder (OrderDTO orderDTO) {
        final String MESSAGE_TO_MANAGER_ABOUT_NEW_ORDER = "Hello, kotiki! Thank you and so on!";
        List<Staff> staffList = staffRepository.findAllByIsReceiverMailsTrue();
        for (Staff staff : staffList) {
            try {
                emailService.sendEmail(staff.getUser().getEmail(),
                        "New order",
                        MESSAGE_TO_MANAGER_ABOUT_NEW_ORDER + " Message from customer: " + orderDTO.getMessage());
            } catch (MessagingException e) {
                System.out.println("MessagingException: " + e);
            }
        }
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        return orderMapper.fromOrder(orderRepository.findById(orderId).orElse(null));
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderMapper.fromOrderList(orderRepository.findAll());
    }

    @Override
    public OrderDTO updateOrderNote(Long orderId, String note) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        order.setNote(note);
        orderRepository.save(order);

        return orderMapper.fromOrder(order);
    }

    @Override
    public OrderDTO updateOrderActiveStatus(Long orderId, Boolean status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        order.setActive(status);
        orderRepository.save(order);

        return orderMapper.fromOrder(order);
    }

    @Override
    public void deleteOrder (Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            orderRepository.delete(order);
        }
    }

    @Override
    public List<OrderDTO> findOrdersByProductId(Long productId) {
        return orderMapper.fromOrderList(orderRepository.findByProductId(productId));
    }

}
