package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.CartItemRequestDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private static final Long PRODUCT_ID = 1L;
    private static final int QUANTITY_TWO = 2;
    private static final int QUANTITY_FIVE = 5;
    private static final int EXPECTED_INVOCATIONS = 1;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void getAll_ShouldReturnCartResponseDto() {
        CartResponseDto expectedDto = new CartResponseDto(new ArrayList<>(), BigDecimal.ZERO);
        when(cartService.getAll()).thenReturn(expectedDto);

        CartResponseDto actualDto = cartController.getAll();

        assertEquals(expectedDto, actualDto);
        verify(cartService, times(EXPECTED_INVOCATIONS)).getAll();
    }

    @Test
    void addProductToCart_ShouldReturnOk_WhenRequestIsValid() {
        CartItemRequestDto dto = new CartItemRequestDto(PRODUCT_ID, QUANTITY_TWO);

        cartController.addProductToCart(dto);

        verify(cartService, times(EXPECTED_INVOCATIONS)).addProductToCart(PRODUCT_ID, QUANTITY_TWO);
    }

    @Test
    void changeNumberOfProduct_ShouldReturnOk_WhenRequestIsValid() {
        CartItemRequestDto dto = new CartItemRequestDto(PRODUCT_ID, QUANTITY_FIVE);

        cartController.changeNumberOfProduct(dto);

        verify(cartService, times(EXPECTED_INVOCATIONS)).changeNumberOfProduct(PRODUCT_ID, QUANTITY_FIVE);
    }

    @Test
    void delete_ShouldReturnOk_WhenIdIsValid() {
        cartController.delete(PRODUCT_ID);

        verify(cartService, times(EXPECTED_INVOCATIONS)).removeProduct(PRODUCT_ID);
    }
}