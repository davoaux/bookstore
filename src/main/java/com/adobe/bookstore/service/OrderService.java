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

import java.util.*;

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
    public ResponseEntity<UUID> create(List<OrderItemRequestDTO> orderItemsDTO) throws NoSuchElementException, OrderCreationException {
        if (orderItemsDTO.isEmpty())
            throw new OrderCreationException(HttpStatus.BAD_REQUEST, "An order must have at least 1 order item");

        Order newOrder = new Order();
        newOrder.setAccepted(true);

        List<OrderItem> newOrderItems = new ArrayList<>();

        // Assuming an order can have multiple order items for the same book, we'll use this map to keep track of the
        // book stock and check if there is enough to process the order
        Map<String, Integer> quantityByBookStock = new HashMap<>();

        // Set new Order and OrderItems
        for (OrderItemRequestDTO itemDTO : orderItemsDTO) {
            OrderItem orderItem = new OrderItem();
            BookStock bookStock = stockRepository
                    .findById(itemDTO.getBook())
                    .orElseThrow();

            String bookStockId = bookStock.getId();
            int quantity = itemDTO.getQuantity();

            if (quantity < 1)
                throw new OrderCreationException(HttpStatus.BAD_REQUEST, "An order item must have a quantity of at least 1");

            // Update map of book stock quantities
            if (quantityByBookStock.containsKey(bookStockId)) {
                int prevQuantity = quantityByBookStock.get(bookStockId);
                quantityByBookStock.put(bookStockId, prevQuantity + quantity);
            } else {
                quantityByBookStock.put(bookStockId, quantity);
            }

            // Check if there is enough stock for the current book
            if (bookStock.getQuantity() - quantityByBookStock.get(bookStockId) < 0)
                throw new OrderCreationException(HttpStatus.UNPROCESSABLE_ENTITY, "There is not enough stock for the book: " + bookStockId);

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
