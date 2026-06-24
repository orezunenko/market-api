package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Checkout and order processing")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order", description = "Places an order using the current items in the user's cart.")
    public OrderResponseDto createOrder(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return orderService.createOrder(userId);
    }
}