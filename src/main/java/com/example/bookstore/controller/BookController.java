package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookFilterDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(
            @RequestParam(value = "title",    required = false) String title,
            @RequestParam(value = "author",   required = false) String author,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            Model model
    ) {
        BookFilterDto filter = new BookFilterDto(title, author, category, minPrice, maxPrice);
        model.addAttribute("books", bookService.findByFilter(filter));
        model.addAttribute("filter", filter);
        model.addAttribute("categories", categoryService.findAll());
        return "books/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            BookDto book = bookService.findById(id);
            model.addAttribute("book", book);
            return "books/detail";
        } catch (EntityNotFoundException ex) {
            return "error/404";
        }
    }
}
