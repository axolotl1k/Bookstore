package com.example.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class RootRedirectController {
    @GetMapping("/")
    public String redirectToCatalog() {
        return "redirect:/books";
    }
}
