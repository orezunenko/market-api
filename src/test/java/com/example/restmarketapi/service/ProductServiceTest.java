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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAll_ShouldReturnListOfProductResponseDtos() {
        Product product1 = new Product(1L, "Product 1", BigDecimal.TEN, 10);
        Product product2 = new Product(2L, "Product 2", BigDecimal.ONE, 5);
        List<Product> products = List.of(product1, product2);

        ProductResponseDto dto1 = new ProductResponseDto(1L, "Product 1", BigDecimal.TEN, 10);
        ProductResponseDto dto2 = new ProductResponseDto(2L, "Product 2", BigDecimal.ONE, 5);

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(product1, ProductResponseDto.class)).thenReturn(dto1);
        when(modelMapper.map(product2, ProductResponseDto.class)).thenReturn(dto2);

        List<ProductResponseDto> result = productService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getTitle());
        assertEquals("Product 2", result.get(1).getTitle());

        verify(productRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(product1, ProductResponseDto.class);
        verify(modelMapper, times(1)).map(product2, ProductResponseDto.class);
    }
}