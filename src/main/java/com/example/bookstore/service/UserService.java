package com.example.bookstore.service;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;

import java.util.List;

public interface UserService {
    void register(UserDto dto);
    User getCurrentUser();
    void deleteCurrentUser();
    List<User> findAllUsers();
    void updateUserRole(Long userId, Role newRole);
}
