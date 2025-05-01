package com.example.bookstore.dto;

public record ReviewDto(
        Long bookId,
        Long userId,
        String username,
        int rating,
        String comment
) { }
