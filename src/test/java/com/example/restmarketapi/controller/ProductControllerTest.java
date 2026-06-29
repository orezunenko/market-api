package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.ProductResponseDto;
import com.example.restmarketapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final Long PRODUCT_ONE_ID = 1L;
    private static final Long PRODUCT_TWO_ID = 2L;
    private static final String PRODUCT_ONE_TITLE = "Product 1";
    private static final String PRODUCT_TWO_TITLE = "Product 2";
    private static final int PRODUCT_ONE_AVAILABLE = 10;
    private static final int PRODUCT_TWO_AVAILABLE = 5;
    private static final int EXPECTED_SIZE = 2;
    private static final int EXPECTED_INVOCATIONS = 1;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAll_ShouldReturnListOfProducts() {
        List<ProductResponseDto> expectedProducts = List.of(
                new ProductResponseDto(PRODUCT_ONE_ID, PRODUCT_ONE_TITLE, BigDecimal.TEN, PRODUCT_ONE_AVAILABLE),
                new ProductResponseDto(PRODUCT_TWO_ID, PRODUCT_TWO_TITLE, BigDecimal.ONE, PRODUCT_TWO_AVAILABLE)
        );
        when(productService.getAll()).thenReturn(expectedProducts);

        List<ProductResponseDto> actualProducts = productController.getAll();

        assertEquals(expectedProducts, actualProducts);
        assertEquals(EXPECTED_SIZE, actualProducts.size());
        verify(productService, times(EXPECTED_INVOCATIONS)).getAll();
    }
}