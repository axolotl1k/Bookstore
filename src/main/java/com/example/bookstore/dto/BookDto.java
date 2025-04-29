package com.example.bookstore.dto;

import java.math.BigDecimal;

public record BookDto(
        Long id,
        String title,
        String author,
        String description,
        BigDecimal price,
        Integer stock,
        String category
) {
}
