package com.example.bookstore.controller;

import com.example.bookstore.dto.CartDto;
import com.example.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;


    @GetMapping
    public String viewCart(Model model) {
        CartDto cart = cartService.viewCart();
        model.addAttribute("cart", cart);
        return "cart/list";
    }

    @PostMapping("/add/{bookId}")
    public String addToCart(@PathVariable Long bookId) {
        cartService.addToCart(bookId);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{bookId}")
    public String removeFromCart(@PathVariable Long bookId) {
        cartService.removeFromCart(bookId);
        return "redirect:/cart";
    }

    @PostMapping("/update/{bookId}")
    public String updateQuantity(@PathVariable Long bookId, @RequestParam("quantity") int quantity) {
        cartService.updateQuantity(bookId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}