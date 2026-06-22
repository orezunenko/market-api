package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void createOrder_ShouldReturnUnauthorized_WhenUserIsNotLoggedIn() {
        MockHttpSession session = new MockHttpSession();

        ResponseEntity<?> response = orderController.createOrder(session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User is not authenticated. Please log in first.", response.getBody());
        verifyNoInteractions(orderService);
    }

    @Test
    void createOrder_ShouldReturnOk_WhenUserIsLoggedIn() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        OrderResponseDto expectedDto = new OrderResponseDto(1L, BigDecimal.TEN, "SUCCESS", LocalDateTime.now(), new ArrayList<>());
        when(orderService.createOrder(1L)).thenReturn(expectedDto);

        ResponseEntity<?> response = orderController.createOrder(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(orderService, times(1)).createOrder(1L);
    }
}