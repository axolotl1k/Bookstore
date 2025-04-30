package com.example.bookstore.controller;

import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "admin/users/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        User user = userService.findAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "admin/users/form";
    }

    @PostMapping("/edit/{id}")
    public String updateRole(
            @PathVariable Long id,
            @RequestParam("role") Role role
    ) {
        userService.updateUserRole(id, role);
        return "redirect:/admin/users";
    }
}
