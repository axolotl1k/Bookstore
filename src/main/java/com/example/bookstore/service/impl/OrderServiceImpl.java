package com.example.bookstore.service.impl;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderItemDto;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.dto.OrderStatusResponseDto;
import com.example.bookstore.notification.OrderStatusChangedEvent;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderStatus;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CartService cartService;
    private final UserService userService;
    private final ApplicationEventPublisher evPublisher;

    @Override
    @Transactional
    public OrderStatusResponseDto createOrder(OrderDto dto) {
        User user = userService.getCurrentUser();

        Order cart = orderRepo.findByUserAndStatus(user, OrderStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("Кошик порожній або не знайдений"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Неможливо оформити порожній кошик");
        }

        cart.setFullName(dto.fullName());
        cart.setPhone(dto.phone());
        cart.setDeliveryAddress(dto.deliveryAddress());
        cart.setPaymentMethod(dto.paymentMethod());

        cart.setStatus(OrderStatus.PROCESSING);
        cart.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepo.save(cart);

        cartService.clearCart();

        evPublisher.publishEvent(new OrderStatusChangedEvent(
                this,
                saved.getId(),
                user.getEmail(),
                saved.getStatus()
        ));

        return new OrderStatusResponseDto(saved.getId(), saved.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatusResponseDto getOrderStatus(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        return new OrderStatusResponseDto(o.getId(), o.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusResponseDto> listUserOrders() {
        User user = userService.getCurrentUser();
        return orderRepo.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(o -> new OrderStatusResponseDto(o.getId(), o.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        order.setStatus(newStatus);
        orderRepo.save(order);

        evPublisher.publishEvent(new OrderStatusChangedEvent(
                this,
                orderId,
                order.getUser().getEmail(),
                newStatus
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> listAllOrders() {
        return orderRepo.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getBook().getId(),
                        i.getBook().getTitle(),
                        i.getPrice(),
                        i.getQuantity(),
                        i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                items
        );
    }
}
