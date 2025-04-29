package com.example.bookstore.config;

import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createAdmin(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.email}")   String adminEmail,
            @Value("${admin.password}") String adminRawPassword
    ) {
        return args -> {
            if (userRepo.existsByUsername(adminUsername)) {
                return;
            }

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminRawPassword));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
            System.out.printf(">>> Admin user '%s' created%n", adminUsername);
        };
    }
}
