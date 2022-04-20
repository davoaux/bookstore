package com.adobe.bookstore.resource;

import com.adobe.bookstore.dto.BookStockResponseDTO;
import com.adobe.bookstore.service.BookStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books_stock/")
public class BookStockResource {

    private BookStockService bookStockService;

    @Autowired
    public BookStockResource(BookStockService bookStockService) {
        this.bookStockService = bookStockService;
    }

    @GetMapping("{bookId}")
    public ResponseEntity<BookStockResponseDTO> getStockById(@PathVariable String bookId) {
        return bookStockService.findById(bookId);
    }
}
