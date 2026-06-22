package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.CartItemResponseDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.entity.Cart;
import com.example.restmarketapi.entity.Product;
import com.example.restmarketapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Cart cart;

    @InjectMocks
    private CartService cartService;

    private Map<Long, Integer> cartItems;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        cartItems = new LinkedHashMap<>();
        sampleProduct = new Product(1L, "Test Product", BigDecimal.TEN, 10);
    }

    @Test
    void addProductToCart_ShouldAddProduct_WhenStockIsSufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(cart.getItems()).thenReturn(cartItems);

        cartService.addProductToCart(1L, 2);

        assertEquals(2, cartItems.get(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(1L, 2));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenStockIsInsufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(cart.getItems()).thenReturn(cartItems);

        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(1L, 11));
    }

    @Test
    void getAll_ShouldReturnEmptyCart_WhenCartIsEmpty() {
        when(cart.getItems()).thenReturn(Collections.emptyMap());

        CartResponseDto result = cartService.getAll();

        assertTrue(result.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getTotalSum());
        verifyNoInteractions(productRepository);
    }

    @Test
    void getAll_ShouldReturnPopulatedCart_WhenCartHasItems() {
        cartItems.put(1L, 3);
        when(cart.getItems()).thenReturn(cartItems);
        when(productRepository.findAllById(Set.of(1L))).thenReturn(List.of(sampleProduct));

        CartResponseDto result = cartService.getAll();

        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("30"), result.getTotalSum());

        CartItemResponseDto itemDto = result.getItems().get(0);
        assertEquals("Test Product", itemDto.getTitle());
        assertEquals(3, itemDto.getQuantity());
        assertEquals(BigDecimal.TEN, itemDto.getPrice());
        assertEquals(new BigDecimal("30"), itemDto.getSubTotal());
    }

    @Test
    void removeProduct_ShouldRemoveItemFromCart() {
        when(cart.getItems()).thenReturn(cartItems);
        cartItems.put(1L, 2);

        cartService.removeProduct(1L);

        assertFalse(cartItems.containsKey(1L));
    }

    @Test
    void changeNumberOfProduct_ShouldUpdateQuantity_WhenStockIsSufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(cart.getItems()).thenReturn(cartItems);

        cartService.changeNumberOfProduct(1L, 5);

        assertEquals(5, cartItems.get(1L));
    }

    @Test
    void changeNumberOfProduct_ShouldThrowException_WhenStockIsInsufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        assertThrows(IllegalArgumentException.class, () -> cartService.changeNumberOfProduct(1L, 15));
    }
}