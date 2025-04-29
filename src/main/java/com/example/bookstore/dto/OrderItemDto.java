package com.example.bookstore.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long bookId,
        String title,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {}
