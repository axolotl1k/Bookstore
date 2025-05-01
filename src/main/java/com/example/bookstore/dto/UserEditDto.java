package com.example.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEditDto(
        @NotBlank(message = "Ім’я користувача не може бути порожнім")
        String username,

        String password,

        @Email(message = "Невірний формат email")
        @NotBlank(message = "Email не може бути порожнім")
        String email
) {}
