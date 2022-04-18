package com.adobe.bookstore.service;

import com.adobe.bookstore.domain.BookStock;
import com.adobe.bookstore.domain.Order;
import com.adobe.bookstore.domain.OrderItem;
import com.adobe.bookstore.dto.Mapper;
import com.adobe.bookstore.dto.OrderItemRequestDTO;
import com.adobe.bookstore.dto.OrderItemResponseDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.repository.OrderItemRepository;
import com.adobe.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService {

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
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(Mapper::orderToDTO).collect(toList());
    }

    /**
     * Create a new order with the order items received after validating it
     *
     * @param orderItemsDTO
     * @return
     * @throws NoSuchElementException
     */
    @Transactional
    public Long create(List<OrderItemRequestDTO> orderItemsDTO) throws NoSuchElementException {
        Order newOrder = new Order();

        List<OrderItem> newOrderItems = new ArrayList<>();
        orderItemsDTO.forEach(itemDTO -> {
            BookStock bookStock = stockRepository.findById(itemDTO.getBook()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setOrder(newOrder);
            orderItem.setBookStock(bookStock);

            newOrderItems.add(orderItem);
            newOrder.addOrderItem(orderItem);
            bookStock.addOrderItem(orderItem);
        });
        System.out.println("newOrder = " + newOrder);
        newOrderItems.forEach(orderItem -> System.out.println("orderItem = " + orderItem));

        Order order = orderRepository.save(newOrder);
        orderItemRepository.saveAll(newOrderItems);

        return order.getId();
    }

    /**
     * Tells whether a BookOrder object is valid or not
     *
     * @param order
     * @return If order is valid or not
     */
    public boolean validateOrder(Order order) {
        // TODO impl
        return false;
    }
}
