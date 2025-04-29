package com.example.bookstore.dto;

import java.math.BigDecimal;

public record BookFilterDto(
        String title,
        String author,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
