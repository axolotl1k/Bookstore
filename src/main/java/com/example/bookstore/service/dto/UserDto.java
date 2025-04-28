package com.example.bookstore.service.dto;

public record UserDto(
        String username,
        String password,
        String email
) { }
