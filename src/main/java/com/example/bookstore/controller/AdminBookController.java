package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookFilterDto;
import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @ModelAttribute("categories")
    public List<CategoryDto> categories() {
        return categoryService.findAll();
    }

    @GetMapping
    public String list(Model model) {
        var books = bookService.findByFilter(new BookFilterDto(null, null, null, null, null));
        model.addAttribute("books", books);
        return "admin/books/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("bookDto", new BookDto(null, "", "", "", null, 0, ""));
        return "admin/books/form";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute("bookDto") @Valid BookDto dto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "admin/books/form";
        }
        bookService.create(dto);
        return "redirect:/admin/books";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("bookDto", bookService.findById(id));
        return "admin/books/form";
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("bookDto") @Valid BookDto dto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "admin/books/form";
        }
        bookService.update(id, dto);
        return "redirect:/admin/books";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/admin/books";
    }
}

