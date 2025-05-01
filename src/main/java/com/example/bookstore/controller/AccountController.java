package com.example.bookstore.controller;

import com.example.bookstore.dto.OrderStatusResponseDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.dto.UserEditDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public String accountHome(Model model, Authentication auth) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);

        List<OrderStatusResponseDto> history = orderService.listUserOrders();
        model.addAttribute("orders", history);

        return "account/home";
    }

    @PostMapping("/delete")
    public String deleteAccount() {
        userService.deleteCurrentUser();
        return "redirect:/login?deleted";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        User user = userService.getCurrentUser();
        UserEditDto dto = new UserEditDto(user.getUsername(), "", user.getEmail());
        model.addAttribute("userDto", dto);
        return "account/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid UserEditDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "account/edit";
        }

        userService.updateCurrentUser(userDto);
        return "redirect:/account?updated";
    }
}
