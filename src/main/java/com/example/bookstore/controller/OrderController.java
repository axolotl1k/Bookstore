package com.example.bookstore.controller;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderStatusResponseDto;
import com.example.bookstore.dto.CartDto;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        CartDto cart = cartService.viewCart();
        model.addAttribute("cart", cart);
        model.addAttribute("orderDto", new OrderDto("", "", "", null));
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String createOrder(@ModelAttribute("orderDto") OrderDto dto) {
        OrderStatusResponseDto status = orderService.createOrder(dto);
        return "redirect:/order/status/" + status.orderId();
    }

    @GetMapping("/status/{orderId}")
    public String orderStatus(@PathVariable Long orderId, Model model) {
        OrderStatusResponseDto status = orderService.getOrderStatus(orderId);
        model.addAttribute("status", status);
        return "order/status";
    }

    @GetMapping("/history")
    public String orderHistory(Model model) {
        List<OrderStatusResponseDto> history = orderService.listUserOrders();
        model.addAttribute("orders", history);
        return "order/history";
    }
}
