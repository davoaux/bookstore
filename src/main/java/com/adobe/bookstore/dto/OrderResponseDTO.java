package com.adobe.bookstore.dto;

import java.util.List;
import java.util.UUID;

public class OrderResponseDTO {
    private UUID orderId;
    private List<OrderItemResponseDTO> items;
    private boolean accepted;

    public OrderResponseDTO(UUID id, List<OrderItemResponseDTO> items, boolean accepted) {
        this.orderId = id;
        this.items = items;
        this.accepted = accepted;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
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
