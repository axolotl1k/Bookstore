package com.example.bookstore.service.impl;

import com.example.bookstore.dto.CartDto;
import com.example.bookstore.dto.CartItemDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.OrderStatus;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final BookRepository bookRepo;
    private final UserService userService;

    private Order getOrCreateCartOrder(User user) {
        return orderRepo.findByUserAndStatus(user, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order o = new Order();
                    o.setUser(user);
                    o.setStatus(OrderStatus.PENDING);
                    o.setTotalPrice(BigDecimal.ZERO);
                    return orderRepo.save(o);
                });
    }

    @Override
    @Transactional
    public void addToCart(Long bookId) {
        User user = userService.getCurrentUser();
        Order cart = getOrCreateCartOrder(user);

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книгу не знайдено: " + bookId));

        OrderItem item = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElseGet(() -> {
                    OrderItem oi = new OrderItem();
                    oi.setOrder(cart);
                    oi.setBook(book);
                    oi.setPrice(book.getPrice());
                    oi.setQuantity(0);
                    cart.getItems().add(oi);
                    return oi;
                });

        item.setQuantity(item.getQuantity() + 1);
        orderItemRepo.save(item);

        recalcTotal(cart);
        orderRepo.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto viewCart() {
        User user = userService.getCurrentUser();
        return orderRepo
                .findByUserAndStatus(user, OrderStatus.PENDING)
                .map(this::mapToDto)
                .orElseGet(() -> new CartDto(List.of(), BigDecimal.ZERO));
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = userService.getCurrentUser();
        orderRepo.findByUserAndStatus(user, OrderStatus.PENDING).ifPresent(cart -> {
            orderItemRepo.deleteAll(cart.getItems());
            orderRepo.delete(cart);
        });
    }

    @Override
    @Transactional
    public void removeFromCart(Long bookId) {
        User user = userService.getCurrentUser();
        orderRepo.findByUserAndStatus(user, OrderStatus.PENDING).ifPresent(cart -> {
            cart.getItems().stream()
                    .filter(i -> i.getBook().getId().equals(bookId))
                    .findFirst()
                    .ifPresent(item -> {
                        cart.getItems().remove(item);
                        orderItemRepo.delete(item);

                        if (cart.getItems().isEmpty()) {
                            orderRepo.delete(cart);
                        } else {
                            recalcTotal(cart);
                            orderRepo.save(cart);
                        }
                    });
        });
    }

    @Override
    @Transactional
    public void updateQuantity(Long bookId, int quantity) {
        User user = userService.getCurrentUser();
        orderRepo.findByUserAndStatus(user, OrderStatus.PENDING).ifPresent(cart -> {
            cart.getItems().stream()
                    .filter(i -> i.getBook().getId().equals(bookId))
                    .findFirst()
                    .ifPresent(item -> {
                        if (quantity <= 0) {
                            cart.getItems().remove(item);
                            orderItemRepo.delete(item);
                        } else {
                            item.setQuantity(quantity);
                            orderItemRepo.save(item);
                        }

                        if (cart.getItems().isEmpty()) {
                            orderRepo.delete(cart);
                        } else {
                            recalcTotal(cart);
                            orderRepo.save(cart);
                        }
                    });
        });
    }

    private void recalcTotal(Order cart) {
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    private CartDto mapToDto(Order cart) {
        return new CartDto(
                cart.getItems().stream()
                        .map(i -> new CartItemDto(
                                i.getBook().getId(),
                                i.getBook().getTitle(),
                                i.getPrice(),
                                i.getQuantity(),
                                i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                        ))
                        .collect(Collectors.toList()),
                cart.getTotalPrice()
        );
    }
}
