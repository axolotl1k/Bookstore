package com.example.bookstore.service;

import com.example.bookstore.dto.CartDto;

public interface CartService {
    void addToCart(Long bookId);
    CartDto viewCart();
    void clearCart();
    void removeFromCart(Long bookId);
    void updateQuantity(Long bookId, int quantity);
}
