package com.adobe.bookstore.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "book_order")
@JsonSerialize
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.MERGE)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "accepted", nullable = false, columnDefinition = "boolean DEFAULT false")
    private boolean accepted = false;

    public Order() {
    }

    public Order(UUID id, List<OrderItem> orderItems, boolean accepted) {
        this.id = id;
        this.orderItems = orderItems;
        this.accepted = accepted;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * Adds OrderItem and synchronizes bidirectional association
     *
     * @param orderItem
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderItems=" + orderItems +
                ", accepted=" + accepted +
                '}';
    }
}
