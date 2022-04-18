package com.adobe.bookstore.resource;

import com.adobe.bookstore.dto.OrderItemRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/")
public class OrderResource {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // TODO impl validation
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody List<OrderItemRequestDTO> orderItems) {
        return ResponseEntity.ok(orderService.create(orderItems));
    }
}
