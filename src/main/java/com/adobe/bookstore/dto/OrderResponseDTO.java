package com.adobe.bookstore.dto;

import java.util.List;

public class OrderResponseDTO {
    private Long orderId;
    private List<OrderItemResponseDTO> items;
    private boolean accepted;

    public OrderResponseDTO(Long id, List<OrderItemResponseDTO> items, boolean accepted) {
        this.orderId = id;
        this.items = items;
        this.accepted = accepted;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
