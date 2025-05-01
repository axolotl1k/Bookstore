package com.example.bookstore.controller;

import com.example.bookstore.dto.AdminOrderDetailsDto;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.model.OrderStatus;
import com.example.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String list(Model model) {
        List<OrderResponseDto> orders = orderService.listAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/list";
    }

    @PostMapping("/status/{orderId}")
    public String changeStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus
    ) {
        orderService.updateOrderStatus(orderId, newStatus);
        return "redirect:/admin/orders";
    }

    @GetMapping("/details/{orderId}")
    public String orderDetails(@PathVariable Long orderId, Model model) {
        AdminOrderDetailsDto dto = orderService.getAdminOrderDetails(orderId);
        model.addAttribute("order", dto);
        return "admin/orders/details";
    }

    @PostMapping("/delete/{orderId}")
    public String deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/admin/orders";
    }
}

