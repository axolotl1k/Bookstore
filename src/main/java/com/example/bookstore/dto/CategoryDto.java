package com.example.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
        Long id,

        @NotBlank(message = "Назва категорії обов’язкова")
        String name
) { }
