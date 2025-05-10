package com.example.bookstore.service.impl;

import com.example.bookstore.dto.CartDto;
import com.example.bookstore.model.*;
import com.example.bookstore.repository.*;
import com.example.bookstore.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ComponentScan(
        basePackages = "com.example.bookstore",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.example.bookstore.config.DataInitializer.class
        )
)
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
class CartServiceImplTest {

    @Autowired private CartService cartService;
    @Autowired private UserRepository userRepo;
    @Autowired private BookRepository bookRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private CategoryRepository categoryRepo;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setUsername("cartuser");
        testUser.setPassword("123");
        testUser.setEmail("cart@example.com");
        testUser.setRole(Role.USER);
        userRepo.save(testUser);

        var auth = new TestingAuthenticationToken(testUser.getUsername(), null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Category category = new Category();
        category.setName("Test Cat");
        categoryRepo.save(category);

        testBook = new Book();
        testBook.setTitle("Cart Book");
        testBook.setAuthor("Author");
        testBook.setPrice(BigDecimal.valueOf(50));
        testBook.setStock(10);
        testBook.setCategory(category);
        bookRepo.save(testBook);
    }

    @Test
    void addToCart_addsBookCorrectly() {
        cartService.addToCart(testBook.getId());

        CartDto cart = cartService.viewCart();
        assertEquals(1, cart.items().size());
        assertEquals(testBook.getTitle(), cart.items().get(0).title());
    }

    @Test
    void clearCart_deletesCart() {
        cartService.addToCart(testBook.getId());
        assertFalse(orderRepo.findByUserAndStatus(testUser, OrderStatus.PENDING).isEmpty());

        cartService.clearCart();
        assertTrue(orderRepo.findByUserAndStatus(testUser, OrderStatus.PENDING).isEmpty());
    }

    @Test
    void removeFromCart_removesCorrectly() {
        cartService.addToCart(testBook.getId());
        cartService.removeFromCart(testBook.getId());

        CartDto cart = cartService.viewCart();
        assertEquals(0, cart.items().size());
    }

    @Test
    void updateQuantity_setsCorrectQuantity() {
        cartService.addToCart(testBook.getId());
        cartService.updateQuantity(testBook.getId(), 5);

        CartDto cart = cartService.viewCart();
        assertEquals(5, cart.items().get(0).quantity());
    }

    @Test
    void viewCart_returnsEmptyForNewUser() {
        CartDto cart = cartService.viewCart();
        assertEquals(0, cart.items().size());
        assertEquals(BigDecimal.ZERO, cart.total());
    }
}
