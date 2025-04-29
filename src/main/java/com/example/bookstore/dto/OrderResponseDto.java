package com.example.bookstore.dto;

import com.example.bookstore.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDto(
        Long orderId,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderItemDto> items
) {}
