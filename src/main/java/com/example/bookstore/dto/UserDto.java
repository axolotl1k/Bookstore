package com.example.bookstore.dto;

public record UserDto(
        String username,
        String password,
        String email
) { }
