package com.adobe.bookstore.dto;

import com.adobe.bookstore.domain.Order;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Mapper {
    public static OrderResponseDTO orderToDTO(Order order) {
        List<OrderItemResponseDTO> orderItemsDTO = order
                .getOrderItems()
                .stream()
                .map(item -> new OrderItemResponseDTO(item.getId(), item.getBookStock().getId(), item.getQuantity()))
                .collect(toList());

        return new OrderResponseDTO(order.getId(), orderItemsDTO, order.isAccepted());
    }
}
