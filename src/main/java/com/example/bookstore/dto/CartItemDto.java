package com.example.bookstore.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long bookId,
        String title,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) { }
