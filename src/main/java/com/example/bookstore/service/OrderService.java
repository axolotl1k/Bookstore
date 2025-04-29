package com.example.bookstore.service;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.dto.OrderStatusResponseDto;
import com.example.bookstore.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderStatusResponseDto createOrder(OrderDto dto);
    OrderStatusResponseDto getOrderStatus(Long orderId);
    List<OrderStatusResponseDto> listUserOrders();
    List<OrderResponseDto> listAllOrders();
    void updateOrderStatus(Long orderId, OrderStatus newStatus);
}
