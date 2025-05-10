package com.example.bookstore.service.impl;

import com.example.bookstore.dto.ReviewDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.repository.ReviewRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        "spring.mail.username=test@example.com",
        "spring.mail.password=test1234"
})
@Transactional
class ReviewServiceImplTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Book testBook;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("reviewer");
        testUser.setEmail("review@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory = categoryRepository.save(testCategory);

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Some Author");
        testBook.setDescription("Test Description");
        testBook.setPrice(BigDecimal.valueOf(99.99));
        testBook.setStock(10);
        testBook.setCategory(testCategory);
        testBook = bookRepository.save(testBook);

        var auth = new TestingAuthenticationToken(testUser, null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void addReview_shouldPersistReview() {
        var dto = new ReviewDto(
                testBook.getId(),
                testUser.getId(),
                testUser.getUsername(),
                4,
                "Great book!"
        );

        reviewService.addReview(dto);

        var reviews = reviewRepository.findByBookId(testBook.getId());
        assertEquals(1, reviews.size());
        assertEquals("Great book!", reviews.get(0).getComment());
    }

    @Test
    void getReviewsByBookId_shouldReturnAll() {
        var dto1 = new ReviewDto(testBook.getId(), testUser.getId(), testUser.getUsername(), 5, "Awesome");
        var dto2 = new ReviewDto(testBook.getId(), testUser.getId(), testUser.getUsername(), 3, "Good");

        reviewService.addReview(dto1);
        reviewService.addReview(dto2);

        var reviews = reviewService.getReviewsByBookId(testBook.getId());

        assertEquals(2, reviews.size());
    }

    @Test
    void getAverageRating_shouldReturnCorrectValue() {
        reviewService.addReview(new ReviewDto(testBook.getId(), testUser.getId(), testUser.getUsername(), 4, "Nice"));
        reviewService.addReview(new ReviewDto(testBook.getId(), testUser.getId(), testUser.getUsername(), 2, "Okay"));

        double avg = reviewService.getAverageRating(testBook.getId());

        assertEquals(3.0, avg, 0.01);
    }
}
