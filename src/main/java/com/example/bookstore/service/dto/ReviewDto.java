package com.example.bookstore.service.dto;

public record ReviewDto(
        Long bookId,
        Long userId,
        int rating,
        String comment
) { }
