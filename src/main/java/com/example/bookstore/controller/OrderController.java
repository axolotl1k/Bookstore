package com.example.bookstore.web;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderStatusResponseDto;
import com.example.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderStatusResponseDto> createOrder(@RequestBody OrderDto dto) {
        OrderStatusResponseDto status = orderService.createOrder(dto);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusResponseDto> getOrderStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderStatusResponseDto>> listMyOrders() {
        return ResponseEntity.ok(orderService.listUserOrders());
    }
}
