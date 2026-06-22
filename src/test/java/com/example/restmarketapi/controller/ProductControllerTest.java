package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.ProductResponseDto;
import com.example.restmarketapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAll_ShouldReturnListOfProducts() {
        List<ProductResponseDto> expectedProducts = List.of(
                new ProductResponseDto(1L, "Product 1", BigDecimal.TEN, 10),
                new ProductResponseDto(2L, "Product 2", BigDecimal.ONE, 5)
        );
        when(productService.getAll()).thenReturn(expectedProducts);

        ResponseEntity<List<ProductResponseDto>> response = productController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProducts, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).getAll();
    }
}