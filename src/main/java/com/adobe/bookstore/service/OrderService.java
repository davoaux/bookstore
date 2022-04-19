package com.adobe.bookstore.service;

import com.adobe.bookstore.domain.BookStock;
import com.adobe.bookstore.domain.Order;
import com.adobe.bookstore.domain.OrderItem;
import com.adobe.bookstore.dto.Mapper;
import com.adobe.bookstore.dto.OrderItemRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.exception.OrderCreationException;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.repository.OrderItemRepository;
import com.adobe.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService {

    @Autowired
    private BookStockService stockService;

    private BookStockRepository stockRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(BookStockRepository stockRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Returns a list of all the orders
     *
     * @return
     */
    public List<OrderResponseDTO> getAll() {
        return orderRepository
                .findAll()
                .stream()
                .map(Mapper::orderToDTO)
                .collect(toList());
    }

    /**
     * Create a new order with the order items received after validating it. After persisting the order, the book stock
     * quantity of each item will be updated asynchronously.
     *
     * @param orderItemsDTO
     * @return
     * @throws NoSuchElementException
     * @throws OrderCreationException
     */
    public ResponseEntity<Long> create(List<OrderItemRequestDTO> orderItemsDTO) throws NoSuchElementException, OrderCreationException {
        if (orderItemsDTO.isEmpty())
            throw new OrderCreationException(HttpStatus.BAD_REQUEST, "An order must have at least 1 order item");

        Order newOrder = new Order();
        newOrder.setAccepted(true);

        List<OrderItem> newOrderItems = new ArrayList<>();

        // Set new Order and OrderItems
        for (OrderItemRequestDTO itemDTO : orderItemsDTO) {
            OrderItem orderItem = new OrderItem();
            BookStock bookStock = stockRepository
                    .findById(itemDTO.getBook())
                    .orElseThrow();
            int quantity = itemDTO.getQuantity();

            if (quantity < 1)
                throw new OrderCreationException(HttpStatus.BAD_REQUEST, "An order item must have a quantity of at least 1");

            if (bookStock.getQuantity() - quantity < 0)
                throw new OrderCreationException(HttpStatus.UNPROCESSABLE_ENTITY, "There is not enough stock for the book: " + bookStock.getId());

            orderItem.setQuantity(quantity);
            orderItem.setOrder(newOrder);
            orderItem.setBookStock(bookStock);

            newOrderItems.add(orderItem);
            newOrder.addOrderItem(orderItem);
            bookStock.addOrderItem(orderItem);
        }

        // Persist new order and it's items
        Order order = orderRepository.save(newOrder);
        orderItemRepository.saveAll(newOrderItems);

        // Updates the stock quantity asynchronously
        stockService.asyncUpdateStockQuantity(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order.getId());
    }
}
