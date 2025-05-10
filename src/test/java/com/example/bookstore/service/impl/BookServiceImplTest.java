package com.example.bookstore.service.impl;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class BookServiceImplTest {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepo, categoryRepo, reviewRepo, orderItemRepo);
    }

    @Test
    void create_shouldSaveBookAndReturnDto() {
        Category category = new Category();
        category.setName("Фентезі");
        categoryRepo.save(category);

        BookDto dto = new BookDto(
                null,
                "Назва книги",
                "Автор",
                "Цікавий опис",
                BigDecimal.valueOf(249.99),
                7,
                "Фентезі"
        );

        BookDto result = bookService.create(dto);

        assertNotNull(result);
        assertEquals("Назва книги", result.title());
        assertEquals("Фентезі", result.category());
        assertEquals(1, bookRepo.findAll().size());
    }

    @Test
    void findById_shouldReturnBookDto() {
        Category category = new Category();
        category.setName("Фентезі");
        categoryRepo.save(category);

        BookDto dto = new BookDto(
                null,
                "Назва книги",
                "Автор",
                "Опис",
                BigDecimal.valueOf(199.99),
                5,
                "Фентезі"
        );

        BookDto created = bookService.create(dto);

        BookDto found = bookService.findById(created.id());

        assertEquals(created.title(), found.title());
        assertEquals(created.category(), found.category());
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(999L));
    }

    @Test
    void update_shouldChangeBookData() {
        Category cat1 = new Category();
        cat1.setName("Фентезі");
        categoryRepo.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Детектив");
        categoryRepo.save(cat2);

        BookDto dto = new BookDto(
                null,
                "Стара назва",
                "Автор",
                "Опис",
                BigDecimal.valueOf(100),
                3,
                "Фентезі"
        );

        BookDto created = bookService.create(dto);

        BookDto updated = new BookDto(
                created.id(),
                "Нова назва",
                "Автор",
                "Новий опис",
                BigDecimal.valueOf(150),
                10,
                "Детектив"
        );

        BookDto result = bookService.update(created.id(), updated);

        assertEquals("Нова назва", result.title());
        assertEquals("Детектив", result.category());
    }

    @Test
    void update_shouldThrow_whenBookNotFound() {
        BookDto dto = new BookDto(999L, "Тест", "Автор", "Опис", BigDecimal.TEN, 1, "Фентезі");
        assertThrows(EntityNotFoundException.class, () -> bookService.update(999L, dto));
    }

    @Test
    void delete_shouldRemoveBook() {
        Category cat = new Category();
        cat.setName("Фентезі");
        categoryRepo.save(cat);

        BookDto dto = new BookDto(null, "Назва", "Автор", "Опис", BigDecimal.valueOf(100), 1, "Фентезі");
        BookDto saved = bookService.create(dto);

        assertEquals(1, bookRepo.findAll().size());

        bookService.delete(saved.id());

        assertEquals(0, bookRepo.findAll().size());
    }

    @Test
    void delete_shouldThrow_whenBookNotFound() {
        assertThrows(EntityNotFoundException.class, () -> bookService.delete(12345L));
    }
}
