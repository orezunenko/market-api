package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 1L;
    private static final String SESSION_USER_ID_KEY = "userId";
    private static final String ORDER_STATUS_SUCCESS = "SUCCESS";
    private static final int EXPECTED_INVOCATIONS = 1;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void createOrder_ShouldReturnOrderResponseDto_WhenUserIsLoggedIn() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_USER_ID_KEY, USER_ID);

        OrderResponseDto expectedDto = new OrderResponseDto(ORDER_ID, BigDecimal.TEN, ORDER_STATUS_SUCCESS, LocalDateTime.now(), new ArrayList<>());
        when(orderService.createOrder(USER_ID)).thenReturn(expectedDto);

        OrderResponseDto actualDto = orderController.createOrder(session);

        assertEquals(expectedDto, actualDto);
        verify(orderService, times(EXPECTED_INVOCATIONS)).createOrder(USER_ID);
    }
}