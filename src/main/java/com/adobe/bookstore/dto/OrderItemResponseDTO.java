package com.adobe.bookstore.dto;

public class OrderItemResponseDTO {
    private Long id;
    private String bookId;
    private Integer quantity;

    public OrderItemResponseDTO(Long id, String book, Integer quantity) {
        this.id = id;
        this.bookId = book;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
