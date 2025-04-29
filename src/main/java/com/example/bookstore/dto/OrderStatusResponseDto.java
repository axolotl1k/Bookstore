package com.example.bookstore.dto;

import com.example.bookstore.model.OrderStatus;

public record OrderStatusResponseDto(
        Long orderId,
        OrderStatus status
) { }
