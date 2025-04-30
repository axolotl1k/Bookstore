package com.example.bookstore.service.impl;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.BookService;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookFilterDto;
import com.example.bookstore.specification.BookSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepo;
    private final CategoryRepository categoryRepo;

    public List<BookDto> findByFilter(BookFilterDto f) {
        Specification<Book> spec = Specification.where(null);

        if (f.title() != null && !f.title().isBlank()) {
            spec = spec.and(BookSpecifications.hasTitle(f.title()));
        }
        if (f.author() != null && !f.author().isBlank()) {
            spec = spec.and(BookSpecifications.hasAuthor(f.author()));
        }
        if (f.category() != null && !f.category().isBlank()) {
            spec = spec.and(BookSpecifications.inCategory(f.category()));
        }
        if (f.minPrice() != null) {
            spec = spec.and(BookSpecifications.priceGreaterOrEqual(f.minPrice()));
        }
        if (f.maxPrice() != null) {
            spec = spec.and(BookSpecifications.priceLessOrEqual(f.maxPrice()));
        }

        List<Book> books = bookRepo.findAll(spec);
        return books.stream().map(this::toDto).toList();
    }

    @Override
    public BookDto findById(Long bookId) {
        Book b = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return toDto(b);
    }

    private BookDto toDto(Book b) {
        return new BookDto(
                b.getId(),
                b.getTitle(),
                b.getAuthor(),
                b.getDescription(),
                b.getPrice(),
                b.getStock(),
                b.getCategory().getName()
        );
    }

    @Override
    public BookDto create(BookDto dto) {
        String categoryName = dto.category();
        Category cat = categoryRepo.findByName(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.category()));

        Book b = Book.builder()
                .title(dto.title())
                .author(dto.author())
                .description(dto.description())
                .price(dto.price())
                .stock(dto.stock())
                .category(cat)
                .build();

        Book saved = bookRepo.save(b);
        return toDto(saved);
    }

    @Override
    public BookDto update(Long bookId, BookDto dto) {
        Book b = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        if (!b.getCategory().getName().equals(dto.category())) {
            Category cat = categoryRepo.findByName(dto.category())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.category()));
            b.setCategory(cat);
        }

        b.setTitle(dto.title());
        b.setAuthor(dto.author());
        b.setDescription(dto.description());
        b.setPrice(dto.price());
        b.setStock(dto.stock());

        Book updated = bookRepo.save(b);
        return toDto(updated);
    }

    @Override
    public void delete(Long bookId) {
        if (!bookRepo.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found: " + bookId);
        }
        bookRepo.deleteById(bookId);
    }
}
