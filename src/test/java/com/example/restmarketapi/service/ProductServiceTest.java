package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.ProductResponseDto;
import com.example.restmarketapi.entity.Product;
import com.example.restmarketapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final Long PRODUCT_ONE_ID = 1L;
    private static final Long PRODUCT_TWO_ID = 2L;
    private static final String PRODUCT_ONE_TITLE = "Product 1";
    private static final String PRODUCT_TWO_TITLE = "Product 2";
    private static final int PRODUCT_ONE_AVAILABLE = 10;
    private static final int PRODUCT_TWO_AVAILABLE = 5;
    private static final int EXPECTED_SIZE = 2;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAll_ShouldReturnListOfProductResponseDtos() {
        Product product1 = new Product(PRODUCT_ONE_ID, PRODUCT_ONE_TITLE, BigDecimal.TEN, PRODUCT_ONE_AVAILABLE);
        Product product2 = new Product(PRODUCT_TWO_ID, PRODUCT_TWO_TITLE, BigDecimal.ONE, PRODUCT_TWO_AVAILABLE);
        List<Product> products = List.of(product1, product2);

        ProductResponseDto dto1 = new ProductResponseDto(PRODUCT_ONE_ID, PRODUCT_ONE_TITLE, BigDecimal.TEN, PRODUCT_ONE_AVAILABLE);
        ProductResponseDto dto2 = new ProductResponseDto(PRODUCT_TWO_ID, PRODUCT_TWO_TITLE, BigDecimal.ONE, PRODUCT_TWO_AVAILABLE);

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(product1, ProductResponseDto.class)).thenReturn(dto1);
        when(modelMapper.map(product2, ProductResponseDto.class)).thenReturn(dto2);

        List<ProductResponseDto> result = productService.getAll();

        assertNotNull(result);
        assertEquals(EXPECTED_SIZE, result.size());
        assertEquals(PRODUCT_ONE_TITLE, result.get(0).getTitle());
        assertEquals(PRODUCT_TWO_TITLE, result.get(1).getTitle());

        verify(productRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(product1, ProductResponseDto.class);
        verify(modelMapper, times(1)).map(product2, ProductResponseDto.class);
    }
}