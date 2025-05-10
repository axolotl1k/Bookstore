package com.example.bookstore.service.impl;

import com.example.bookstore.dto.UserEditDto;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.Review;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.ReviewRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.UserService;
import com.example.bookstore.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    private final PasswordEncoder encoder;

    @Override
    public void register(UserDto dto) {
        if (userRepo.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Ім’я користувача вже зайняте");
        }
        var user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(encoder.encode(dto.password()));
        userRepo.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Немає автентифікованого користувача");
        }

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauthUser = oauthToken.getPrincipal();
            String email = oauthUser.getAttribute("email");
            if (email == null) {
                throw new UsernameNotFoundException("Користувач OAuth2 не має атрибута електронної пошти");
            }
            return userRepo.findByEmail(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("Користувача з електронною адресою " + email + " не знайдено"));
        }

        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Користувача '" + username + "' не знайдено"));
    }

    @Override
    @Transactional
    public void deleteCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Користувача не знайдено: " + username));

        List<Review> reviews = reviewRepo.findAllByUser(user);
        reviewRepo.deleteAll(reviews);

        List<Order> orders = orderRepo.findAllByUser(user);
        for (Order order : orders) {
            orderItemRepo.deleteAll(order.getItems());
        }
        orderRepo.deleteAll(orders);

        userRepo.delete(user);

        SecurityContextHolder.clearContext();
    }

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Користувача не знайдено: " + userId));
        user.setRole(newRole);
        userRepo.save(user);
    }

    @Override
    public void updateCurrentUser(UserEditDto updated) {
        User user = getCurrentUser();
        user.setUsername(updated.username());
        user.setEmail(updated.email());
        if (!updated.password().isBlank()) {
            user.setPassword(encoder.encode(updated.password()));
        }
        userRepo.save(user);
    }
}
