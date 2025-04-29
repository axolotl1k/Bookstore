package com.example.bookstore.service;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.model.User;

public interface UserService {
    void register(UserDto dto);
    User getCurrentUser();
}
