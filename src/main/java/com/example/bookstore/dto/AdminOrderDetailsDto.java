package com.example.bookstore.dto;

import com.example.bookstore.model.OrderStatus;
import com.example.bookstore.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record AdminOrderDetailsDto(
        Long orderId,
        String userFullName,
        String userEmail,
        OrderStatus status,
        BigDecimal totalPrice,
        String deliveryAddress,
        String phone,
        PaymentMethod paymentMethod,
        List<OrderItemDto> items
) {}
