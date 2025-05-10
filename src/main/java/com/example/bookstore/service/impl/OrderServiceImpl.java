package com.example.bookstore.service.impl;

import com.example.bookstore.dto.*;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.notification.OrderStatusChangedEvent;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderStatus;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepo;
    private final CartService cartService;
    private final UserService userService;
    private final ApplicationEventPublisher evPublisher;

    @Override
    @Transactional
    public OrderStatusResponseDto createOrder(OrderDto dto) {
        User user = userService.getCurrentUser();

        Order cart = orderRepo.findByUserAndStatus(user, OrderStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("Кошик порожній або не знайдений"));

        for (var item : cart.getItems()) {
            if (item.getBook() == null) {
                throw new IllegalStateException("У кошику є книга, яка була видалена");
            }

            int inStock = item.getBook().getStock();
            int toBuy = item.getQuantity();

            if (inStock < toBuy) {
                throw new IllegalStateException("Недостатньо книг: " + item.getBook().getTitle());
            }

            item.getBook().setStock(inStock - toBuy);
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
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Замовлення не знайдено: " + orderId));
        return new OrderStatusResponseDto(order.getId(), order.getStatus());
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
                .orElseThrow(() -> new EntityNotFoundException("Замовлення не знайдено: " + orderId));
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
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Замовлення не знайдено: " + orderId));

        List<OrderItem> orderItems = orderItemRepo.findByOrder(order);
        for (OrderItem item : orderItems) {
            item.setBook(null);
        }
        orderRepo.delete(order);
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
                        i.getBook() != null ? i.getBook().getId() : null,
                        i.getBook() != null ? i.getBook().getTitle() : "[Книгу видалено]",
                        i.getPrice(),
                        i.getQuantity(),
                        i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getUser().getUsername(),
                order.getStatus(),
                order.getTotalPrice(),
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderDetailsDto getAdminOrderDetails(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Замовлення не знайдено: " + orderId));

        List<OrderItemDto> items = order.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getBook() != null ? i.getBook().getId() : null,
                        i.getBook() != null ? i.getBook().getTitle() : "[Книгу видалено]",
                        i.getPrice(),
                        i.getQuantity(),
                        i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                ))
                .toList();

        return new AdminOrderDetailsDto(
                order.getId(),
                order.getUser().getUsername(),
                order.getUser().getEmail(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getDeliveryAddress(),
                order.getPhone(),
                order.getPaymentMethod(),
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getUserOrderDetails(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Замовлення не знайдено: " + orderId));
        return mapToResponseDto(order);
    }
}
