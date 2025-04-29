package com.example.bookstore.controller;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userDto", new UserDto("", "", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("userDto") @Valid UserDto userDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(userDto);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("username", null, ex.getMessage());
            return "auth/register";
        }
    }
}
