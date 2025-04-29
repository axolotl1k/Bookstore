package com.example.bookstore.dto;

import com.example.bookstore.model.PaymentMethod;

public record OrderDto(
        String fullName,
        String phone,
        String deliveryAddress,
        PaymentMethod paymentMethod
) { }
