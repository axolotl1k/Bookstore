package com.example.bookstore.service.impl;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderStatusResponseDto;
import com.example.bookstore.model.*;
import com.example.bookstore.repository.*;
import com.example.bookstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.mail.host=example.com",
        "spring.mail.port=25",
        "spring.mail.username=test@example.com",
        "spring.mail.password=testpass",
        "spring.mail.test-connection=false",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",
        "app.email.enabled=false"
})
@Transactional
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    private User testUser;
    private Order testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("orderuser");
        testUser.setPassword("pass");
        testUser.setEmail("orderuser@example.com");
        testUser.setRole(Role.USER);
        userRepo.save(testUser);

        var auth = new TestingAuthenticationToken(testUser.getUsername(), null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var category = new Category();
        category.setName("Test Category");
        categoryRepo.save(category);

        Book book = new Book();
        book.setTitle("Some book");
        book.setAuthor("Some author");
        book.setDescription("Test");
        book.setPrice(BigDecimal.valueOf(100));
        book.setStock(10);
        book.setCategory(category);
        bookRepo.save(book);

        OrderItem item = new OrderItem();
        item.setBook(book);
        item.setPrice(book.getPrice());
        item.setQuantity(2);

        testCart = new Order();
        testCart.setUser(testUser);
        testCart.setStatus(OrderStatus.PENDING);
        testCart.setPaymentMethod(PaymentMethod.CASH);
        testCart.setItems(new ArrayList<>());
        testCart.getItems().add(item);
        orderRepo.save(testCart);

        item.setOrder(testCart);
        orderItemRepo.save(item);
    }

    @Test
    void createOrder_shouldPersistAndChangeStatus() {
        OrderDto dto = new OrderDto(
                "Ім'я Прізвище",
                "+380123456789",
                "м. Київ, вул. Шевченка 1",
                PaymentMethod.CASH
        );

        OrderStatusResponseDto result = orderService.createOrder(dto);
        Order saved = orderRepo.findById(result.orderId()).orElseThrow();

        assertEquals(OrderStatus.PROCESSING, saved.getStatus());
        assertEquals("Ім'я Прізвище", saved.getFullName());
    }

    @Test
    void getOrderStatus_shouldReturnCorrectStatus() {
        testCart.setStatus(OrderStatus.SHIPPED);
        orderRepo.save(testCart);

        var dto = orderService.getOrderStatus(testCart.getId());
        assertEquals(OrderStatus.SHIPPED, dto.status());
    }

    @Test
    void listUserOrders_shouldReturnOne() {
        testCart.setStatus(OrderStatus.DELIVERED);
        orderRepo.save(testCart);

        var list = orderService.listUserOrders();
        assertEquals(1, list.size());
        assertEquals(OrderStatus.DELIVERED, list.get(0).status());
    }

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        orderService.updateOrderStatus(testCart.getId(), OrderStatus.DELIVERED);
        var updated = orderRepo.findById(testCart.getId()).orElseThrow();
        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
    }

    @Test
    void deleteOrder_shouldRemoveOrder() {
        Long id = testCart.getId();
        orderService.deleteOrder(id);
        assertTrue(orderRepo.findById(id).isEmpty());
    }
}
