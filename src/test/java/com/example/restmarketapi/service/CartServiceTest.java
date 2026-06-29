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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_TITLE = "Test Product";
    private static final int INITIAL_STOCK = 10;
    private static final int QUANTITY_TWO = 2;
    private static final int QUANTITY_THREE = 3;
    private static final int QUANTITY_FIVE = 5;
    private static final int INSUFFICIENT_STOCK_ADD = 11;
    private static final int INSUFFICIENT_STOCK_CHANGE = 15;
    private static final int EXPECTED_ITEMS_SIZE = 1;

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
        sampleProduct = new Product(PRODUCT_ID, PRODUCT_TITLE, BigDecimal.TEN, INITIAL_STOCK);
    }

    @Test
    void addProductToCart_ShouldAddProduct_WhenStockIsSufficient() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(sampleProduct));
        when(cart.getItems()).thenReturn(cartItems);

        cartService.addProductToCart(PRODUCT_ID, QUANTITY_TWO);

        assertEquals(QUANTITY_TWO, cartItems.get(PRODUCT_ID));
        verify(productRepository, times(1)).findById(PRODUCT_ID);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(PRODUCT_ID, QUANTITY_TWO));
        verify(productRepository, times(1)).findById(PRODUCT_ID);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenStockIsInsufficient() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(sampleProduct));
        when(cart.getItems()).thenReturn(cartItems);

        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(PRODUCT_ID, INSUFFICIENT_STOCK_ADD));
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
        cartItems.put(PRODUCT_ID, QUANTITY_THREE);
        when(cart.getItems()).thenReturn(cartItems);
        when(productRepository.findAllById(Set.of(PRODUCT_ID))).thenReturn(List.of(sampleProduct));

        CartResponseDto result = cartService.getAll();

        assertEquals(EXPECTED_ITEMS_SIZE, result.getItems().size());
        assertEquals(new BigDecimal("30"), result.getTotalSum());

        CartItemResponseDto itemDto = result.getItems().get(0);
        assertEquals(PRODUCT_TITLE, itemDto.getTitle());
        assertEquals(QUANTITY_THREE, itemDto.getQuantity());
        assertEquals(BigDecimal.TEN, itemDto.getPrice());
        assertEquals(new BigDecimal("30"), itemDto.getSubTotal());
    }

    @Test
    void removeProduct_ShouldRemoveItemFromCart() {
        when(cart.getItems()).thenReturn(cartItems);
        cartItems.put(PRODUCT_ID, QUANTITY_TWO);

        cartService.removeProduct(PRODUCT_ID);

        assertFalse(cartItems.containsKey(PRODUCT_ID));
    }

    @Test
    void changeNumberOfProduct_ShouldUpdateQuantity_WhenStockIsSufficient() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(sampleProduct));
        when(cart.getItems()).thenReturn(cartItems);

        cartService.changeNumberOfProduct(PRODUCT_ID, QUANTITY_FIVE);

        assertEquals(QUANTITY_FIVE, cartItems.get(PRODUCT_ID));
    }

    @Test
    void changeNumberOfProduct_ShouldThrowException_WhenStockIsInsufficient() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(sampleProduct));

        assertThrows(IllegalArgumentException.class, () -> cartService.changeNumberOfProduct(PRODUCT_ID, INSUFFICIENT_STOCK_CHANGE));
    }
}