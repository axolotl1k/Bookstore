package com.example.bookstore.service.impl;

import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

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
class CategoryServiceImplTest {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private BookRepository bookRepo;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepo, bookRepo);
    }

    @Test
    void findAll_shouldReturnAllCategories() {
        Category cat1 = new Category();
        cat1.setName("CatA");
        categoryRepo.save(cat1);

        Category cat2 = new Category();
        cat2.setName("CatB");
        categoryRepo.save(cat2);

        List<CategoryDto> result = categoryService.findAll();
        assertEquals(2, result.size(), "Повернуло дві категорії");
        assertTrue(
                result.stream().map(CategoryDto::name).anyMatch("CatA"::equals) &&
                        result.stream().map(CategoryDto::name).anyMatch("CatB"::equals)
        );
    }

    @Test
    void create_shouldSaveCategoryAndReturnDto() {
        CategoryDto dto = new CategoryDto(null, "NewCat");
        CategoryDto saved = categoryService.create(dto);

        assertNotNull(saved.id(), "Очікуємо згенерований ID");
        assertEquals("NewCat", saved.name());
        assertEquals(1, categoryRepo.count(), "В репозиторії має бути одна категорія");
    }

    @Test
    void delete_shouldRemoveCategoryAndReassignBooks() {
        Category toDelete = new Category();
        toDelete.setName("OldCat");
        categoryRepo.save(toDelete);

        Book book = new Book();
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setDescription("Desc");
        book.setPrice(BigDecimal.TEN);
        book.setStock(3);
        book.setCategory(toDelete);
        bookRepo.save(book);

        categoryService.delete(toDelete.getId());

        assertTrue(categoryRepo.findById(toDelete.getId()).isEmpty(),
                "Категорія має бути видалена");

        Category fallback = categoryRepo.findByName("Без категорії").orElseThrow();
        List<Book> reassigned = bookRepo.findAllByCategory(fallback);
        assertEquals(1, reassigned.size(),
                "Книга має бути перенесена у fallback-категорію");
    }
}
