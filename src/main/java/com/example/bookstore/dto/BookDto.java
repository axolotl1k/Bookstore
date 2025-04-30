package com.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookDto(
        Long id,
        @NotBlank(message = "Назва обов'язкова")
        String title,

        @NotBlank(message = "Автор обов'язковий")
        String author,

        String description,

        @NotNull(message = "Ціна обов'язкова")
        BigDecimal price,

        @Min(value = 0, message = "Кількість не може бути від’ємною")
        int stock,

        @NotBlank(message = "Категорія обов'язкова")
        String category
) {
}
