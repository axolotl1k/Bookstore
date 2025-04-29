package com.example.bookstore.repository;

import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderStatus;
import com.example.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserAndStatus(User user, OrderStatus status);
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);
}
