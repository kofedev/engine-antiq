package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.OrderCellDTO;
import dev.kofe.kengine.dto.OrderDTO;
import dev.kofe.kengine.model.Order;
import dev.kofe.kengine.model.OrderCell;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderMapper {

    ProductMapper productMapper;
    public OrderMapper (ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public OrderDTO fromOrder(Order order) {
        if (order == null) return null;
        OrderDTO orderDTO = new OrderDTO();
        List<OrderCellDTO> orderCellDTOS = new ArrayList<>();
        BeanUtils.copyProperties(order, orderDTO);
        for (OrderCell orderCell : order.getOrderCells()) {
            OrderCellDTO orderCellDTO = new OrderCellDTO();
            orderCellDTO.setOrderCellId(orderCell.getOrderCellId());
            orderCellDTO.setQuantity(orderCell.getQuantity());
            orderCellDTO.setProduct(productMapper.fromProduct(orderCell.getProduct(), false));
            orderCellDTOS.add(orderCellDTO);
        }
        orderDTO.setOrderCells(orderCellDTOS);

        return orderDTO;
    }

    public List<OrderDTO> fromOrderList(List<Order> orderList) {
        if (orderList == null) return null;
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order : orderList) {
            orderDTOList.add(fromOrder(order));
        }

        return orderDTOList;
    }

}
