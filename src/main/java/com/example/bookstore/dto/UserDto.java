package com.example.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        @NotBlank(message = "Ім’я користувача не може бути порожнім")
        String username,

        @NotBlank(message = "Пароль не може бути порожнім")
        String password,

        @NotBlank(message = "Email не може бути порожнім")
        @Email(message = "Невірний формат email")
        String email
) { }
