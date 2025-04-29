package com.example.bookstore.notification;

import com.example.bookstore.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {
    private final Long orderId;
    private final String userEmail;
    private final OrderStatus newStatus;

    public OrderStatusChangedEvent(Object source, Long orderId, String userEmail, OrderStatus newStatus) {
        super(source);
        this.orderId  = orderId;
        this.userEmail = userEmail;
        this.newStatus = newStatus;
    }
}