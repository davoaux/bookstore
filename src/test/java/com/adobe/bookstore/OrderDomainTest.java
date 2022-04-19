package com.adobe.bookstore;

import com.adobe.bookstore.domain.BookStock;
import com.adobe.bookstore.domain.Order;
import com.adobe.bookstore.domain.OrderItem;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.repository.OrderItemRepository;
import com.adobe.bookstore.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderDomainTest {

    private BookStockRepository bookStockRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    @Autowired
    public OrderDomainTest(BookStockRepository bookStockRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.bookStockRepository = bookStockRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Test
    @Transactional
    @Sql(statements = "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67890', 'some book', 7);"
            + "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67891', 'some bad book', 3);"
            + "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67892', 'some good book', 10);")
    public void associationShouldBeBidirectional() {
        List<BookStock> books = bookStockRepository.findAllById(List.of("12345-67890", "12345-67891", "12345-67892"));
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>(List.of(
                new OrderItem(2L, order, books.get(0), 2),
                new OrderItem(3L, order, books.get(1), 1),
                new OrderItem(4L, order, books.get(2), 5)
        ));
        // See if we can make something better: https://stackoverflow.com/a/30474303
        for (int i = 0; i < books.size(); i++) {
            order.addOrderItem(orderItems.get(i));
            books.get(i).addOrderItem(orderItems.get(i));
        }

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        OrderItem firstOrderItem = orderItems.get(0);
        BookStock firstBookStock = books.get(0);

        assertThat(order.getOrderItems()).hasSize(3);
        assertThat(firstOrderItem.getOrder()).isEqualTo(order);
        assertThat(firstOrderItem.getBookStock()).isEqualTo(firstBookStock);
        assertThat(firstBookStock.getOrderItems()).hasSize(1);
    }
}
