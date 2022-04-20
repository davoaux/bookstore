package com.adobe.bookstore.service;

import com.adobe.bookstore.domain.BookStock;
import com.adobe.bookstore.domain.Order;
import com.adobe.bookstore.dto.BookStockResponseDTO;
import com.adobe.bookstore.dto.Mapper;
import com.adobe.bookstore.repository.BookStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BookStockService {

    private BookStockRepository bookStockRepository;

    @Autowired
    public BookStockService(BookStockRepository bookStockRepository) {
        this.bookStockRepository = bookStockRepository;
    }

    /**
     * Find BookStock by ID and return it in a ResponseEntity
     *
     * @param bookId
     * @return
     */
    public ResponseEntity<BookStockResponseDTO> findById(String bookId) {
        return bookStockRepository.findById(bookId)
                .map(bookStock -> ResponseEntity.ok(Mapper.bookStockToDTO(bookStock)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update's the BookStock quantity of a processed order
     *
     * @param order
     */
    @Async
    public void asyncUpdateStockQuantity(Order order) {
        // Instead of simply returning if the order is not accepted, or if the stock quantity has changed and updating
        // it would set it to a negative number, we should at least log it
        if (!order.isAccepted()) return;

        order.getOrderItems().forEach(item -> {
            BookStock bookStock = item.getBookStock();
            int newQuantity = bookStock.getQuantity() - item.getQuantity();
            if (newQuantity < 0) return;
            bookStock.setQuantity(newQuantity);

            bookStockRepository.save(bookStock);
        });
    }
}
