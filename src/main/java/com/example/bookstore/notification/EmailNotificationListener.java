package com.example.bookstore.notification;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    private final JavaMailSender mailSender;
    private final Environment env;

    public EmailNotificationListener(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @Value("${spring.mail.username}")
    private String fromAddress;

    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        boolean emailEnabled = Boolean.parseBoolean(env.getProperty("app.email.enabled", "true"));
        if (!emailEnabled) return;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(event.getUserEmail());
        msg.setSubject("Статус вашого замовлення #" + event.getOrderId());
        msg.setText("Статус замовлення було змінено на: " + event.getNewStatus());
        mailSender.send(msg);
    }
}
