package com.example.bookstore.service.impl;

import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.UserService;
import com.example.bookstore.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void register(UserDto dto) {
        if (userRepo.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already taken");
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

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user");
        }

        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
