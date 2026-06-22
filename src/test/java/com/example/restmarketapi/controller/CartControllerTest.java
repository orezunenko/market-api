package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.CartItemRequestDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void getAll_ShouldReturnCartResponseDto() {
        CartResponseDto expectedDto = new CartResponseDto(new ArrayList<>(), BigDecimal.ZERO);
        when(cartService.getAll()).thenReturn(expectedDto);

        ResponseEntity<CartResponseDto> response = cartController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(cartService, times(1)).getAll();
    }

    @Test
    void addProductToCart_ShouldReturnOk_WhenRequestIsValid() {
        CartItemRequestDto dto = new CartItemRequestDto(1L, 2);

        ResponseEntity<String> response = cartController.addProductToCart(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("You successfully add product to cart", response.getBody());
        verify(cartService, times(1)).addProductToCart(1L, 2);
    }

    @Test
    void changeNumberOfProduct_ShouldReturnOk_WhenRequestIsValid() {
        CartItemRequestDto dto = new CartItemRequestDto(1L, 5);

        ResponseEntity<String> response = cartController.changeNumberOfProduct(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Number of product successfully changed to: 5", response.getBody());
        verify(cartService, times(1)).changeNumberOfProduct(1L, 5);
    }

    @Test
    void delete_ShouldReturnOk_WhenIdIsValid() {
        Long productId = 1L;

        ResponseEntity<String> response = cartController.delete(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product successfully deleted from cart", response.getBody());
        verify(cartService, times(1)).removeProduct(productId);
    }
}