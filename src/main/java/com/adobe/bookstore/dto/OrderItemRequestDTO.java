package com.adobe.bookstore.dto;

public class OrderItemRequestDTO {
    private String book;
    private int quantity;

    public OrderItemRequestDTO(String book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItemRequestDTO{" +
                "book='" + book + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
