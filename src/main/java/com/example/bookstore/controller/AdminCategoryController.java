package com.example.bookstore.controller;

import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categoryDto", new CategoryDto(null, ""));
        return "admin/categories/form";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute("categoryDto") @Valid CategoryDto dto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "admin/categories/form";
        }
        categoryService.create(dto);
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
