package com.example.bookstore.notification;


import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final JavaMailSender mailSender;

    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(event.getUserEmail());
        msg.setSubject("Статус вашого замовлення #" + event.getOrderId());
        msg.setText("Статус замовлення було змінено на: " + event.getNewStatus());
        mailSender.send(msg);
    }
}
