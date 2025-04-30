package com.example.bookstore.service.impl;

import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(user.getUsername())
                .password(user.getPassword())      // уже закодований BCrypt
                .roles(user.getRole().name())
                .build();
    }
}

